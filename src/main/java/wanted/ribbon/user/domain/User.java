package wanted.ribbon.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Table(name="users")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "id", nullable = false, unique = true)
    @Size(max = 50)
    private String id;

    @Column(name = "password")
    @Size(max=200)
    private String password;

    @Column(name = "user_lat")
    private double lat;

    @Column(name= "user_lon")
    private double lon;

    @Column
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

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired(){
        // 계정 잠금되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음
    }

    // 계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked(){
        // 계정 잠금되었는지 확인하는 로직
        return true; // true -> 잠금되지 않았음
    }

    // 패스워드의 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired(){
        // 패스워드가 만려되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled(){
        // 계정이 사용 가능한지 확인하는 로직
        return true; // true -> 사용 가능
    }
}
