package wanted.ribbon.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import wanted.ribbon.user.domain.SocialType;
import wanted.ribbon.user.dto.LoginRequestDto;

@RequiredArgsConstructor
@Service
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {
    private final UserOauthService userAuthService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Kakao API를 통해 유저 정보를 가져오는 로직
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 사용자 정보 가져오기
        String id = oAuth2User.getAttribute("id").toString();

        // 사용자 로그인 처리
        LoginRequestDto loginRequestDto = userAuthService.loginUser(id, SocialType.KAKAO);

        // UserDetails 객체 생성 (필요에 따라)
        UserDetails userDetails = new CustomUserDetail(loginRequestDto.getId());

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 세션에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 카카오에서 받은 유저 정보를 처리하여 저장하거나 가공하는 로직 추가 가능
        return oAuth2User;
    }
}
