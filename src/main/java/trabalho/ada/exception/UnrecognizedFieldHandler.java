package trabalho.ada.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;

@Provider
public class UnrecognizedFieldHandler implements ExceptionMapper<UnrecognizedPropertyException> {

    @Override
    public Response toResponse(UnrecognizedPropertyException ex) {

        String field = ex.getPropertyName();

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(
                        List.of("o campo " + field + " não deve fazer parte do request")
                ))
                .build();
    }

    public record ErrorResponse(List<String> errors) {}
}