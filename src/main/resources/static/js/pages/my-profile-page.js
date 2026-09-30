document.addEventListener('DOMContentLoaded', () => {
    loadProfileData();
    loadUserTrips(0);
    initSectionToggles();
});

const tripsState = {currentPage: 0, totalPages: 0, loading: false};
const favState = {currentPage: 0, totalPages: 0, loading: false};
const appState = {currentPage: 0, totalPages: 0, loading: false};
const joinedState = {currentPage: 0, totalPages: 0, loading: false};

let tripsObserver, favObserver, appObserver, joinedObserver;

function initSectionToggles() {
    const btnFav = document.getElementById('toggleFavoritesBtn');
    const btnApp = document.getElementById('toggleApplicationsBtn');
    const btnJoined = document.getElementById('toggleJoinedBtn');

    const sections = {
        profile: document.getElementById('profileContainer'),
        myTrips: document.getElementById('myTripsSection'),
        favorites: document.getElementById('favoriteTripsSection'),
        apps: document.getElementById('myApplicationsSection'),
        joined: document.getElementById('joinedTripsSection'),
    };

    let activeMode = null;

    function showDefaultView() {
        activeMode = null;
        sections.profile.style.display = 'block';
        sections.myTrips.style.display = 'block';
        sections.favorites.style.display = 'none';
        sections.apps.style.display = 'none';
        sections.joined.style.display = 'none';
        btnFav.innerText = 'Избранное';
        btnApp.innerText = '📩 Заявки мне';
        btnJoined.innerText = 'Одобренные заявки';
        btnFav.classList.remove('active-mode');
        btnApp.classList.remove('active-mode');
        btnJoined.classList.remove('active-mode');
    }

    btnFav.addEventListener('click', () => {
        if (activeMode === 'favorites') {
            showDefaultView();
            return;
        }
        showDefaultView();
        activeMode = 'favorites';
        sections.profile.style.display = 'none';
        sections.myTrips.style.display = 'none';
        sections.favorites.style.display = 'block';
        btnFav.innerText = 'К профилю';
        btnFav.classList.add('active-mode');
        favState.currentPage = 0;
        loadFavoriteTrips(0);
    });

    btnApp.addEventListener('click', () => {
        if (activeMode === 'applications') {
            showDefaultView();
            return;
        }
        showDefaultView();
        activeMode = 'applications';
        sections.profile.style.display = 'none';
        sections.myTrips.style.display = 'none';
        sections.apps.style.display = 'block';
        btnApp.innerText = 'К профилю';
        btnApp.classList.add('active-mode');
        appState.currentPage = 0;
        loadMyApplications(0);
    });

    btnJoined.addEventListener('click', () => {
        if (activeMode === 'joined') {
            showDefaultView();
            return;
        }
        showDefaultView();
        activeMode = 'joined';
        sections.profile.style.display = 'none';
        sections.myTrips.style.display = 'none';
        sections.joined.style.display = 'block';
        btnJoined.innerText = 'К профилю';
        btnJoined.classList.add('active-mode');
        joinedState.currentPage = 0;
        loadJoinedTrips(0);
    });
}

const PROFILE_FIELD_MAP = {
    firstName: 'input[name="firstName"]',
    lastName: 'input[name="lastName"]',
    birthDate: 'input[name="birthDate"]',
    contact: 'input[name="contact"]',
    country: 'input[name="country"]',
    city: 'input[name="city"]',
    bio: 'textarea[name="bio"]',
};

async function loadProfileData() {
    const container = document.getElementById('profileContainer');
    try {
        const profile = await api.get('api/profiles/my');
        container.appendChild(createProfileView(profile));
        document.getElementById('profileForm').addEventListener('submit', handleProfileSave);
    } catch (err) {
        renderError(container, err.data?.message ?? 'Не удалось загрузить профиль');
    }
}

