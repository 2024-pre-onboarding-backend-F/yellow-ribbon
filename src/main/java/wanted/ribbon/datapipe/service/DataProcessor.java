package wanted.ribbon.datapipe.service;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import wanted.ribbon.datapipe.dto.RawData;
import wanted.ribbon.store.domain.Category;
import wanted.ribbon.store.domain.Store;

@Component
public class DataProcessor implements ItemProcessor<RawData, Store> {

    @Override
    public Store process(final RawData rawData) {
        return Store.builder()
                .sigun(rawData.getSigunNm() != null ? rawData.getSigunNm() : "시군명 정보 없음")
                .storeName(rawData.getBizplcNm() != null ? rawData.getBizplcNm() : "업장명 정보 없음")
                .category(parseCategory(rawData.getSanittnBizcondNm()))
                .address(rawData.getRefineRoadnmAddr() != null ? rawData.getRefineRoadnmAddr() : "도로명주소 정보 없음")
                .storeLat(rawData.getRefineWgs84Lat() != null ? rawData.getRefineWgs84Lat() : 0.0)
                .storeLon(rawData.getRefineWgs84Logt() != null ? rawData.getRefineWgs84Logt() : 0.0)
                .rating(0.0) // 가게 평점
                .reviewCount(0) // 리뷰수
                .build();
    }
    // 카테고리명 이름 설정
    private Category parseCategory(String category){
        if (category == null || category.isEmpty()){
            return Category.valueOf("카테고리명 정보 없음");
        }
         // 실제 카테고리 값을 받아오는 경우, 카테고리 설정에 맞는지 확인하고 매핑
        switch (category.trim()){
            case "김밥(도시락)":
                return Category.Lunch;
            case "일식":
                return Category.Japanese;
            case "중국식":
                return Category.Chinese;
            default:
                return Category.valueOf(category); // 입력받은 값 그대로 매핑
        }
    }
}
