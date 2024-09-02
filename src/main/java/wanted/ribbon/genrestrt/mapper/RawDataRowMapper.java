package wanted.ribbon.genrestrt.mapper;

import org.springframework.jdbc.core.RowMapper;
import wanted.ribbon.genrestrt.dto.RawData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RawDataRowMapper implements RowMapper<RawData> {

    @Override
    public RawData mapRow(ResultSet rs, int rowNum) throws SQLException {
        return RawData.builder()
                .sigunNm(rs.getString("sigun_nm"))
                .bizplcNm(rs.getString("bizplc_nm"))
                .sanittnBizcondNm(rs.getString("sanittn_bizcond_nm"))
                .refineRoadnmAddr(rs.getString("refine_roadnm_addr"))
                .refineWgs84Lat(rs.getDouble("refine_wgs84lat"))
                .refineWgs84Logt(rs.getDouble("refine_wgs84logt"))
                .build();
    }
}
