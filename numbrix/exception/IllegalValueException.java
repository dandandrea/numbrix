package numbrix.exception;

import java.lang.Exception;

public class IllegalValueException extends BoardException {
    public IllegalValueException(String message) {
        super(message);
    }
}
