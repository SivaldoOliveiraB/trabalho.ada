package trabalho.ada.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.Conta;
import trabalho.ada.repository.ContaRepository;
import trabalho.ada.resource.conta.CreateContaRequest;

@ApplicationScoped
public class ContaService {

    @Inject
    ClienteService clienteService;

    @Inject
    ContaRepository contaRepository;

    public Conta create(CreateContaRequest request){
        Cliente cliente = clienteService.getRequiredCliente(request.cliente().id());

        Long seq = contaRepository.proximoNumero();
        String numeroConta = gerarNumeroConta(seq);

        Conta conta = new Conta();
        conta.setTipo(TipoConta.valueOf(request.tipo()));
        conta.setNumero(numeroConta);
        conta.setCliente(cliente);
        conta.persist();

        return conta;
    }

    public String gerarNumeroConta(Long sequencial) {
        int digito = (int) (sequencial % 3);

        if (digito == 0) {
            digito = 3;
        }

        return String.format("%04d-%d", sequencial, digito);
    }
}
