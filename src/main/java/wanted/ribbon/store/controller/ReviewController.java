package wanted.ribbon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wanted.ribbon.store.dto.CreateReviewRequestDto;
import wanted.ribbon.store.dto.CreateReviewResponseDto;
import wanted.ribbon.store.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{storeId}")
    public ResponseEntity<CreateReviewResponseDto> createReview(@PathVariable Long storeId, @RequestBody CreateReviewRequestDto requestDto) {
        CreateReviewResponseDto responseDto = reviewService.createReview(storeId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
