package wanted.ribbon.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import wanted.ribbon.user.domain.User;
import wanted.ribbon.user.dto.SignUpUserRequest;
import wanted.ribbon.user.dto.UpdateUserRequest;
import wanted.ribbon.user.dto.UserLoginRequestDto;
import wanted.ribbon.user.dto.UserLoginResponseDto;
import wanted.ribbon.user.service.UserService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserApiController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UUID> signUp(@Validated @RequestBody SignUpUserRequest request){
        UUID response = userService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponseDto> login(@RequestBody UserLoginRequestDto requestDto) {
        // 사용자 검증 로직 추가
        UserLoginResponseDto responseDto = userService.login(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<User> getUserInfo(@PathVariable UUID userId){
        User user = userService.findByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/settings/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable UUID userId, @RequestBody UpdateUserRequest request){
        User updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }
}
