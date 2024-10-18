package wanted.ribbon.store.domain;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Table(name = "stores", uniqueConstraints = {
        @UniqueConstraint(
                name = "STORENAME_ADDRESS_UNIQUE",
                columnNames = {"STORENAME", "ADDRESS"}
        )})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private String sigun;

    @Column(nullable = false)
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private double storeLon; // 경도 (세로선=동서)

    @Column(nullable = false)
    private double storeLat; // 위도 (가로선=남북)

    @Column(nullable = false, columnDefinition = "POINT SRID 4326")
    private Point location; // Point(경도, 위도)

    @Column(nullable = false, columnDefinition = "double default 0")
    private double rating;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer reviewCount;

    public void updateRating(double rating) {
        this.rating = rating;
    }

    public void addReviewCount() {
        this.reviewCount++;
    }

    // GeometryFactory는 thread-safe, stateless 하므로, static으로 선언하여 성능 개선
    private static final GeometryFactory geometryFactory = new GeometryFactory();

    // storeLon, storeLat으로 location(Point) 설정
    public void setLocation(double storeLon, double storeLat) {
        this.location = geometryFactory.createPoint(new Coordinate(storeLon, storeLat));
        this.location.setSRID(4326);
    }

    private void ensureLocation() {
        this.setLocation(this.storeLon, this.storeLat);
    }

    // @PrePersist: 엔티티가 처음 저장될 때 location 필드 설정
    @PrePersist
    public void prePersist() {
        ensureLocation();
    }

    // @PreUpdate: 엔티티가 업데이트될 때 (경도/위도 값이 변경되면) 자동으로 location 필드 갱신
    @PreUpdate
    public void preUpdate() {
        ensureLocation();
    }

    @Builder
    public Store(String sigun, String storeName, Category category, String address, double storeLon, double storeLat, double rating, Integer reviewCount) {
        this.sigun = sigun;
        this.storeName = storeName;
        this.category = category;
        this.address = address;
        this.storeLon = storeLon;
        this.storeLat = storeLat;
        this.setLocation(storeLon, storeLat);
        this.rating = rating;
        this.reviewCount = reviewCount;
    }
}
