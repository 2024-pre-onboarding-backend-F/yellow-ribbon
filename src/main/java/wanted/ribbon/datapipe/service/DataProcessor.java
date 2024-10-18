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
    private Category parseCategory(String category) {
        if (category == null || category.isEmpty()) {
            return Category.valueOf("카테고리명 정보 없음");
        }
        return switch (category.trim()) {
            case "김밥(도시락)" -> Category.Lunch;
            case "일식" -> Category.Japanese;
            case "중국식" -> Category.Chinese;
            case "까페" -> Category.Cafe;
            case "이동조리" -> Category.Movmntcook;
            case "탕류(보신용)" -> Category.Soup;
            case "패스트푸드" -> Category.Fastfood;
            case "횟집" -> Category.Sashimi;
            case "뷔페식" -> Category.Buff;
            case "복어취급" -> Category.Fugu;
            case "정종/대포집/소주방" -> Category.Pub;
            case "출장조리" -> Category.Bsrpcook;
            case "전통찻집" -> Category.Tratearm;
            default -> Category.ETC;
        };
    }
}
