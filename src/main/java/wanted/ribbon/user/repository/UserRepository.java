package wanted.ribbon.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.ribbon.user.domain.SocialType;
import wanted.ribbon.user.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // id로 사용자 정보를 가져옴
    Optional<User> findByIdAndSocialType(String id, SocialType socialType);
    Optional<Object> findById(String id);
}
