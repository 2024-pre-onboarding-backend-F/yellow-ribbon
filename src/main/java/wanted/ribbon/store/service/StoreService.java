package wanted.ribbon.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wanted.ribbon.exception.ErrorCode;
import wanted.ribbon.exception.NotFoundException;
import wanted.ribbon.store.domain.Review;
import wanted.ribbon.store.domain.Store;
import wanted.ribbon.store.dto.ReviewListResponseDto;
import wanted.ribbon.store.dto.StoreDetailResponseDto;
import wanted.ribbon.store.repository.ReviewRepository;
import wanted.ribbon.store.repository.StoreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final ReviewRepository reviewRepository;

    public StoreDetailResponseDto getStoreDetail(Long storeId) {
        Store store = storeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.STORE_NOT_FOUND));
        List<Review> reviewList = reviewRepository.findByStore_StoreId(storeId);
        List<ReviewListResponseDto> reviewListResponseDto = reviewList.stream()
                .map(list -> new ReviewListResponseDto(list.getScore(), list.getContent()))
                .collect(Collectors.toList());

        StoreDetailResponseDto responseDto = new StoreDetailResponseDto(
                storeId,
                store.getSigun(),
                store.getStoreName(),
                store.getCategory(),
                store.getAddress(),
                store.getStoreLat(),
                store.getStoreLon(),
                store.getRating(),
                reviewListResponseDto
        );

        return responseDto;
    }
}
