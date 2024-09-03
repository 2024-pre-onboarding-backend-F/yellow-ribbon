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
import wanted.ribbon.user.domain.User;
import wanted.ribbon.user.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateReviewResponseDto createReview(Long storeId, String id, CreateReviewRequestDto requestDto) {
        Store store = storeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND));
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ENTITY_NOT_FOUND));

        Review review = Review.builder()
                .score(requestDto.score())
                .content(requestDto.content())
                .store(store)
                .user(user)
                .build();
        reviewRepository.save(review);

        // 점수 입력되면 해당 맛집의 평점 update
        updateRating(store);

        CreateReviewResponseDto responseDto = new CreateReviewResponseDto(id, requestDto.score(), requestDto.content());
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
