package com.n3w.threedays.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
public class UserEntity {
    @Id
    @Column(nullable = false, unique = true)  // 기본키이자 닉네임으로 사용됨
    private String id;

    @Column(nullable = false)
    private String password;

    private String profileImg;

    public UserEntity(String id, String password) {
        this.id = id;
        this.password = password;
        this.profileImg = "src/main/resources/static/default/profile.png";
    }

    public UserEntity(String id, String password, String profileImg) {
        this.id = id;
        this.password = password;
        this.profileImg = profileImg;
    }
}
