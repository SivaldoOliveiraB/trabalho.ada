package trabalho.ada.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.Conta;
import trabalho.ada.resource.conta.CreateContaRequest;

@ApplicationScoped
public class ContaService {

    @Inject
    ClienteService clienteService;

    public Conta create(CreateContaRequest request){
        Cliente cliente = clienteService.getRequiredCliente(request.cliente().id());

        Conta conta = new Conta();
        conta.setTipo(TipoConta.valueOf(request.tipo()));
        conta.setNumero("0020-0");
        conta.setCliente(cliente);
        conta.persist();

        return conta;
    }
}
