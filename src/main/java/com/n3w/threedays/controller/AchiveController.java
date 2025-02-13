package com.n3w.threedays.controller;

import com.n3w.threedays.dto.ResponseDto;
import com.n3w.threedays.entity.MissionEntity;
import com.n3w.threedays.security.JwtTokenProvider;
import com.n3w.threedays.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achive")
@RequiredArgsConstructor
public class AchiveController {
    private final MissionService missionService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<ResponseDto<List<MissionEntity>>> getAchiveMissionList(@RequestHeader("Authorization") String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
        String userId = authentication.getName();

        List<MissionEntity> achiveMissionList = missionService.getAchiveMission(userId);

        ResponseDto<List<MissionEntity>> response = new ResponseDto<>(200, true, "성취 미션 조회 성공", achiveMissionList);

        return ResponseEntity.ok(response);
    }

        @GetMapping("/category")
        public ResponseEntity<ResponseDto<List<MissionEntity>>> getAchiveMissionListByCategory(@RequestHeader("Authorization") String token, @RequestParam("category") MissionEntity.Category category) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token.replace("Bearer ", ""));
            String userId = authentication.getName();

            List<MissionEntity> achiveMissionList = missionService.getAchiveMissionListByCategory(userId, category);

            ResponseDto<List<MissionEntity>> response = new ResponseDto<>(200, true, "카테고리별 성취 미션 조회 성공", achiveMissionList);

            return ResponseEntity.ok(response);
        }
}
