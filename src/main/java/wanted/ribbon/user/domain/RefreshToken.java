package wanted.ribbon.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id", nullable = false)
    private Long tokenId;

    //@ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "user_id", nullable = false, unique = true)
    //@JoinColumn(name = "user_id", nullable = false)
    private UUID userId;
//    private User user;


    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    public RefreshToken(UUID userId, String refreshToken){
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public RefreshToken update(String newRefreshToken){
        this.refreshToken = newRefreshToken;
        return this;
    }
}
