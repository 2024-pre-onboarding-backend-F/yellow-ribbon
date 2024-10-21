package wanted.ribbon.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wanted.ribbon.user.dto.*;
import wanted.ribbon.user.service.UserService;

import java.util.UUID;

@Tag(name = "Users", description = "Users API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService userService;

    @Operation(summary = "사용자 회원가입", description = "사용자가 ID와 PW로 회원가입합니다.")
    @ApiResponse(responseCode = "201", description = "CREATED")
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@Validated @RequestBody SignUpUserRequest request){
        SignUpResponse response = userService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "사용자 로그인", description = "사용자가 ID와 PW로 로그인합니다.")
    @ApiResponse(responseCode = "200", description = "OK")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        // 사용자 검증 로직 추가
        LoginResponseDto responseDto = userService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "사용자 프로필 확인", description = "사용자가 로그인 후 아이디, 경도, 위도, 점심 추천 기능 여부 등에 대해 확인이 가능합니다.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "BAD REQUEST")
    @GetMapping("/profile/{userId}")
    public ResponseEntity<ProfileResponse> getUserInfo(@PathVariable UUID userId, @RequestBody ProfileRequest request){
        ProfileResponse profile = userService.findByUser(userId, request);
        return ResponseEntity.ok(profile);
    }
    @Operation(summary = "사용자 프로필 업데이트", description = "사용자가 로그인 후 경도, 위도, 점심 추천 기능 여부 등에 대해 업데이트합니다.")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "BAD REQUEST")
    @PutMapping("/settings/{userId}")
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable UUID userId, @RequestBody UpdateUserRequest request){
        UpdateUserResponse updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }
}
