package wanted.ribbon.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRequest {
    private String id;
    private double lat;
    private double lon;
    private boolean recommend;
}
