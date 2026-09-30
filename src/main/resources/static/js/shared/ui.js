const alert = {
    success: (text) => Swal.fire({icon: 'success', text, confirmButtonColor: '#ff7a45'}),
    error: (text) => Swal.fire({icon: 'error', text, confirmButtonColor: '#ff7a45'}),
    info: (text) => Swal.fire({icon: 'info', text, confirmButtonColor: '#ff7a45'}),
    confirm: (opts) => Swal.fire({
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#ff7a45',
        confirmButtonText: 'Да, удалить',
        cancelButtonText: 'Отмена',
        ...opts,
    }),
};

function clearFormErrors(form) {
    form.querySelectorAll('.field-error').forEach(el => el.remove());
    form.querySelectorAll('.error-input').forEach(el => el.classList.remove('error-input'));
}

function displayFieldErrors(form, errors, fieldMap, fallback = null) {
    clearFormErrors(form);
    if (!errors || !Object.keys(errors).length) return;

    for (const [field, message] of Object.entries(errors)) {
        const selector = fieldMap[field];
        const el = selector ? form.querySelector(selector) : null;

        if (el) {
            el.classList.add('error-input');
            const div = document.createElement('div');
            div.className = 'field-error';
            div.textContent = message;
            el.parentNode.appendChild(div);
        } else if (fallback) {
            fallback.textContent = message;
            fallback.className = 'status-message error';
        }
    }
}

function createScrollObserver(container, state, loadFn) {
    const observer = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting && !state.loading && state.currentPage + 1 < state.totalPages) {
            state.currentPage++;
            loadFn(state.currentPage);
        }
    }, {rootMargin: '100px'});

    const last = container.lastElementChild;
    if (last) observer.observe(last);

    return observer;
}

function renderError(container, message) {
    container.innerHTML = `
        <div class="error-state">
            <div class="icon">⚠️</div>
            <h2>Что-то пошло не так</h2>
            <p>${message}</p>
        </div>`;
}

function renderEmpty(container, message) {
    container.innerHTML = `<p class="empty-state" style="grid-column:1/-1;text-align:center;">${message}</p>`;
}
