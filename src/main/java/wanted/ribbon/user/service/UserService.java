package wanted.ribbon.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import wanted.ribbon.exception.ErrorCode;
import wanted.ribbon.exception.NotFoundException;
import wanted.ribbon.user.config.TokenProvider;
import wanted.ribbon.user.domain.User;
import wanted.ribbon.user.dto.*;
import wanted.ribbon.user.repository.UserRepository;

import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenProvider tokenProvider;   // TokenProvider를 사용
    private final TokenService tokenService;     // TokenService를 사용

    public SignUpResponse save(SignUpRequest dto){
        User user = userRepository.save(User.builder()
                .id(dto.id())
                .password(bCryptPasswordEncoder.encode(dto.password())) // 패스워드 암호화
                .build());

        return  new SignUpResponse(
                "회원가입 성공",
                user.getUserId(),
                user.getId()
        );
    }

    public ProfileResponse findByUser(UUID userId, ProfileRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));

        return new ProfileResponse(
                "회원 조회 성공",
                user.getId(),
                user.getLat(),
                user.getLon(),
                user.isRecommend()
        );
    }

    @Transactional
    public UpdateProfileResponse updateUser(UUID userId, UpdateProfileRequest request) {
        // 1. 유저 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));

        // 2. 요청된 lat, lon, recommend 값으로 유저 객체 업데이트
        user.setLat(request.lat());
        user.setLon(request.lon());
        user.setRecommend(request.recommend());

        // 3. 변경된 유저 객체 저장
        User updatedUser = userRepository.save(user);

        // 4. UpdateUserResponse 생성 및 반환
        return new UpdateProfileResponse(
                "위도, 경도, 추천 여부 변경 완료",
                user.getUserId(),
                user.getLat(),
                user.getLon(),
                user.isRecommend()
        );
    }

    @Transactional
    public LoginResponse login(LoginRequest requestDto) {
        User user;

        if (requestDto.socialType() != null) {
            // 소셜 로그인 처리 (KAKAO 등)
            user = userRepository.findByIdAndSocialType(requestDto.id(), requestDto.socialType())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));
        } else {
            // 일반 로그인 처리 (SocialType 없이)
            // 사용자 조회 (String 타입의 ID와 소셜 타입으로 조회)
            user = (User) userRepository.findById(requestDto.id())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));

            // 비밀번호 검증(소셜 로그인은 비밀번호 검증 생략)
            if (!bCryptPasswordEncoder.matches(requestDto.password(), user.getPassword())) {
                throw new IllegalArgumentException("Invalid password");
            }
        }

        // JWT 토큰 발급
        String accessToken = tokenProvider.generateToken(user, Duration.ofHours(2));
        String refreshToken = UUID.randomUUID().toString(); // 새로운 Refresh Token 생성

        // Refresh Token을 DB에 저장
        tokenService.saveRefreshToken(user, refreshToken);

        // 응답 DTO 반환
        return new LoginResponse(user.getUserId(), accessToken, refreshToken);
    }
}
