document.addEventListener('DOMContentLoaded', () => {
    initFeed();
    initApplicationModal();
});

function initFeed() {
    const container = document.getElementById('tripsContainer');
    const state = {currentPage: 0, totalPages: 0, loading: false};
    let observer;

    async function loadPage(page) {
        if (state.loading) return;
        state.loading = true;

        try {
            const data = await api.get(`api/trips?page=${page}`);
            state.totalPages = data.totalPages;
            const trips = data.tripCards ?? [];

            if (trips.length === 0 && page === 0) {
                renderEmpty(container, 'Поездок пока нет. Станьте первым!');
                return;
            }

            trips.forEach((trip, i) => {
                const card = createTripCardElement(trip);
                card.addEventListener('click', () => {
                    window.location.href = `trips/${trip.id}`;
                });
                container.appendChild(card);

                if (i === trips.length - 1) {
                    if (observer) observer.disconnect();
                    observer = createScrollObserver(container, state, loadPage);
                }
            });
        } catch (err) {
            renderError(container, err.data?.message ?? 'Ошибка загрузки');
        } finally {
            state.loading = false;
        }
    }

    loadPage(0);
}

function initApplicationModal() {
    const modal = document.getElementById('applicationModal');
    const closeBtn = modal.querySelector('.close-modal-btn');
    const form = document.getElementById('applicationForm');

    window.openApplicationModal = (tripId) => {
        document.getElementById('modalTripId').value = tripId;
        document.getElementById('modalContacts').value = '';
        document.getElementById('modalMessage').value = '';
        modal.style.display = 'block';
    };

    closeBtn.addEventListener('click', () => {
        modal.style.display = 'none';
    });
    window.addEventListener('click', (e) => {
        if (e.target === modal) modal.style.display = 'none';
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const tripId = document.getElementById('modalTripId').value;
        const contacts = document.getElementById('modalContacts').value;
        const message = document.getElementById('modalMessage').value;

        try {
            const res = await fetch(`api/applications/create?tripId=${tripId}`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({contacts, message}),
            });
            const data = await res.json();

            if (res.status === 200) {
                modal.style.display = 'none';
                alert.success('Заявка успешно отправлена!');
            } else {
                alert.info(data.message ?? 'Не удалось отправить заявку');
            }
        } catch {
            alert.error('Произошла ошибка при отправке запроса');
        }
    });
}
