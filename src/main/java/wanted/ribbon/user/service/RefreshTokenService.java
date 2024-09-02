package wanted.ribbon.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wanted.ribbon.user.domain.User;
import wanted.ribbon.user.domain.RefreshToken;
import wanted.ribbon.user.repository.RefreshTokenRepository;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveRefreshToken(User user, String refreshToken) {
        RefreshToken token = new RefreshToken(user, refreshToken);
        refreshTokenRepository.save(token);
    }

    public RefreshToken findByRefreshToken(String refreshToken){
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
    }
}
