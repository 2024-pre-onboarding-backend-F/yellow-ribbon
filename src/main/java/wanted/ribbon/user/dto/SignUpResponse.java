package wanted.ribbon.user.dto;

import java.util.UUID;

public record SignUpResponse(
        String message,
        UUID userId,
        String id

) { }
