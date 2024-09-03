package wanted.ribbon.user.dto;

import java.util.UUID;

public record UpdateUserResponse(
    String message,
    UUID userId,
    double lat,
    double lon,
    boolean recommend
) { }

