package com.n3w.threedays.controller;

import com.n3w.threedays.dto.MissionRequestDto;
import com.n3w.threedays.entity.MissionEntity;
import com.n3w.threedays.security.JwtTokenProvider;
import com.n3w.threedays.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final JwtTokenProvider jwtTokenProvider;

    // [미션 상세 정보 조회]
    @GetMapping("detail/{missionId}")
    public ResponseEntity<MissionEntity> getMission(
            @RequestHeader("Authorization") String token,
            @PathVariable Long missionId) {
        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();  // userId 가져오기

        MissionEntity mission = missionService.getMissionById(userId, missionId);
        return ResponseEntity.ok(mission);
    }


    // [진행중 상태 미션 유무 조회]
    @GetMapping("/ongoing")
    public ResponseEntity<Map<String, Boolean>> checkOngoingMission(@RequestHeader("Authorization") String token) {
        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();  // userId 가져오기

        boolean hasOngoing = missionService.hasOngoingMission(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasOngoingMission", hasOngoing);
        return ResponseEntity.ok(response);
    }


    // [랜덤 미션 조회]
    @GetMapping("/random")
    public ResponseEntity<MissionEntity> getRandomMission(
            @RequestHeader("Authorization") String token,
            @RequestParam MissionEntity.Level level,
            @RequestParam MissionEntity.Category category,
            @RequestParam boolean includeCompleted) {

        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();  // userId 가져오기

        MissionEntity randomMission = missionService.getRandomMission(userId, level, category, includeCompleted);
        return ResponseEntity.ok(randomMission);
    }


    // [미션 상태 변경]
    @PutMapping("status/{missionId}/status")
    public ResponseEntity<MissionEntity> updateMissionStatus(
            @PathVariable Long missionId,
            @RequestParam MissionEntity.Status newStatus,
            @RequestHeader("Authorization") String token) {

        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();  // userId 가져오기

        MissionEntity updatedMission = missionService.updateMissionStatus(missionId, userId, newStatus);
        return ResponseEntity.ok(updatedMission);
    }


    // [미션 생성]
    @PostMapping
    public ResponseEntity<MissionEntity> createMission(
            @RequestHeader("Authorization") String token,
            @RequestBody MissionRequestDto requestDto) {

        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();  // userId 가져오기

        MissionEntity newMission = new MissionEntity(
                userId,
                requestDto.getName(),
                requestDto.getCategory(),
                requestDto.getLevel()
        );

        // 미션 저장
        MissionEntity savedMission = missionService.createMission(newMission);

        return ResponseEntity.ok(savedMission);
    }

    // [미션 여러 개 저장]
    @PostMapping("/batch")
    public ResponseEntity<List<MissionEntity>> createMissions(
            @RequestHeader("Authorization") String token,
            @RequestBody List<MissionRequestDto> requestDtos) {

        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();

        List<MissionEntity> newMissions = requestDtos.stream()
                .map(dto -> new MissionEntity(userId, dto.getName(), dto.getCategory(), dto.getLevel()))
                .collect(Collectors.toList());

        List<MissionEntity> savedMissions = missionService.createMissions(newMissions);  // 여러 개 저장

        return ResponseEntity.ok(savedMissions);
    }

    // [미션 수정]
    @PutMapping("/{missionId}")
    public ResponseEntity<MissionEntity> updateMission(
            @RequestHeader("Authorization") String token,
            @PathVariable Long missionId,
            @RequestBody MissionRequestDto requestDto) {

        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();  // userId 가져오기

        // 미션 수정
        MissionEntity updatedMission = missionService.updateMission(userId, missionId, requestDto);

        return ResponseEntity.ok(updatedMission);
    }


    // [미션 삭제]
    @DeleteMapping("/{missionId}")
    public ResponseEntity<Map<String, String>> deleteMission(
            @RequestHeader("Authorization") String token,
            @PathVariable Long missionId) {

        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();  // userId 가져오기

        // 미션 삭제 및 삭제된 미션 이름 가져오기
        String missionName = missionService.deleteMission(userId, missionId);

        // 응답 메시지 생성
        Map<String, String> response = new HashMap<>();
        response.put("message", missionName + " 미션이 삭제되었습니다.");

        return ResponseEntity.ok(response);
    }


    // [미션 목록 유무 조회]
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Boolean>> checkRegisteredMission(@RequestHeader("Authorization") String token) {
        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();  // userId 가져오기

        boolean hasRegisteredMission = missionService.hasRegisteredMission(userId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("hasRegisteredMission", hasRegisteredMission);

        return ResponseEntity.ok(response);
    }


    // [전체 미션 목록 조회]
    @GetMapping
    public ResponseEntity<?> allMission(@RequestHeader("Authorization") String token) {
        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();  // userId 가져오기

        // 사용자의 모든 미션 조회
        List<MissionEntity> missions = missionService.getAllMissions(userId);

        // 미션이 없는 경우
        if (missions.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "미션이 존재하지 않습니다.");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(missions);
    }


    // [카테고리 별 미션 목록 조회]
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getMissionsByCategory(
            @RequestHeader("Authorization") String token,
            @PathVariable MissionEntity.Category category) {

        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();  // userId 가져오기

        // 특정 카테고리의 미션 목록 조회
        List<MissionEntity> missions = missionService.getMissionsByCategory(userId, category);

        // 미션이 없는 경우
        if (missions.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", category.name() + " 카테고리에 미션이 존재하지 않습니다.");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.ok(missions);
    }


    // [미션 성취율 증가]
    @PutMapping("/check/{missionId}")
    public ResponseEntity<MissionEntity> increaseAchievement(
            @PathVariable Long missionId,
            @RequestHeader("Authorization") String token) {

        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();

        MissionEntity updatedMission = missionService.increaseAchievement(missionId, userId);
        return ResponseEntity.ok(updatedMission);
    }


    // [미션 성취율 감소]
    @PutMapping("/uncheck/{missionId}")
    public ResponseEntity<MissionEntity> decreaseAchievement(
            @PathVariable Long missionId,
            @RequestHeader("Authorization") String token) {

        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();

        MissionEntity updatedMission = missionService.decreaseAchievement(missionId, userId);
        return ResponseEntity.ok(updatedMission);
    }


    // [메모 저장&수정]
    @PutMapping("/memo/{missionId}")
    public ResponseEntity<MissionEntity> updateMemo(
            @PathVariable Long missionId,
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> requestBody) {

        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();

        String newMemo = requestBody.getOrDefault("memo", "");  // null이면 ""로 변경

        MissionEntity updatedMission = missionService.updateMemo(missionId, userId, newMemo);
        return ResponseEntity.ok(updatedMission);
    }


    // [챌린지 시작]
    @PutMapping("/start/{missionId}")
    public ResponseEntity<MissionEntity> startMission(
            @PathVariable Long missionId,
            @RequestHeader("Authorization") String token) {
        // 토큰에서 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();


        MissionEntity updatedMission = missionService.startMission(missionId, userId);

        return ResponseEntity.ok(updatedMission);
    }
}
