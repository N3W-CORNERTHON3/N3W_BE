package com.n3w.threedays.repository;

import com.n3w.threedays.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, Long> {
    boolean existsByUserIdAndStatus(String userId, MissionEntity.Status status); // 진행중 상태인 미션 여부 확인
}
