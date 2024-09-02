package wanted.ribbon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wanted.ribbon.store.dto.StoreDetailResponseDto;
import wanted.ribbon.store.service.StoreService;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDetailResponseDto> getStoreDetail(@PathVariable Long storeId) {
        StoreDetailResponseDto responseDto = storeService.getStoreDetail(storeId);
        return ResponseEntity.ok().body(responseDto);
    }

}
