package wanted.ribbon.datapipe.mapper;

import org.springframework.jdbc.core.RowMapper;
import wanted.ribbon.store.domain.Category;
import wanted.ribbon.store.domain.Store;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StoreDataRowMapper implements RowMapper<Store> {

    @Override
    public Store mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Store.builder()
                .sigun(rs.getString("sigun"))
                .storeName(rs.getString("store_name"))
                .category(Category.valueOf(rs.getString("category")))
                .address(rs.getString("address"))
                .storeLat(rs.getDouble("store_lat"))
                .storeLon(rs.getDouble("store_lon"))
                .rating(rs.getDouble("rating"))
                .reviewCount(rs.getInt("review_count"))
                .build();
    }
}
