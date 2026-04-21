package trabalho.ada.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import trabalho.ada.dto.ErrorResponse;

@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException ex) {
        return Response.status(422)
                .entity(new ErrorResponse(ex.getMessage()))
                .build();
    }
}
