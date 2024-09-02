package wanted.ribbon.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    // 클라이언트의 입력 값에 대한 일반적인 오류 (@PathVariable, @RequestParam가 잘못되었을 때)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "클라이언트의 입력 값을 확인해주세요."),
    // @RequestBody의 입력 값이 유효하지 않을 때
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터 값을 확인해주세요."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 엔티티입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // 사용자, jwt


    // 맛집 목록
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 맛집입니다."),

    // 맛집 상세

    // 평가

    // 데이터 파이프라인
    GENRESTRT_INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "API로 받은 응답이 없습니다."),
    GENRESTRT_PARSING_ERROR(HttpStatus.BAD_REQUEST, "API 응답을 파싱하는 데 실패했습니다."),
    GENRESTRT_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "공공데이터 API 호출 중 오류가 발생했습니다."),
    GENRESTRT_EMPTY_RESPONSE(HttpStatus.NOT_FOUND, "API 데이터가 존재하지 않습니다."),
    GENRESTRT_NO_VALID_ROWS(HttpStatus.NOT_FOUND, "추출된 행이 없습니다."),
    GENRESTRT_UNKNOWN_SERVICE(HttpStatus.NOT_FOUND, "요청한 서비스명이 아닙니다."),
    DATA_PIPE_TASKLET_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터 처리에 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
