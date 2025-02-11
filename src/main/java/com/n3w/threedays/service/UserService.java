package com.n3w.threedays.service;

import com.n3w.threedays.dto.LoginRequestDto;
import com.n3w.threedays.dto.SignupRequestDto;
import com.n3w.threedays.dto.TokenResponseDto;
import com.n3w.threedays.entity.UserEntity;
import com.n3w.threedays.repository.UserRepository;
import com.n3w.threedays.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 회원가입
    public void signup(SignupRequestDto request){
        // 아이디 중복 확인
        if (checkId(request.getId())) {
            throw new IllegalArgumentException("사용 중인 아이디입니다.");
        }

        // 비번 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity user = new UserEntity(request.getId(), encodedPassword);

        userRepository.save(user);
    }

    // 아이디 중복 확인
    public boolean checkId(String id) {
        return userRepository.existsById(id);
    }

    // 로그인
    public TokenResponseDto login(LoginRequestDto request){
        UserEntity user = userRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        return new TokenResponseDto(token);
    }

}