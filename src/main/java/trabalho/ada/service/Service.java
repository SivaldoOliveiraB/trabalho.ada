package trabalho.ada.service;

import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import trabalho.ada.exception.BusinessException;
import trabalho.ada.model.Conta;

public class Service {

    @Inject
    JsonWebToken jwt;

    final String CONTA_NAO_PERTENCE_AO_CLIENTE = "Essa conta não pertence ao cliente logado.";

    public boolean contaPertenceAoCliente(Conta conta){
        Long idClienteToken = Long.valueOf(jwt.getClaim("id").toString());
        Long idClienteConta = conta.getCliente().getId();

        return (!jwt.getGroups().contains("CLIENTE")) || idClienteToken.equals(idClienteConta);
    }
}
