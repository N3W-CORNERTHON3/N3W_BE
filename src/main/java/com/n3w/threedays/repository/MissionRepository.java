package com.n3w.threedays.repository;

import com.n3w.threedays.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, Long> {
    // 진행중 상태인 미션 여부 확인
    boolean existsByUserIdAndStatus(String userId, MissionEntity.Status status);

    // 랜덤 미션 선택
    List<MissionEntity> findByUserIdAndLevelAndCategoryAndStatusIn(
            String userId,
            MissionEntity.Level level,
            MissionEntity.Category category,
            List<MissionEntity.Status> statuses
    );

    // 사용자가 등록한 미션 유무 확인
    boolean existsByUserId(String userId);

}
