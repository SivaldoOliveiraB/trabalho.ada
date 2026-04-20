package trabalho.ada.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.Conta;
import trabalho.ada.model.Transacao;
import trabalho.ada.repository.ContaRepository;
import trabalho.ada.resource.conta.CreateContaRequest;

import java.math.BigDecimal;

@ApplicationScoped
public class ContaService {

    @Inject
    ClienteService clienteService;

    @Inject
    ContaRepository contaRepository;

    @Inject
    TransacaoService transacaoService;

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

    public Conta getRequiredConta(Long id) {
        Conta conta = Conta.findById(id);
        if (conta == null) {
            throw new NotFoundException("Conta com o id " + id + " não encontrada");
        }
        return conta;
    }

    public Conta deposita(BigDecimal valor, Long contaId){
        Conta contaDestino = getRequiredConta(contaId);
        Transacao transacao = transacaoService.crate(TipoTransacao.DEPOSITO, valor, contaDestino);
        contaDestino.setDeposito(transacao);

        return contaDestino;
    }

    public String gerarNumeroConta(Long sequencial) {
        int digito = (int) (sequencial % 3);

        if (digito == 0) {
            digito = 3;
        }

        return String.format("%04d-%d", sequencial, digito);
    }
}
