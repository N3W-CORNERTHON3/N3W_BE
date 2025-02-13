package com.n3w.threedays.repository;

import com.n3w.threedays.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    // 사용자의 전체 미션 목록
    List<MissionEntity> findByUserId(String userId);

    // 아이디와 카테고리로 미션 확인
    List<MissionEntity> findByUserIdAndCategory(String userId, MissionEntity.Category category);

    // 성취 미션 목록 확인
    List<MissionEntity> findByUserIdAndStatus(String userid, MissionEntity.Status status);

    // 카테고리별 성취 미션 목록 확인
    List<MissionEntity> findByUserIdAndCategoryAndStatus(String userId, MissionEntity.Category category, MissionEntity.Status status);


    // 특정 사용자의 진행 중인 미션 조회
    Optional<MissionEntity> findFirstByUserIdAndStatus(String userId, MissionEntity.Status status);

    // 종료 날짜가 지난 진행 중인 미션 조회
    List<MissionEntity> findByStatusAndEndDateBefore(MissionEntity.Status status, LocalDate date);

}