async function handleProfileSave(e) {
    e.preventDefault();
    const form = e.target;
    const statusDiv = document.getElementById('saveStatus');
    const saveBtn = form.querySelector('.save-btn');

    clearFormErrors(form);
    saveBtn.disabled = true;
    saveBtn.innerText = 'Сохранение...';

    try {
        await api.put('api/profiles/my', Object.fromEntries(new FormData(form).entries()));
        statusDiv.innerText = 'Изменения успешно сохранены!';
        statusDiv.className = 'status-message success';
        setTimeout(() => {
            statusDiv.innerText = '';
            statusDiv.className = 'status-message';
        }, 3000);
    } catch (err) {
        if (err.status === 400 && err.data?.errors) {
            displayFieldErrors(form, err.data.errors, PROFILE_FIELD_MAP, statusDiv);
        } else {
            statusDiv.innerText = 'Ошибка: ' + (err.data?.message ?? 'Не удалось обновить профиль');
            statusDiv.className = 'status-message error';
        }
    } finally {
        saveBtn.disabled = false;
        saveBtn.innerText = 'Сохранить изменения';
    }
}

async function loadUserTrips(page) {
    if (tripsState.loading) return;
    tripsState.loading = true;

    const container = document.getElementById('myTripsContainer');

    try {
        const data = await api.get(`api/trips/my?page=${page}`);
        tripsState.totalPages = data.totalPages;
        const trips = data.tripCards ?? [];

        if (page === 0) container.innerHTML = '';

        if (trips.length === 0 && page === 0) {
            renderEmpty(container, 'Вы еще не создали ни одной поездки.');
            return;
        }

        trips.forEach(trip => {
            const card = createMyTripCardElement(trip);
            card.querySelector('.trip-info-block').onclick = () => {
                window.location.href = `trips/${trip.id}`;
            };

            const actions = document.createElement('div');
            actions.className = 'trip-actions';
            actions.innerHTML = `
                <button class="action-btn edit-btn"   data-id="${trip.id}">✏️</button>
                <button class="action-btn delete-btn" data-id="${trip.id}">🗑️</button>`;
            card.appendChild(actions);
            container.appendChild(card);
        });

        bindTripActions();

        if (tripsObserver) tripsObserver.disconnect();
        tripsObserver = createScrollObserver(container, tripsState, loadUserTrips);
    } catch (err) {
        renderError(container, err.data?.message ?? 'Ошибка загрузки поездок');
    } finally {
        tripsState.loading = false;
    }
}

function bindTripActions() {
    const editModal = document.getElementById('editTripModal');
    const editForm = document.getElementById('editTripForm');

    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.replaceWith(btn.cloneNode(true));
    });
    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.replaceWith(btn.cloneNode(true));
    });

    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.stopPropagation();
            const {isConfirmed} = await alert.confirm({
                title: 'Удалить поездку?',
                text: 'Это действие нельзя будет отменить'
            });
            if (!isConfirmed) return;
            try {
                await api.delete(`api/trips/${btn.dataset.id}`);
                tripsState.currentPage = 0;
                loadUserTrips(0);
            } catch (err) {
                alert.error(err.data?.message ?? 'Не удалось удалить поездку');
            }
        });
    });

    document.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.stopPropagation();
            try {
                const trip = await api.get(`api/trips/${btn.dataset.id}`);
                document.getElementById('editTripId').value = trip.id;
                document.getElementById('editBudget').value = trip.budget;
                document.getElementById('editMaxFellows').value = trip.maxFellows;
                document.getElementById('editDescription').value = trip.description ?? '';
                editModal.style.display = 'flex';
            } catch (err) {
                alert.error(err.data?.message ?? 'Не удалось загрузить поездку');
            }
        });
    });

    document.getElementById('closeModalBtn').onclick = () => {
        editModal.style.display = 'none';
    };
    window.onclick = (e) => {
        if (e.target === editModal) editModal.style.display = 'none';
    };

    editForm.onsubmit = async (e) => {
        e.preventDefault();
        const id = document.getElementById('editTripId').value;
        try {
            await api.put(`api/trips/${id}`, {
                budget: parseFloat(document.getElementById('editBudget').value),
                maxFellows: parseInt(document.getElementById('editMaxFellows').value),
                description: document.getElementById('editDescription').value,
            });
            editModal.style.display = 'none';
            tripsState.currentPage = 0;
            loadUserTrips(0);
        } catch (err) {
            alert.error(err.data?.message ?? 'Не удалось сохранить изменения');
        }
    };
}

