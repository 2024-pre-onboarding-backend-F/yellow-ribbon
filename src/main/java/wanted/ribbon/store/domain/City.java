package wanted.ribbon.store.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "city")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long cityId;

    @Column(nullable = false)
    private String doSi;

    @Column(nullable = false)
    private String sgg;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lon;
}
