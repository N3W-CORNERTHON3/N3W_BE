package com.n3w.threedays.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.n3w.threedays.dto.LoginRequestDto;
import com.n3w.threedays.dto.ProfilesResponseDto;
import com.n3w.threedays.dto.SignupRequestDto;
import com.n3w.threedays.dto.TokenResponseDto;
import com.n3w.threedays.entity.UserEntity;
import com.n3w.threedays.exception.DuplicateIDException;
import com.n3w.threedays.exception.IncorrectLoginException;
import com.n3w.threedays.repository.UserRepository;
import com.n3w.threedays.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private final AmazonS3Client amazonS3Client;

    private static final String DEFAULT_IMG = "src/main/resources/static/default/profile.png";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, AmazonS3Client amazonS3Client){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.amazonS3Client = amazonS3Client;
    }

    // 회원가입
    public void signup(SignupRequestDto request){

        // 아이디 중복 확인
        if (checkId(request.getId())) {
            throw new DuplicateIDException("사용 중인 아이디입니다.");
        }

        // 비번 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        UserEntity user = new UserEntity(request.getId(), encodedPassword);

        userRepository.save(user);
    }

    // 아이디 중복 확인
    public boolean checkId(String id) {
        return userRepository.existsUserIdById(id);
    }

    // 로그인
    public TokenResponseDto login(LoginRequestDto request){

        // 아이디 확인
        UserEntity user = userRepository.findUserById(request.getId())
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
        UserEntity user = userRepository.findUserById(id)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        if (DEFAULT_IMG.equals(user.getProfileImg())) {
            user.setProfileImg("defualtImg");
        }

        return new ProfilesResponseDto(user.getId(), user.getProfileImg());
    }

    // 프로필 수정
    public String changeUserImg(String id, MultipartFile imageFile){
        // 기존 이미지 존재 확인
        String currentImgUrl  = getUserInfo(id).getProfileImg();

        // 이미지가 존재하면 삭제(기본 이미지는 제외)
        if (!currentImgUrl.equals("defualtImg") && StringUtils.hasText(currentImgUrl)) {
            // 파일명 추출
            String fileName = extractObjectKeyFromUrl(currentImgUrl);
            try {
                String decodedFileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
                if (amazonS3Client.doesObjectExist(bucket, decodedFileName)) {
                    amazonS3Client.deleteObject(bucket, decodedFileName);
                } else {
                    throw new AmazonS3Exception(decodedFileName + " 은 존재하지 않습니다.");
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("잘못된 파일 이름입니다: " + e.getMessage(), e);
            }
        }

        // 업로드 파일 확인
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        // 이미지 업로드
        try {
            // 파일 확인
            String originalFileName = imageFile.getOriginalFilename();

            if (originalFileName == null || !originalFileName.contains(".")) {
                throw new IllegalArgumentException("유효한 이미지 파일이 아닙니다.");
            }

            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String lowerCaseExtension = fileExtension.toLowerCase();

            List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg");

            if (!allowedExtensions.contains(lowerCaseExtension)) {
                throw new IllegalArgumentException("지원되지 않는 파일 형식입니다.");
            }

            String fileName = id + "_profile" + fileExtension;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(imageFile.getContentType());
            metadata.setContentLength(imageFile.getSize());
            amazonS3Client.putObject(bucket,fileName,imageFile.getInputStream(),metadata);

            String newImgUrl = amazonS3Client.getUrl(bucket, fileName).toString();

            userRepository.updateProfileImg(id, newImgUrl);

            return newImgUrl;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패: " + e.getMessage(), e);
        } catch (MaxUploadSizeExceededException e) {
            throw new MaxUploadSizeExceededException(imageFile.getSize());
        }
    }

    private String extractObjectKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}