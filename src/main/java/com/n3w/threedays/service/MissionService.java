package com.n3w.threedays.service;

import com.n3w.threedays.entity.MissionEntity;
import com.n3w.threedays.exception.MissionNotFoundException;
import com.n3w.threedays.exception.NoRandomMissionFoundException;
import com.n3w.threedays.repository.MissionRepository;
//import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;

    // [특정 미션 조회]
    public MissionEntity getMissionById(Long missionId) {
        return missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));
    }

    // [진행중 상태인 미션 조회]
    public boolean hasOngoingMission(String userId) {
        return missionRepository.existsByUserIdAndStatus(userId, MissionEntity.Status.PROGRESSING);
    }

    // [랜덤 미션 조회]
    public MissionEntity getRandomMission(String userId, MissionEntity.Level level,
                                          MissionEntity.Category category, boolean includeCompleted) {
        // 포함할 상태 목록 설정
        List<MissionEntity.Status> statuses = includeCompleted
                ? Arrays.asList(MissionEntity.Status.INCOMPLETE, MissionEntity.Status.PROGRESSING, MissionEntity.Status.COMPLETE)
                : Arrays.asList(MissionEntity.Status.INCOMPLETE, MissionEntity.Status.PROGRESSING);

        // 조건에 맞는 미션 목록 가져오기
        List<MissionEntity> missions = missionRepository.findByUserIdAndLevelAndCategoryAndStatusIn(userId, level, category, statuses);

        // 미션이 없으면 예외 발생
        if (missions.isEmpty()) {
            throw new NoRandomMissionFoundException("선택하신 옵션에 맞는 미션이 존재하지 않습니다.");
        }

        // 랜덤으로 하나 선택
        Random random = new Random();
        return missions.get(random.nextInt(missions.size()));
    }

    // [미션 상태 변경]
    @Transactional
    public MissionEntity updateMissionStatus(Long missionId, MissionEntity.Status newStatus) {
        // 1. 미션 조회 (없으면 예외 발생)
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        // 2. 상태 변경
        mission.setStatus(newStatus);

        // 3. 변경된 미션 반환
        return mission;
    }
}