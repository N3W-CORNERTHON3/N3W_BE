package com.n3w.threedays.repository;

import com.n3w.threedays.entity.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, String> {

    Optional<UserEntity> findUserById(String id); // 아이디 조회

    boolean existsUserIdById(String id); // 아이디 중복 확인

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.profileImg = :profileImg WHERE u.id = :id")
    void updateProfileImg(String id, String profileImg); // 이미지 업로드
}
