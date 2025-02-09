package com.n3w.threedays.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {  // 클래스명 대문자로 변경
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Long 타입 ID 추가

    @Column(nullable = false, unique = true)
    private String name;

    private String password;
    private String profileImg;

    public UserEntity(String name){
        this.name = name;
    }
}
