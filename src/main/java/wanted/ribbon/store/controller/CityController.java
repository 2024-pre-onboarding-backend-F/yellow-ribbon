package wanted.ribbon.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wanted.ribbon.store.dto.CityResponseDto;
import wanted.ribbon.store.service.CityService;

import java.util.List;

@RestController
@RequestMapping("/api/city")
@RequiredArgsConstructor
@Tag(name = "City", description = "City API")
public class CityController {
    private final CityService cityService;

    @GetMapping
    @Operation(summary = "시군구 목록 조회", description = "시군구 목록 조회 시 사용하는 API")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<List<CityResponseDto>> getCityList() {
        List<CityResponseDto> responseDto = cityService.getCityList();

        if(responseDto.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
