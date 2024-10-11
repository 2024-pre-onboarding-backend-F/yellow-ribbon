package wanted.ribbon.datapipe.dto;

import java.util.Date;
import java.util.List;

public record GyeongGiList(String message,
                           Long total,
                           int newCount,
                           int updatedCount,
                           int unchangedCount,
                           List<GyeongGiApiResponse> gyeongGiApiResponses) {
    public record GyeongGiApiResponse(String sigunNm,
                                      String sigunCd,
                                      String bizplcNm,
                                      Date licensgDe,
                                      String bsnStateNm,
                                      Date clsbizDe,
                                      Double locplcAr,
                                      String gradFacltDivNm,
                                      Long maleEnflpsnCnt,
                                      Integer yy,
                                      String multiUseBizestblYn,
                                      String gradDivNm,
                                      Double totFacltScale,
                                      Long femaleEnflpsnCnt,
                                      String bsnsiteCircumfrDivNm,
                                      String sanittnIndutypeNm,
                                      String sanittnBizcondNm,
                                      Long totEmplyCnt,
                                      String refineRoadnmAddr,
                                      String refineLotnoAddr,
                                      String refineZipCd,
                                      Double refineWgs84Lat,
                                      Double refineWgs84Logt){}
}
