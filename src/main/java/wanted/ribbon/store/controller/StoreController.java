package wanted.ribbon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wanted.ribbon.store.dto.StoreResponseDto;
import wanted.ribbon.store.service.StoreService;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<List<StoreResponseDto>> getStoreList(@RequestParam(value = "lat") double lat,
                                                               @RequestParam(value = "lon") double lon,
                                                               @RequestParam(value = "range") double range,
                                                               @RequestParam(value = "orderBy", defaultValue = "distance", required = false) String orderBy) {

        List<StoreResponseDto> storeList = storeService.findStores(lat, lon, range, orderBy);

        if (storeList.isEmpty())
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(storeList);
    }
}
