function createTripCardElement(trip) {
    const template = document.getElementById('tripCardTemplate');
    const card = template.content.cloneNode(true).querySelector('.trip-card');

    card.querySelector('.departure').textContent = trip.departurePoint;
    card.querySelector('.destination').textContent = trip.destination;
    card.querySelector('.budget-badge').textContent =
        new Intl.NumberFormat('ru-RU', {maximumFractionDigits: 0}).format(trip.budget) + ' ₽';
    card.querySelector('.date-range span').textContent = `${trip.startAt} — ${trip.endAt}`;
    card.querySelector('.owner-info span').textContent = trip.ownerUsername;
    card.querySelector('.slots-info span').textContent = trip.maxFellows;

    const tagsList = card.querySelector('.tags-list');
    if (trip.tags?.length) {
        tagsList.innerHTML = trip.tags.map(t => `<span class="tag">#${t}</span>`).join('');
    } else {
        tagsList.remove();
    }

    const favoriteBtn = card.querySelector('.favorite-btn');
    if (trip.isFavorite) favoriteBtn.classList.add('liked');

    favoriteBtn.addEventListener('click', async (e) => {
        e.stopPropagation();
        try {
            await api.postRaw(`api/favorites/toggle?tripId=${trip.id}`);
            favoriteBtn.classList.toggle('liked');
        } catch {
            alert.error('Не удалось изменить статус избранного');
        }
    });

    card.querySelector('.apply-btn').addEventListener('click', (e) => {
        e.stopPropagation();
        window.openApplicationModal(trip.id);
    });

    return card;
}
