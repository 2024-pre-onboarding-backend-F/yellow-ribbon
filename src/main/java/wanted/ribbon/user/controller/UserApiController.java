package wanted.ribbon.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import wanted.ribbon.user.dto.SignUpUserRequest;
import wanted.ribbon.user.dto.SignUpUserResponse;
import wanted.ribbon.user.service.UserService;

import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/user")
public class UserApiController {
    private final UserService userService;

    // 회원가입
    @PostMapping
    public ResponseEntity<UUID> signUp(@Validated @RequestBody SignUpUserRequest request){
        UUID response = userService.save(request); // 회원가입 메서드 호출
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<SignUpUserResponse> login(){
        return ResponseEntity.status(HttpStatus.OK).body(new SignUpUserResponse("Login successful", "id"));
    }


}
