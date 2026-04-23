package trabalho.ada.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class AuthenticationException extends WebApplicationException {

    public AuthenticationException(String message) {
        super(message, Response.Status.UNAUTHORIZED);
    }
}