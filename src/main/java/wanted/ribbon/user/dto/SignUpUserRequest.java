package wanted.ribbon.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpUserRequest {
    @NotBlank
    @Size(max = 50)
    private String id;

//    @NotBlank
    @Size(max = 200)
    private String password;
}
