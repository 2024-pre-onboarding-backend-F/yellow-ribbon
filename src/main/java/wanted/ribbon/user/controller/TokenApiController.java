package wanted.ribbon.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wanted.ribbon.user.dto.CreateAccessTokenRequest;
import wanted.ribbon.user.dto.CreateAccessTokenResponse;
import wanted.ribbon.user.service.TokenService;

@Tag(name = "Users", description = "Users API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class TokenApiController {
    private final TokenService tokenService;

    @Operation(summary = "토큰 생성", description = "Access Token을 생성하는 API")
    @ApiResponse(responseCode = "201", description = "CREATED")
    @PostMapping("/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request){
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateAccessTokenResponse(newAccessToken));
    }
}
