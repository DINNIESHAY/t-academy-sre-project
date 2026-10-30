import os
import threading
import time
import urllib.error
import urllib.request
from http.server import BaseHTTPRequestHandler, HTTPServer

TARGET_URL = os.environ.get("TARGET_URL", "http://app/")
INTERVAL = float(os.environ.get("PROBE_INTERVAL_SECONDS", "5"))
TIMEOUT = float(os.environ.get("PROBE_TIMEOUT_SECONDS", "3"))
SLOW_THRESHOLD = float(os.environ.get("PROBE_SLOW_SECONDS", "1"))
PORT = int(os.environ.get("METRICS_PORT", "9100"))

lock = threading.Lock()
state = {"attempts": 0, "failures": 0, "slow": 0}

def probe_once():
    start = time.monotonic()
    try:
        with urllib.request.urlopen(TARGET_URL, timeout=TIMEOUT) as response:
            response.read()
            status = response.status
    except urllib.error.HTTPError as error:
        status = error.code
    except Exception:
        status = 0
    duration = time.monotonic() - start
    with lock:
        state["attempts"] += 1
        state["failures"] += 0 if status == 200 else 1
        state["slow"] += 1 if duration > SLOW_THRESHOLD else 0

def probe_loop():
    while True:
        started = time.monotonic()
        probe_once()
        time.sleep(max(0.0, INTERVAL - (time.monotonic() - started)))

def render_metrics():
    with lock:
        s = dict(state)
    return (
        "# HELP probe_attempts_total Total number of probe attempts.\n"
        "# TYPE probe_attempts_total counter\n"
        f"probe_attempts_total {s['attempts']}\n"
        "# HELP probe_failures_total Probes that got a non-200 status or failed to connect.\n"
        "# TYPE probe_failures_total counter\n"
        f"probe_failures_total {s['failures']}\n"
        "# HELP probe_slow_total Probes slower than the slow threshold.\n"
        "# TYPE probe_slow_total counter\n"
        f"probe_slow_total {s['slow']}\n"
    )

class MetricsHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        body = render_metrics().encode()
        self.send_response(200)
        self.send_header("Content-Type", "text/plain; version=0.0.4; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)

if __name__ == "__main__":
    threading.Thread(target=probe_loop, daemon=True).start()
    HTTPServer(("0.0.0.0", PORT), MetricsHandler).serve_forever()
