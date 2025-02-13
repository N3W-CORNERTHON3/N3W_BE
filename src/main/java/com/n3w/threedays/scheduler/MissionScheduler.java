package com.n3w.threedays.scheduler;

import com.n3w.threedays.entity.MissionEntity;
import com.n3w.threedays.repository.MissionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class MissionScheduler {

    private final MissionRepository missionRepository;

    public MissionScheduler(MissionRepository missionRepository) {
        this.missionRepository = missionRepository;
    }

    // 매일 00:00:01 실행
    @Scheduled(cron = "1 0 0 * * *")
    @Transactional
    public void completeExpiredMissions() {
        LocalDate today = LocalDate.now();

        // 진행 중인 미션이 있는지 조회
        List<MissionEntity> ongoingMissions = missionRepository.findByStatusAndEndDateBefore(MissionEntity.Status.PROGRESSING, today);

        // 진행 중인 미션이 1개 이상이면 오류 로그 출력
        if (ongoingMissions.size() > 1) {
            log.error("진행 중인 미션이 1개 이상 존재합니다. 데이터 무결성을 확인하세요.");
        }

        // 진행 중인 미션이 1개라면 자동 완료 처리
        ongoingMissions.stream().findFirst().ifPresent(mission -> {
            mission.setStatus(MissionEntity.Status.COMPLETE);
            missionRepository.save(mission);
            log.info("자동 완료된 미션: {}", mission.getMissionId());
        });
    }
}
