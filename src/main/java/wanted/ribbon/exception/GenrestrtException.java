package wanted.ribbon.exception;

public class GenrestrtException extends BaseException {
    public GenrestrtException(ErrorCode errorCode) {
        super(errorCode);
    }

    public GenrestrtException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
