package ru.itis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dto.ProfileDto;
import ru.itis.dto.form.ProfileForm;
import ru.itis.dto.request.UpdateProfileRequest;
import ru.itis.entity.Profile;
import ru.itis.entity.User;
import ru.itis.exception.exceptions.UserNotFoundException;
import ru.itis.repository.ProfileRepository;
import ru.itis.repository.UserRepository;

@Service
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Cacheable(value = "profiles", key = "#username")
    public ProfileDto findByUsername(String username) {
        return profileRepository.findByUsername(username)
                .map(ProfileDto::fromEntity)
                .orElseThrow(() -> new UserNotFoundException(username));
    }

    public void save(ProfileForm profileForm, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        Profile profile = Profile.builder()
                .user(user)
                .firstName(profileForm.getFirstName())
                .lastName(profileForm.getLastName())
                .birthDate(profileForm.getBirthDate())
                .contact(profileForm.getContact())
                .country(profileForm.getCountry())
                .city(profileForm.getCity())
                .avatarUrl(profileForm.getAvatarUrl())
                .bio(profileForm.getBio())
                .build();

        profileRepository.save(profile);
    }

    @CachePut(value = "profiles", key = "#username")
    public ProfileDto update(UpdateProfileRequest request, String username) {
        Profile profile = profileRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setBirthDate(request.getBirthDate());
        profile.setContact(request.getContact());
        profile.setCountry(request.getCountry());
        profile.setCity(request.getCity());
        profile.setBio(request.getBio());

        return ProfileDto.fromEntity(profileRepository.save(profile));
    }
}
