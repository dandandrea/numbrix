package numbrix.exception;

import java.lang.Exception;

public class IllegalPositionException extends BoardException {
    public IllegalPositionException(String message) {
        super(message);
    }
}
