package com.n3w.threedays.controller;

import com.n3w.threedays.dto.LoginRequestDto;
import com.n3w.threedays.dto.ResponseDto;
import com.n3w.threedays.dto.SignupRequestDto;
import com.n3w.threedays.dto.TokenResponseDto;
import com.n3w.threedays.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 아이디 중복 체크
    @GetMapping("/checkId")
    public ResponseEntity<ResponseDto<Integer>> checkIdDuplicate(@RequestParam String id) {
        boolean isDuplicate = userService.checkId(id);
        ResponseDto<Integer> response;
        
        if(isDuplicate){
            response = new ResponseDto<>(400, false,"중복 아이디", 1);
        } else {
            response = new ResponseDto<>(200, true,"사용 가능한 아이디", 1);
        }
        
        return ResponseEntity.ok(response);
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<Integer>> signup(@Valid @RequestBody SignupRequestDto request){
        boolean isCheckId = userService.checkId(request.getId());
        if (isCheckId){
            return ResponseEntity.badRequest().body(new ResponseDto<>(400, false, "아이디 중복", 0));
        }

        userService.signup(request);

        ResponseDto<Integer> response = new ResponseDto<>(200, true, "회원가입 성공", 1);

        return ResponseEntity.ok().body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseDto<TokenResponseDto>> login(@Valid @RequestBody LoginRequestDto request){
        TokenResponseDto token = userService.login(request);

        ResponseDto<TokenResponseDto> response = new ResponseDto<>(200, true, "로그인 성공", token);

        return ResponseEntity.ok(response);
    }

}
