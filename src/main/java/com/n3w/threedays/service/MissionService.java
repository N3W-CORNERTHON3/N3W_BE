package com.n3w.threedays.service;

import com.n3w.threedays.dto.MissionRequestDto;
import com.n3w.threedays.entity.MissionEntity;
import com.n3w.threedays.exception.MissionNotFoundException;
import com.n3w.threedays.exception.NoRandomMissionFoundException;
import com.n3w.threedays.exception.NotFoundAchiveMissionException;
import com.n3w.threedays.exception.UnauthorizedException;
import com.n3w.threedays.repository.MissionRepository;
import com.n3w.threedays.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // [특정 미션 조회]
    public MissionEntity getMissionById(String userId, Long missionId) {
        // 미션 조회(없으면 예외 발생)
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        // 로그인한 사용자의 미션인지 확인
        if (!mission.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 미션을 조회할 권한이 없습니다.");
        }

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
    public MissionEntity updateMissionStatus(Long missionId, String userId, MissionEntity.Status newStatus) {
        // 미션 조회(없으면 예외 발생)
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        // 로그인한 사용자의 미션인지 확인
        if (!mission.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 미션을 수정할 권한이 없습니다.");
        }

        // 상태 변경
        mission.setStatus(newStatus);
        return mission; // 변경된 미션 반환
    }


    // [미션 생성]
    public MissionEntity createMission(MissionEntity mission) {
        return missionRepository.save(mission);
    }


    // [미션 수정]
    public MissionEntity updateMission(String userId, Long missionId, MissionRequestDto requestDto) {
        // 미션 조회(없으면 예외 발생)
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        // 로그인한 사용자의 미션인지 확인
        if (!mission.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 미션을 수정할 권한이 없습니다.");
        }

        // 수정할 내용 반영
        mission.setName(requestDto.getName());
        mission.setCategory(requestDto.getCategory());
        mission.setLevel(requestDto.getLevel());

        // 수정된 미션 저장
        return missionRepository.save(mission);
    }


    // [미션 삭제]
    public String deleteMission(String userId, Long missionId) {
        // 미션 조회(없으면 예외 발생)
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        // 로그인한 사용자의 미션인지 확인
        if (!mission.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 미션을 수정할 권한이 없습니다.");
        }

        String missionName = mission.getName(); // 미션 이름 저장

        // 미션 삭제
        missionRepository.delete(mission);

        return missionName; // 삭제된 미션 이름 반환
    }


    // [미션 목록 유무 조회]
    public boolean hasRegisteredMission(String userId) {
        return missionRepository.existsByUserId(userId);
    }


    // [전체 미션 목록 조회]
    public List<MissionEntity> getAllMissions(String userId) {
        return missionRepository.findByUserId(userId);
    }


    // [카테고리 별 미션 목록 조회]
    public List<MissionEntity> getMissionsByCategory(String userId, MissionEntity.Category category) {
        return missionRepository.findByUserIdAndCategory(userId, category);
    }


    // [미션 성취율 증가]
    @Transactional
    public MissionEntity increaseAchievement(Long missionId, String userId) {
        // 미션 조회
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        // 로그인한 사용자의 미션인지 확인
        if (!mission.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 미션을 수정할 권한이 없습니다.");
        }

        // 성취율 증가 (최대 3 제한)
        if (mission.getAchievement() < 3) {
            mission.setAchievement(mission.getAchievement() + 1);
        }

        return mission;
    }


    // [미션 성취율 감소]
    @Transactional
    public MissionEntity decreaseAchievement(Long missionId, String userId) {
        // 미션 조회
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        // 로그인한 사용자의 미션인지 확인
        if (!mission.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 미션을 수정할 권한이 없습니다.");
        }

        // 성취율 감소 (최소 0 제한)
        if (mission.getAchievement() > 0) {
            mission.setAchievement(mission.getAchievement() - 1);
        }

        return mission;
    }


    // [성취 미션 조회]
    public List<MissionEntity> getAchiveMission(String id) {
        List<MissionEntity> missionList = missionRepository.findByUserIdAndStatus(id, MissionEntity.Status.COMPLETE);

        if (missionList.isEmpty()){
            throw new NotFoundAchiveMissionException("성취한 미션이 없습니다.");
        }

        return missionList;
    }

  
    // [카테고리 별 성취 미션 조회]
    public List<MissionEntity> getAchiveMissionListByCategory(String id, MissionEntity.Category category) {
        List<MissionEntity> missionListByCategory = missionRepository.findByUserIdAndCategoryAndStatus(id, category, MissionEntity.Status.COMPLETE);

        if (missionListByCategory.isEmpty()){
            throw new NotFoundAchiveMissionException("해당 카테고리에 성취한 미션이 없습니다.");
        }

        return missionListByCategory;
    }
  
  
    // [메모 저장&수정]
    @Transactional // @Transactional: save() 호출 없이 자동으로 반영됨 (JPA 영속성 컨텍스트)
    public MissionEntity updateMemo(Long missionId, String userId, String newMemo) {
        // 미션 조회
        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        // 로그인한 사용자의 미션인지 확인
        if (!mission.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 미션을 수정할 권한이 없습니다.");
        }

        // 메모 업데이트
        mission.setMemo(newMemo != null ? newMemo : "");  // null 방지

        return mission;
    }


    // [챌린지 시작]
    @Transactional
    public MissionEntity startMission(Long missionId, String userId) {
        // 이미 진행 중인 미션이 있는지 체크
        if (missionRepository.findFirstByUserIdAndStatus(userId, MissionEntity.Status.PROGRESSING).isPresent()) {
            throw new IllegalStateException("이미 진행 중인 미션이 있습니다.");
        }

        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException(missionId));

        if (!mission.getUserId().equals(userId)) {
            throw new UnauthorizedException("해당 미션을 시작할 권한이 없습니다.");
        }

        // 미션 상태를 진행중으로 변경
        mission.setStatus(MissionEntity.Status.PROGRESSING);

        // 시작 날짜와 종료 날짜 설정
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(3);
        mission.setStartDate(startDate);
        mission.setEndDate(endDate);

        missionRepository.save(mission);

        return mission;
    }
}