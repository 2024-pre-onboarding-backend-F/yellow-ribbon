package wanted.ribbon.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.ribbon.user.domain.RefreshToken;
import wanted.ribbon.user.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByUserId(User user);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
