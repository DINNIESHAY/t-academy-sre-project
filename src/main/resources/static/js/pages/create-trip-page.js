document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('createTripForm');
    const submitBtn = document.getElementById('submitBtn');
    const errorMsg = document.getElementById('errorMessage');
    const selectedTags = new Set();

    const FIELD_MAP = {
        departurePoint: '#departurePoint',
        destination: '#destination',
        startAt: '#startAt',
        endAt: '#endAt',
        budget: '#budget',
        maxFellows: '#maxFellows',
        description: '#description',
    };

    document.getElementById('tagsSelector').addEventListener('click', (e) => {
        const chip = e.target.closest('.tag-chip');
        if (!chip) return;

        const tag = chip.dataset.tag;
        selectedTags.has(tag) ? selectedTags.delete(tag) : selectedTags.add(tag);
        chip.classList.toggle('active', selectedTags.has(tag));

        const selector = document.getElementById('tagsSelector');
        selector.classList.remove('error-input');
        selector.parentNode.querySelector('.field-error')?.remove();
    });

    form.querySelectorAll('input, textarea').forEach(input => {
        input.addEventListener('input', () => {
            if (input.classList.contains('error-input')) {
                input.classList.remove('error-input');
                input.parentNode.querySelector('.field-error')?.remove();
            }
        });
    });

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        clearFormErrors(form);
        errorMsg.style.display = 'none';

        const startAt = document.getElementById('startAt').value;
        const endAt = document.getElementById('endAt').value;

        if (startAt && endAt && new Date(startAt) > new Date(endAt)) {
            displayFieldErrors(form, {endAt: 'Дата окончания не может быть раньше даты начала'}, FIELD_MAP);
            return;
        }

        const tripData = {
            ...Object.fromEntries(new FormData(form).entries()),
            tags: [...selectedTags],
        };

        submitBtn.disabled = true;
        submitBtn.textContent = 'Публикация...';

        try {
            await api.post('api/trips', tripData);
            window.location.href = 'feed';
        } catch (err) {
            if (err.status === 400 && err.data?.errors) {
                if (err.data.errors.tags) {
                    const selector = document.getElementById('tagsSelector');
                    selector.classList.add('error-input');
                    const div = document.createElement('div');
                    div.className = 'field-error';
                    div.textContent = err.data.errors.tags;
                    selector.parentNode.appendChild(div);
                    delete err.data.errors.tags;
                }
                displayFieldErrors(form, err.data.errors, FIELD_MAP);
            } else {
                errorMsg.textContent = err.data?.message ?? 'Не удалось сохранить поездку';
                errorMsg.style.display = 'block';
            }
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Опубликовать поездку';
        }
    });
});
