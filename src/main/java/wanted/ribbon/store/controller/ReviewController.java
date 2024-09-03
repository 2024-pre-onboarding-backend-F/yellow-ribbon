package wanted.ribbon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wanted.ribbon.store.dto.CreateReviewRequestDto;
import wanted.ribbon.store.dto.CreateReviewResponseDto;
import wanted.ribbon.store.service.ReviewService;
import wanted.ribbon.user.config.TokenProvider;

import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final TokenProvider tokenProvider;

    @PostMapping("/{storeId}")
    public ResponseEntity<CreateReviewResponseDto> createReview(@PathVariable Long storeId, @RequestHeader(value = "Authorization")String token, @RequestBody CreateReviewRequestDto requestDto) {
        String accessToken = token.split("Bearer ")[1];
        String id = tokenProvider.getId(accessToken); // 액세스토큰으로 id 반환
        CreateReviewResponseDto responseDto = reviewService.createReview(storeId, id, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
