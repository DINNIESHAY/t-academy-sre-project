package ru.itis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dto.ApplicationCardDto;
import ru.itis.dto.ApplicationCardsContainerDto;
import ru.itis.dto.request.AddApplicationRequest;
import ru.itis.entity.Trip;
import ru.itis.entity.TripApplication;
import ru.itis.entity.TripMember;
import ru.itis.entity.User;
import ru.itis.exception.exceptions.TripNotFoundException;
import ru.itis.exception.exceptions.UserNotFoundException;
import ru.itis.metrics.BusinessMetrics;
import ru.itis.repository.TripApplicationRepository;
import ru.itis.repository.TripMemberRepository;
import ru.itis.repository.TripRepository;
import ru.itis.repository.UserRepository;

import java.util.List;

@Service
public class TripApplicationService {

    private final TripApplicationRepository tripApplicationRepository;
    private final TripMemberRepository tripMemberRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final BusinessMetrics metrics;

    @Autowired
    public TripApplicationService(TripApplicationRepository tripApplicationRepository,
                                  TripMemberRepository tripMemberRepository,
                                  TripRepository tripRepository,
                                  UserRepository userRepository,
                                  BusinessMetrics metrics) {
        this.tripApplicationRepository = tripApplicationRepository;
        this.tripMemberRepository = tripMemberRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
        this.metrics = metrics;
    }

    @Transactional
    public void createApplication(Long tripId, String username, AddApplicationRequest request) {
        User applicant = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + username));

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Поездка не найдена: " + tripId));

        if (trip.getOwner().getId().equals(applicant.getId())) {
            throw new IllegalArgumentException("Вы не можете подать заявку на собственную поездку");
        }

        if (tripApplicationRepository.existsByApplicantIdAndTripId(applicant.getId(), tripId)) {
            throw new IllegalStateException("Вы уже подали заявку на эту поездку");
        }

        if (tripMemberRepository.existsByTripIdAndUserId(tripId, applicant.getId())) {
            throw new IllegalStateException("Вы уже являетесь участником этой поездки");
        }

        long currentMembers = tripMemberRepository.countByTripId(tripId);
        if (currentMembers >= trip.getMaxFellows()) {
            throw new IllegalStateException("В поездке больше нет свободных мест");
        }

        tripApplicationRepository.save(new TripApplication(applicant, trip, request.getMessage(), request.getContacts()));
        metrics.applicationSubmitted();
    }

    @Transactional
    public void acceptApplication(Long applicationId) {
        TripApplication application = tripApplicationRepository.findByIdWithDetails(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));

        long currentMembers = tripMemberRepository.countByTripId(application.getTrip().getId());
        if (currentMembers >= application.getTrip().getMaxFellows()) {
            throw new IllegalStateException("В поездке больше нет свободных мест");
        }

        tripMemberRepository.save(new TripMember(application.getTrip(), application.getApplicant()));
        tripApplicationRepository.delete(application);
        metrics.applicationAccepted();
    }

    public ApplicationCardsContainerDto findApplicationsToMyTrips(String username, Pageable pageable) {
        Page<TripApplication> appPage = tripApplicationRepository.findAllByTripOwner(username, pageable);

        List<ApplicationCardDto> appList = appPage.getContent().stream()
                .map(ApplicationCardDto::fromEntity)
                .toList();

        return new ApplicationCardsContainerDto(appList, appPage.getNumber(), appPage.getTotalPages());
    }
}