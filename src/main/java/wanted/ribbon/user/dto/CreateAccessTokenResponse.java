package wanted.ribbon.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "Access Token 생성 요청 DTO")
public record CreateAccessTokenResponse(
        @Schema(description = "Access Token", example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJ5b3VuZ2pvby5kZXZAZ21haWwuY29tIiwiaWF0IjoxNzI1MzQ0ODM1LCJleHAiOjE3MjUzNTIwMzUsInN1YiI6IndhbnRlZCJ9.OuzQxp9fAfpJeyglQnVrvqt7jUcUVno5YADjQRBjMCs")
        String accessToken
) { }
