document.addEventListener('DOMContentLoaded', async () => {
    const container = document.getElementById('tripCardContainer');
    const tripId = window.location.pathname.split('/').at(-1);

    try {
        const trip = await api.get(`api/trips/${tripId}`);
        document.title = `${trip.departurePoint} → ${trip.destination} | TravelFellows`;
        container.innerHTML = buildTripHTML(trip);
    } catch (err) {
        renderError(container, err.data?.message ?? 'Не удалось загрузить поездку');
    }
});

function buildTripHTML(trip) {
    const budget = new Intl.NumberFormat('ru-RU', {maximumFractionDigits: 0}).format(trip.budget);
    const tagsHtml = trip.tags?.length
        ? `<div class="tags-container">${trip.tags.map(t => `<span class="tag">#${t}</span>`).join('')}</div>`
        : '';
    const desc = trip.description ?? 'Организатор не оставил описания.';

    return `
        <section class="trip-hero">
            <div class="route-title">
                <span>${trip.departurePoint}</span>
                <span class="route-arrow">→</span>
                <span>${trip.destination}</span>
            </div>
            <div class="budget-tag">${budget} ₽</div>
        </section>

        <div class="trip-info-grid">
            <div class="info-primary">
                <div class="info-block">
                    <h3>О путешествии</h3>
                    <p class="trip-description">${desc}</p>
                </div>
                ${tagsHtml ? `<div class="info-block"><h3>Теги поездки</h3>${tagsHtml}</div>` : ''}
            </div>

            <aside class="info-sidebar">
                ${metaItem('Начало', trip.startAt)}
                ${metaItem('Конец', trip.endAt)}
                ${metaItem('Максимум участников', `${trip.maxFellows} чел.`)}
                ${metaItem('Организатор', `<span class="owner-name">${trip.ownerUsername}</span>`)}
            </aside>
        </div>`;
}

function metaItem(label, value) {
    return `
        <div class="meta-item">
            <div class="meta-text">
                <span class="meta-label">${label}</span>
                <span class="meta-value">${value}</span>
            </div>
        </div>`;
}
