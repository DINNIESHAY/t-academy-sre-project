const api = (() => {
    async function request(url, options = {}) {
        const response = await fetch(url, options);
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({message: 'Неизвестная ошибка'}));
            throw {status: response.status, data: errorData};
        }
        const text = await response.text();
        if (!text) return null;
        return JSON.parse(text);
    }

    return {
        get: (url) => request(url),
        post: (url, body) => request(url, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(body)
        }),
        put: (url, body) => request(url, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(body)
        }),
        delete: (url) => request(url, {method: 'DELETE'}),
        postRaw: (url, options) => request(url, {method: 'POST', ...options}),
    };
})();
