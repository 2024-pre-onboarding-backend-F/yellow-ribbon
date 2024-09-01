package wanted.ribbon.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.ribbon.user.domain.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByUserId(UUID userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
