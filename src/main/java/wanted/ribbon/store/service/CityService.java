package wanted.ribbon.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wanted.ribbon.store.domain.City;
import wanted.ribbon.store.dto.CityResponseDto;
import wanted.ribbon.store.repository.CityRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;

    public List<CityResponseDto> getCityList() {
        List<City> cityList = cityRepository.findAll();
        List<CityResponseDto> responseDto = cityList.stream()
                .map(list -> new CityResponseDto(list.getDoSi(), list.getSgg()))
                .collect(Collectors.toList());
        return responseDto;
    }
}
