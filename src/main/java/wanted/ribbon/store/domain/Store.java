package wanted.ribbon.store.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "stores", uniqueConstraints = {
        @UniqueConstraint(
                name="STORENAME_ADDRESS_UNIQUE",
                columnNames={"STORENAME","ADDRESS"}
        )})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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
    private double storeLat;

    @Column(nullable = false)
    private double storeLon;

    @Column(nullable = false, columnDefinition = "double default 0")
    private double rating;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer reviewCount;

    public void updateRating(double rating) {
        this.rating = rating;
    }
}
