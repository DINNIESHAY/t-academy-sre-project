function createProfileView(profile) {
    const template = document.getElementById('profileCardTemplate');
    const clone    = template.content.cloneNode(true);

    const avatarWrapper = clone.querySelector('.avatar-wrapper');
    if (profile.avatarUrl) {
        const img = document.createElement('img');
        img.src = profile.avatarUrl;
        img.alt = 'Avatar';
        img.className = 'profile-avatar';
        avatarWrapper.appendChild(img);
    } else {
        const placeholder= document.createElement('div');
        placeholder.className = 'profile-avatar-placeholder';
        placeholder.textContent = '👤';
        avatarWrapper.appendChild(placeholder);
    }

    const set = (sel, val) => { clone.querySelector(sel).value = val ?? ''; };

    set('.username-field',   `@${profile.username}`);
    set('.email-field',      profile.email);
    set('.first-name-field', profile.firstName);
    set('.last-name-field',  profile.lastName);
    set('.birth-date-field', profile.birthDate);
    set('.contact-field',    profile.contact);
    set('.country-field',    profile.country);
    set('.city-field',       profile.city);
    set('.bio-field',        profile.bio);

    return clone;
}
