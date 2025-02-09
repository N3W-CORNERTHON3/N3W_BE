package com.n3w.threedays.repository;

import com.n3w.threedays.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SimpleEntityRepository extends JpaRepository<UserEntity, Long> {

    public List<UserEntity> findByName(String name);
}
