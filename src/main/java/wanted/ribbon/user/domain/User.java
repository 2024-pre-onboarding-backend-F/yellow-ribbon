package wanted.ribbon.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name="users")
@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "id", nullable = false, unique = true)
    @Size(max = 50)
    private String id;

    @Column(name = "password")
    @Size(max = 200)
    private String password;

    @Column(name = "user_lat")
    private double lat;

    @Column(name= "user_lon")
    private double lon;

    @Column(name = "recommend")
    private boolean recommend;

    @Builder
    public User(UUID userId, String id, String password, double lat, double lon, boolean recommend) {
        this.userId = userId;
        this.id = id;
        this.password = password;
        this.lat = lat;
        this.lon = lon;
        this.recommend = recommend;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("user"));
    }
    // 사용자의 id를 반환(고유한 값)
    @Override
    public String getUsername(){
        return id;
    }

    @Override
    public String getPassword(){
        return password;
    }

    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    @Override
    public boolean isEnabled(){
        return true;
    }
}
