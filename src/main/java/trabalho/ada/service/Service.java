package trabalho.ada.service;

import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import trabalho.ada.exception.BusinessException;
import trabalho.ada.model.Conta;

public class Service {

    @Inject
    JsonWebToken jwt;

    protected void verificaDonoDaConta(Conta conta){
        Long idClienteToken = Long.valueOf(jwt.getClaim("id").toString());
        Long idClienteConta = conta.getCliente().getId();

        if( (jwt.getGroups().contains("CLIENTE") ) && !( idClienteToken.equals(idClienteConta)) ){
            throw new BusinessException("Essa conta não pertence ao cliente logado.");
        }
    }
}
