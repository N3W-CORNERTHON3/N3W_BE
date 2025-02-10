package com.n3w.threedays.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @Column(nullable = false, unique = true)  // 기본키이자 닉네임으로 사용됨
    private String id;

    @Column(nullable = false)
    private String password;

    private String profileImg = "src/main/resources/static/default/profile.png"; // 기본 프로필 이미지

    public UserEntity(String id, String password) {
        this.id = id;
        this.password = password;
    }
}
