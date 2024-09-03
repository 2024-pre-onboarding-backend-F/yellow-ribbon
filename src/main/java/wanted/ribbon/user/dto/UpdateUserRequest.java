package wanted.ribbon.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private double lat;
    private double lon;
    private boolean recommend;
}
