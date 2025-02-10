package com.n3w.threedays.controller;

import com.n3w.threedays.entity.MissionEntity;
import com.n3w.threedays.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {
    private final MissionService missionService;

    @GetMapping("detail/{missionId}")
    public ResponseEntity<MissionEntity> getMission(@PathVariable Long missionId) {
        MissionEntity mission = missionService.getMissionById(missionId);
        return ResponseEntity.ok(mission);
    }

    @GetMapping("/ongoing")
    public ResponseEntity<Map<String, Boolean>> checkOngoingMission(@RequestParam String userId) {
        boolean hasOngoing = missionService.hasOngoingMission(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasOngoingMission", hasOngoing);
        return ResponseEntity.ok(response);
    }
}
