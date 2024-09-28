package wanted.ribbon.datapipe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GenrestrtApiResponse {

    @JsonProperty("Genrestrtchifood")
    private List<Genrestrtchifood> genrestrtchifood; // 중국식

    @JsonProperty("Genrestrtjpnfood")
    private List<Genrestrtjpnfood> genrestrtjpnfood; // 일식

    @JsonProperty("Genrestrtlunch")
    private List<Genrestrtlunch> genrestrtlunch; // 김밥(도시락)

    @Data
    public static class Genrestrtchifood {
        private List<HeadItem> head;
        private List<Row> row;
    }

    @Data
    public static class Genrestrtjpnfood {
        private List<HeadItem> head;
        private List<Row> row;
    }

    @Data
    public static class Genrestrtlunch {
        private List<HeadItem> head;
        private List<Row> row;
    }


    @Data
    public static class HeadItem {
        @JsonProperty("list_total_count")
        private Integer listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("api_version")
        private String apiVersion;

        @Data
        public static class Result {
            @JsonProperty("CODE")
            private String code;

            @JsonProperty("MESSAGE")
            private String message;
        }
    }

    @Data
    public static class Row {
        @JsonProperty("SIGUN_NM")
        private String sigunNm;

        @JsonProperty("SIGUN_CD")
        private String sigunCd;

        @JsonProperty("BIZPLC_NM")
        private String bizplcNm;

        @JsonProperty("LICENSG_DE")
        private String licensgDe;

        @JsonProperty("BSN_STATE_NM")
        private String bsnStateNm;

        @JsonProperty("CLSBIZ_DE")
        private String clsbizDe;

        @JsonProperty("LOCPLC_AR")
        private String locplcAr;

        @JsonProperty("GRAD_FACLT_DIV_NM")
        private String gradFacltDivNm;

        @JsonProperty("MALE_ENFLPSN_CNT")
        private Integer maleEnflpsnCnt;

        @JsonProperty("YY")
        private Integer yy;

        @JsonProperty("MULTI_USE_BIZESTBL_YN")
        private String multiUseBizestblYn;

        @JsonProperty("GRAD_DIV_NM")
        private String gradDivNm;

        @JsonProperty("TOT_FACLT_SCALE")
        private String totFacltScale;

        @JsonProperty("FEMALE_ENFLPSN_CNT")
        private Integer femaleEnflpsnCnt;

        @JsonProperty("BSNSITE_CIRCUMFR_DIV_NM")
        private String bsnsiteCircumfrDivNm;

        @JsonProperty("SANITTN_INDUTYPE_NM")
        private String sanittnIndutypeNm;

        @JsonProperty("SANITTN_BIZCOND_NM")
        private String sanittnBizcondNm;

        @JsonProperty("TOT_EMPLY_CNT")
        private Integer totEmplyCnt;

        @JsonProperty("REFINE_LOTNO_ADDR")
        private String refineLotnoAddr;

        @JsonProperty("REFINE_ROADNM_ADDR")
        private String refineRoadnmAddr;

        @JsonProperty("REFINE_ZIP_CD")
        private String refineZipCd;

        @JsonProperty("REFINE_WGS84_LOGT")
        private String refineWgs84Logt;

        @JsonProperty("REFINE_WGS84_LAT")
        private String refineWgs84Lat;
    }
}
