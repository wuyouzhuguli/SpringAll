package cc.mrbird.validate.code;

import org.springframework.security.core.AuthenticationException;

public class ValidateCodeException extends AuthenticationException {
    private static final long serialVersionUID = 5022575393500654458L;

    public ValidateCodeException(String message) {
        super(message);
    }
}
