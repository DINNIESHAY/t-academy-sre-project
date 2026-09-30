package ru.itis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.itis.dto.TripMemberDto;
import ru.itis.dto.TripMembersContainerDto;
import ru.itis.entity.TripMember;
import ru.itis.repository.TripMemberRepository;

import java.util.List;

@Service
public class TripMemberService {

    private final TripMemberRepository tripMemberRepository;

    @Autowired
    public TripMemberService(TripMemberRepository tripMemberRepository) {
        this.tripMemberRepository = tripMemberRepository;
    }

    public TripMembersContainerDto findMembersByTrip(Long tripId, Pageable pageable) {
        Page<TripMember> page = tripMemberRepository.findAllByTripId(tripId, pageable);

        List<TripMemberDto> members = page.getContent().stream()
                .map(TripMemberDto::fromEntity)
                .toList();

        return new TripMembersContainerDto(members, page.getNumber(), page.getTotalPages());
    }

    public TripMembersContainerDto findTripsByMember(String username, Pageable pageable) {
        Page<TripMember> page = tripMemberRepository.findAllByUsername(username, pageable);

        List<TripMemberDto> members = page.getContent().stream()
                .map(TripMemberDto::fromEntity)
                .toList();

        return new TripMembersContainerDto(members, page.getNumber(), page.getTotalPages());
    }
}