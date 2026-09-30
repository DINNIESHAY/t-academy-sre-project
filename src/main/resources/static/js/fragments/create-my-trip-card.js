function createMyTripCardElement(trip) {
    const template = document.getElementById('myTripCardTemplate');
    const card = template.content.cloneNode(true).querySelector('.trip-card');

    card.querySelector('.departure').textContent = trip.departurePoint;
    card.querySelector('.destination').textContent = trip.destination;
    card.querySelector('.budget-badge').textContent =
        new Intl.NumberFormat('ru-RU', {maximumFractionDigits: 0}).format(trip.budget) + ' ₽';
    card.querySelector('.date-range span').textContent = `${trip.startAt} — ${trip.endAt}`;
    card.querySelector('.slots-info span').textContent = trip.maxFellows;
    card.querySelector('.likes-info span').textContent = trip.likesCount;

    const tagsList = card.querySelector('.tags-list');
    if (trip.tags?.length) {
        tagsList.innerHTML = trip.tags.map(t => `<span class="tag">#${t}</span>`).join('');
    } else {
        tagsList.remove();
    }

    return card;
}
