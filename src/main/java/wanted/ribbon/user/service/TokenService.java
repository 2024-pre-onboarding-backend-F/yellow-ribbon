package wanted.ribbon.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wanted.ribbon.user.config.TokenProvider;
import wanted.ribbon.user.domain.RefreshToken;
import wanted.ribbon.user.domain.User;
import wanted.ribbon.user.repository.RefreshTokenRepository;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public String createNewAccessToken(String refreshToken) {
        // 토큰 유효성 검사
        if (!tokenProvider.validToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // DB에서 RefreshToken 찾기
        RefreshToken storedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        // 토큰 소유자(User) 찾기
        User user = storedRefreshToken.getUser();

        // 새로운 Access Token 발급
        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }

    @Transactional
    public void saveRefreshToken(User user, String refreshToken) {
        RefreshToken token = new RefreshToken(user, refreshToken);
        refreshTokenRepository.save(token);
    }
}
