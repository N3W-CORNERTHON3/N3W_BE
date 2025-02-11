package com.n3w.threedays.repository;

import com.n3w.threedays.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, String> {

    Optional<UserEntity> findById(String id); // 아이디 조회

    boolean existsById(String id); // 아이디 중복 확인
}
