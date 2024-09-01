package wanted.ribbon.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wanted.ribbon.exception.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(e.getErrorCode(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getErrorCode(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getErrorCode(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException e) {
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(e.getErrorCode(), e.getErrorCode().getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getErrorCode(), e.getErrorCode().getMessage()));
    }

    // 필수 @RequestParam이 입력되지 않았을 때
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParams(MissingServletRequestParameterException e) {
        String paramName = e.getParameterName();
        String errorMessage = "필수 파라미터 '" + paramName + "'가 누락되었습니다.";

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE, errorMessage));
    }
}
