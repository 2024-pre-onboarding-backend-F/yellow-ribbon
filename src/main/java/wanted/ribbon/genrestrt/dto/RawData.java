package wanted.ribbon.genrestrt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawData {
    private String sigunNm;
    private String bizplcNm;
    private String sanittnBizcondNm;
    private String refineRoadnmAddr;
    private Double refineWgs84Lat;
    private Double refineWgs84Logt;
}

