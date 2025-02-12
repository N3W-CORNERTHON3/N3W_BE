package com.n3w.threedays.service;

import com.n3w.threedays.dto.LoginRequestDto;
import com.n3w.threedays.dto.ProfilesResponseDto;
import com.n3w.threedays.dto.SignupRequestDto;
import com.n3w.threedays.dto.TokenResponseDto;
import com.n3w.threedays.entity.UserEntity;
import com.n3w.threedays.exception.DuplicateIDException;
import com.n3w.threedays.exception.IncorrectLoginException;
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
        // null 값 확인
//        if(request.getId() == null){
//            throw new NullException("아이디를 입력해주세요.");
//        } else if(request.getPassword() == null){
//            throw new NullException("비밀번호를 입력해주세요.");
//        }

        // 아이디 중복 확인
        if (checkId(request.getId())) {
            throw new DuplicateIDException("사용 중인 아이디입니다.");
        }

        // 비번 조건 확인
//        if (!request.getPassword().matches("(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}")) {
//            throw new IncorrectSignupException("비밀번호는 8~16자 영문 대소문자, 숫자, 특수문자를 사용해야 합니다.");
//        }


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
        // null 값 예외 처리
//        if(request.getId() == null){
//            throw new NullException("아이디를 입력해주세요.");
//        } else if(request.getPassword() == null){
//            throw new NullException("비밀번호를 입력해주세요.");
//        }

        // 아이디 확인
        UserEntity user = userRepository.findById(request.getId())
                .orElseThrow(() -> new IncorrectLoginException("아이디가 틀렸습니다."));

        // 비번 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IncorrectLoginException("비밀번호가 틀렸습니다.");
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        return new TokenResponseDto(token);
    }

    // 프로필 조회
    public ProfilesResponseDto getUserInfo(String id){
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        return new ProfilesResponseDto(user.getId(), user.getProfileImg());
    }

}