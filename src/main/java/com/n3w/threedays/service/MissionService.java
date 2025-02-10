package com.n3w.threedays.service;

import com.n3w.threedays.entity.MissionEntity;
import com.n3w.threedays.exception.MissionNotFoundException;
import com.n3w.threedays.repository.MissionRepository;
//import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;

    public MissionEntity getMissionById(Long missionId) {
        return missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));
    }
}