package wanted.ribbon.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.ribbon.exception.ErrorCode;
import wanted.ribbon.exception.NotFoundException;
import wanted.ribbon.store.domain.Review;
import wanted.ribbon.store.domain.Store;
import wanted.ribbon.store.dto.CreateReviewRequestDto;
import wanted.ribbon.store.dto.CreateReviewResponseDto;
import wanted.ribbon.store.repository.ReviewRepository;
import wanted.ribbon.store.repository.StoreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public CreateReviewResponseDto createReview(Long storeId, CreateReviewRequestDto requestDto) {
        Store store = storeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND));
        Review review = Review.builder()
                .score(requestDto.score())
                .content(requestDto.content())
                .store(store)
                .build();
        reviewRepository.save(review);

        updateRating(store);

        CreateReviewResponseDto responseDto = new CreateReviewResponseDto(requestDto.score(), requestDto.content());
        return responseDto;
    }

    public void updateRating(Store store) {
        List<Review> reviewList = reviewRepository.findByStore_StoreId(store.getStoreId());

        double rating = reviewList.stream()
                .mapToDouble(Review::getScore)
                .average()
                .orElse(0.0);

        store.updateRating(rating);
    }
}
