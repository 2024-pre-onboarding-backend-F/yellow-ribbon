package wanted.ribbon.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import wanted.ribbon.user.domain.SocialType;
import wanted.ribbon.user.domain.User;
import wanted.ribbon.user.dto.LoginRequestDto;
import wanted.ribbon.user.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserOauthService {
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Transactional
    public LoginRequestDto loginUser(String id, SocialType socialType){
        Optional<User> registedUser = userRepository.findByIdAndSocialType(id, socialType);

        if (registedUser.isPresent()) {
            // 기존 회원인 경우 로그인 처리
            return new LoginRequestDto(id, null, socialType); // 패스워드 없이 소셜 로그인 처리
        } else {
            // 신규 회원 가입 처리
            User newUser = User.builder()
                    .id(id)
                    .socialType(socialType)
                    .build();
            userRepository.save(newUser);
            return new LoginRequestDto(id, null, socialType);
        }
    }

    @Transactional
    public User registedUser(String id, SocialType socialType) {
        // 사용자 정보를 설정하여 User 객체 생성
        User user = User.builder()
                .id(id)
                .socialType(socialType)
                .password("9999")
                .build();

        // User 객체를 데이터베이스에 저장
        userRepository.save(user);

        return user;
    }

    // kakao 회원정보 받기
    @Transactional
    public Map<String, Object> getKakaoUserInfo(String token){
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> header = new HttpEntity<>(headers);
        ResponseEntity<String> res = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                header,
                String.class
        );

        // 결과 parsing
        Map<String, Object> userInfo = null;
        JSONParser jsonParser = new JSONParser();
        try{
            JSONObject jsonObj = (JSONObject)jsonParser.parse(res.getBody());
            jsonObj = (JSONObject)jsonParser.parse(jsonObj.get("kakao_account").toString());
            userInfo = new HashMap<>();
            String kakaoId = (String) jsonObj.get("email");
            System.out.println("kakaoId 확인 : " + kakaoId);
            userInfo.put("id", jsonObj.get("email"));
        } catch (ParseException e){
            // 401 Unauthorized 발생 시 로그 출력
            System.out.println("Error: Unauthorized, invalid token");
            e.printStackTrace();
        }
        return userInfo;
    }

    @Transactional
    public String getKakaoAccessToken(String code) throws ParseException {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId); // 카카오 REST API 키
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON 파싱을 통해 access_token 추출
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(response.getBody());
        System.out.println("access token test in User Oauth " + jsonObject.get("access_token").toString());
        return jsonObject.get("access_token").toString();
    }

}
