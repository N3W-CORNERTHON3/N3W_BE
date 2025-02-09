package com.n3w.threedays.service;

import com.n3w.threedays.entity.UserEntity;
import com.n3w.threedays.repository.SimpleEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SimpleService {
    @Autowired
    SimpleEntityRepository simpleEntityRepository;

    public UserEntity insertSimpleEntity(String name) {
        if(simpleEntityRepository.findByName(name).size() > 0) {
            throw new IllegalArgumentException("이미 존재하는 이름입니다.");
        }
        return simpleEntityRepository.save(new UserEntity(name));
    }


    public List<String> searchName(String name) {
        List<UserEntity> list = simpleEntityRepository.findByName(name);
        List<String> ret = new ArrayList<>();

        for(UserEntity se : list){
            ret.add(se.getName());
        }
        return ret;
    }
}
