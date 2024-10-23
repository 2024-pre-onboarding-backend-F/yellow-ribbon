package wanted.ribbon.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Review", description = "Review API")
public class ReviewController {
    private final ReviewService reviewService;
    private final TokenProvider tokenProvider;

    @PostMapping("/{storeId}")
    @Operation(summary = "맛집 평가 생성", description = "맛집 평가 생성 시 사용하는 API")
    @ApiResponse(responseCode = "201", description = "Created")
    public ResponseEntity<CreateReviewResponseDto> createReview(
            @Parameter(description = "평가할 맛집 식별번호") @PathVariable Long storeId,
            @RequestHeader(value = "Authorization")String token,
            @RequestBody CreateReviewRequestDto requestDto) {
        String accessToken = token.split("Bearer ")[1];
        String id = tokenProvider.getId(accessToken); // 액세스토큰으로 id 반환
        CreateReviewResponseDto responseDto = reviewService.createReview(storeId, id, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
