package com.n3w.threedays.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MissionEntity {

    public enum Category {
        HEALTH, SELF_IMPROVEMENT, STUDY, HOBBY, ETC
    }

    public enum Level {
        HIGH, MEDIUM, LOW, RANDOM
    }

    public enum Status {
        INCOMPLETE, PROGRESSING, COMPLETE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long missionId;  // 기본키 (PK), 자동 증가

    @Column(name = "user_id", nullable = false)
    private String userId; // UserEntity의 id

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;  // 건강, 자기계발, 공부, 취미, 기타

    @Column(nullable = false)
    private String name;  // 사용자 입력

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;  // HIGH, MEDIUM, LOW, RANDOM

    @Column(nullable = false)
    private int achievement = 0;  // 기본값 0 (0,1,2,3)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.INCOMPLETE;  // 기본값 "INCOMPLETE" (미완료)

    @Column(nullable = true)
    private LocalDate startDate;  // 미션 시작 날짜

    @Column(nullable = true)
    private LocalDate endDate;  // 미션 종료 날짜

    @Column(length = 1000, nullable = false)
    private String memo = ""; // 기본값을 빈 문자열로 설정

    public MissionEntity(String userId, String name, Category category, Level level) {
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.level = level;
        this.achievement = 0;
        this.status = Status.INCOMPLETE;
        this.memo = "";
    }
}
