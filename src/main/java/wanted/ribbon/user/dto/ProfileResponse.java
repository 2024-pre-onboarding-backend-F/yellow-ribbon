package wanted.ribbon.user.dto;

public record ProfileResponse(
    String message,
    String id,
    double lat,
    double lon,
    boolean recommend
) { }
