package wanted.ribbon.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wanted.ribbon.global.response.ResponseCode;
import wanted.ribbon.global.response.ResponseDto;
import wanted.ribbon.user.domain.SocialType;
import wanted.ribbon.user.domain.User;
import wanted.ribbon.user.dto.LoginRequestDto;
import wanted.ribbon.user.service.UserOauthService;

import java.util.ArrayList;
import java.util.Map;

@Tag(name = "Users", description = "Users API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth")
public class UserOauthController {
    private final UserOauthService userAuthService;

    @Operation(summary = "사용자 카카오톡 회원가입", description = "사용자가 카카오톡 ID와 PW로 회원가입합니다.")
    @ApiResponse(responseCode = "201", description = "CREATED")
    @GetMapping("/kakao/login")
    public ResponseEntity<ResponseDto> kakaoLogin(@RequestParam("code") String code) throws ParseException {
        // 액세스 토큰 받아오기
        String accessToken = userAuthService.getKakaoAccessToken(code);
        System.out.println("access_token : " + accessToken);

        // 액세스 토큰을 사용해 카카오 유저 정보 받아오기
        Map<String, Object> userInfo = userAuthService.getKakaoUserInfo(accessToken);

        if(userInfo != null && userInfo.get("id") != null){
            String id = userInfo.get("id").toString();
            System.out.println("카카오 로그인 유저 ID: " + id);
            LoginRequestDto userLoginDto = userAuthService.loginUser(id, SocialType.KAKAO);

            if(userLoginDto != null){
                // 유저 등록 또는 로그인 처리
                User user = userAuthService.registedUser(id, SocialType.KAKAO);
                System.out.println("사용자 저장 완료: " + user.getId());

                // 인증 정보를 설정하고 세션에 저장
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userLoginDto.getId(), null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // 로그인 성공 후 리다이렉션이나 응답 반환
                return ResponseEntity.ok(ResponseDto.of(ResponseCode.LOGIN_SUCCESS, user));

            }
        }
        return ResponseEntity.badRequest().body(ResponseDto.of(ResponseCode.LOGIN_FAIL));
    }
}
