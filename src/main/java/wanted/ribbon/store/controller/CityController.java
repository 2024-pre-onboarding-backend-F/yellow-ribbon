package wanted.ribbon.store.controller;

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
public class CityController {
    private final CityService cityService;

    @GetMapping
    public ResponseEntity<List<CityResponseDto>> getCityList() {
        List<CityResponseDto> responseDto = cityService.getCityList();

        if(responseDto.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