async function loadFavoriteTrips(page) {
    if (favState.loading) return;
    favState.loading = true;

    const container = document.getElementById('favoriteTripsContainer');

    try {
        const data = await api.get(`api/favorites?page=${page}`);
        favState.totalPages = data.totalPages;
        const trips = data.tripCards ?? [];

        if (page === 0) container.innerHTML = '';

        if (trips.length === 0 && page === 0) {
            renderEmpty(container, 'У вас пока нет избранных поездок.');
            return;
        }

        trips.forEach(trip => {
            const card = createTripCardElement(trip);
            card.addEventListener('click', () => {
                window.location.href = `trips/${trip.id}`;
            });
            container.appendChild(card);
        });

        if (favObserver) favObserver.disconnect();
        favObserver = createScrollObserver(container, favState, loadFavoriteTrips);
    } catch (err) {
        renderError(container, err.data?.message ?? 'Ошибка загрузки избранных');
    } finally {
        favState.loading = false;
    }
}

async function loadMyApplications(page) {
    if (appState.loading) return;
    appState.loading = true;

    const container = document.getElementById('myApplicationsContainer');

    try {
        const data = await api.get(`api/applications/to-my-trips?page=${page}`);
        appState.totalPages = data.totalPages;
        const applications = data.applications ?? [];

        if (page === 0) container.innerHTML = '';

        if (applications.length === 0 && page === 0) {
            container.innerHTML = '<p class="no-apps-message">На ваши поездки пока никто не откликнулся.</p>';
            return;
        }

        applications.forEach(app => container.appendChild(createApplicationCardElement(app)));

        if (appObserver) appObserver.disconnect();
        appObserver = createScrollObserver(container, appState, loadMyApplications);
    } catch (err) {
        renderError(container, err.data?.message ?? 'Ошибка загрузки заявок');
    } finally {
        appState.loading = false;
    }
}

function createApplicationCardElement(app) {
    const template = document.getElementById('applicationCardTemplate');
    const clone = template.content.cloneNode(true);

    clone.querySelector('.app-trip-destination').innerText = `В ${app.tripDestination}`;
    clone.querySelector('.app-applicant').innerText = `От: @${app.applicantUsername}`;
    clone.querySelector('.app-message').innerText = app.message ? `"${app.message}"` : 'Без сопроводительного текста';
    clone.querySelector('.app-contacts').innerText = app.contacts;

    clone.querySelector('.view-trip-btn').addEventListener('click', () => {
        window.location.href = `trips/${app.tripId}`;
    });

    const acceptBtn = clone.querySelector('.accept-app-btn');
    acceptBtn.addEventListener('click', async () => {
        acceptBtn.disabled = true;
        acceptBtn.innerText = 'Принятие...';
        try {
            const res = await api.post(`api/applications/${app.id}/accept`);
            await alert.success(res?.message ?? 'Заявка принята');
            appState.currentPage = 0;
            loadMyApplications(0);
        } catch (err) {
            alert.error(err.data?.message ?? 'Ошибка при принятии заявки');
            acceptBtn.disabled = false;
            acceptBtn.innerText = '✓ Принять';
        }
    });

    return clone;
}

async function loadJoinedTrips(page) {
    if (joinedState.loading) return;
    joinedState.loading = true;

    const container = document.getElementById('joinedTripsContainer');

    try {
        const data = await api.get(`api/members/my-joined-trips?page=${page}`);
        joinedState.totalPages = data.totalPages;
        const members = data.members ?? [];

        if (page === 0) container.innerHTML = '';

        if (members.length === 0 && page === 0) {
            renderEmpty(container, 'Вы пока не участвуете ни в одной поездке.');
            return;
        }

        members.forEach(member => container.appendChild(createJoinedTripCardElement(member)));

        if (joinedObserver) joinedObserver.disconnect();
        joinedObserver = createScrollObserver(container, joinedState, loadJoinedTrips);
    } catch (err) {
        renderError(container, err.data?.message ?? 'Ошибка загрузки поездок');
    } finally {
        joinedState.loading = false;
    }
}

function createJoinedTripCardElement(member) {
    const template = document.getElementById('joinedTripCardTemplate');
    const clone = template.content.cloneNode(true);

    clone.querySelector('.departure').innerText = member.tripDeparturePoint;
    clone.querySelector('.destination').innerText = member.tripDestination;

    const goToTrip = () => {
        window.location.href = `trips/${member.tripId}`;
    };
    clone.querySelector('.trip-info-block').addEventListener('click', goToTrip);
    clone.querySelector('.view-trip-btn').addEventListener('click', goToTrip);

    return clone;
}