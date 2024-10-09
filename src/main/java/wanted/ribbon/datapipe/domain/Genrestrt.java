package wanted.ribbon.datapipe.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@AllArgsConstructor (access = AccessLevel.PRIVATE) // 모든 필드를 초기화하는 생성자
@Builder
@Table(name = "genrestrts", uniqueConstraints = {
        @UniqueConstraint(
                name="BIZPLCNM_REFINEROADNMADDR_UNIQUE",
                columnNames={"bizplcNm","refineRoadnmAddr"}
        )})
public class Genrestrt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long genrestrtId; // 일련번호

    private String sigunNm; // 시군명
    private String sigunCd; // 시군코드
    private String bizplcNm; // 사업장명
    private Date licensgDe; // 인허가일자
    private String bsnStateNm; // 영업상태명
    private Date clsbizDe; // 폐업일자
    private Double locplcAr; // 소재지면적
    private String gradFacltDivNm; // 급수시설구분명
    private Long maleEnflpsnCnt; // 남성종사자수
    private Integer yy; // 년도
    private String multiUseBizestblYn; // 다중이용업소여부
    private String gradDivNm; // 등급구분명
    private Double totFacltScale; // 총시설규모
    private Long femaleEnflpsnCnt; // 여성종사자수
    private String bsnsiteCircumfrDivNm; // 영업장주변구분명
    private String sanittnIndutypeNm; // 위생업종명
    private String sanittnBizcondNm; // 위생업태명
    private Long totEmplyCnt; // 총종업원수
    private String refineRoadnmAddr; // 소재지 도로명주소
    private String refineLotnoAddr; // 소재지 지번주소
    private String refineZipCd; //소재지 우편번호
    private Double refineWgs84Lat; // WGS84위도
    private Double refineWgs84Logt; // WGS84경도

    @Builder.Default
    private Boolean processed = false; // 데이터 배치 처리 여부를 나타내는 필드

    // 업데이트
   public void updateExistingGenrestrt(Genrestrt newData){
       this.sigunNm = newData.sigunNm;
       this.bizplcNm = newData.bizplcNm;
       this.bsnStateNm = newData.bsnStateNm;
       this.sanittnIndutypeNm = newData.sanittnIndutypeNm;
       this.sanittnBizcondNm = newData.sanittnBizcondNm;
       this.refineRoadnmAddr = newData.refineRoadnmAddr;
       this.refineWgs84Lat = newData.refineWgs84Lat;
       this.refineWgs84Logt = newData.refineWgs84Logt;
   }
}
