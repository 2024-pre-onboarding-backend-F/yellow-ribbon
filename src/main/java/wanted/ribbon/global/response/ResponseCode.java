package wanted.ribbon.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ResoibseCode
 * - 도메인 별로 나누어 관리
 * - [동사_목적어_SUCCESS] 형태로 생성
 * */

@Getter
@AllArgsConstructor
public enum ResponseCode {
    // User
    LOGIN_SUCCESS(200, "U001", "로그인에 성공하였습니다."),
    GET_USERPROFILE_SUCCESS(200, "U002", "회원 프로필을 조회하였습니다."),
    EDIT_PROFILE_SUCCESS(200, "U003", "회원 프로필을 수정하였습니다."),
    LOGIN_FAIL(200, "U004", "로그인에 실패하였습니다."),
    SAVE_PROFILE_SUCCESS(200, "U005", "회원 프로필을 저장하였습니다."),
    DELETE_SUCCESS(200, "U006", "회원 탈퇴에 성공하였습니다."),
    DELETE_FAIL(200, "U007", "회원 탈퇴에 실패했습니다."),
    CHECK_TOKEN_SUCCESS(200, "U008", "유효한 토큰입니다."),
    CHECK_TOKEN_FAIL(200, "U009", "만료된 토큰입니다.")
    ;

    private final int status;
    private final String code;
    private final String message;
}
