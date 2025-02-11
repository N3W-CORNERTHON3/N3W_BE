package com.n3w.threedays.controller;

import com.n3w.threedays.entity.MissionEntity;
import com.n3w.threedays.security.JwtTokenProvider;
import com.n3w.threedays.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;
    private final JwtTokenProvider jwtTokenProvider;

    // [미션 상세 정보 조회]
    @GetMapping("detail/{missionId}")
    public ResponseEntity<MissionEntity> getMission(@PathVariable Long missionId) {
        MissionEntity mission = missionService.getMissionById(missionId);
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

}
