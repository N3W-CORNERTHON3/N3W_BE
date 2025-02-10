package com.n3w.threedays.controller;

import com.n3w.threedays.entity.MissionEntity;
import com.n3w.threedays.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/missions/detail")
@RequiredArgsConstructor
public class MissionController {
    private final MissionService missionService;

    @GetMapping("/{missionId}")
    public ResponseEntity<MissionEntity> getMission(@PathVariable Long missionId) {
        MissionEntity mission = missionService.getMissionById(missionId);
        return ResponseEntity.ok(mission);
    }
}
