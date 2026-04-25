package trabalho.ada.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class EnumExceptionMapper implements ExceptionMapper<InvalidFormatException> {

    @Override
    public Response toResponse(InvalidFormatException exception) {

        if (exception.getTargetType().isEnum()) {

            String campo = exception.getPath().get(0).getFieldName();

            Class<?> enumClass = exception.getTargetType();

            String valoresValidos = Arrays.stream(enumClass.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            Map<String, Object> erro = new HashMap<>();
            erro.put("erro", "Valor inválido para o campo '" + campo + "'");
            erro.put("valorRecebido", exception.getValue());
            erro.put("valoresAceitos", valoresValidos);

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(erro)
                    .build();
        }

        // fallback
        Map<String, String> erro = new HashMap<>();
        erro.put("erro", "Requisição inválida");

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(erro)
                .build();
    }
}
