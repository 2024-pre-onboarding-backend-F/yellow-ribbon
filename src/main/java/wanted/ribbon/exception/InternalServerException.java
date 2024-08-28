package wanted.ribbon.exception;

public class InternalServerException extends BaseException {

    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InternalServerException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
