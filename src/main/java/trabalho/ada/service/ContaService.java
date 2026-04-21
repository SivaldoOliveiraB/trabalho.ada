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

    public Transacao deposito(BigDecimal valor, Long contaId){
        Conta contaDestino = getRequiredConta(contaId);
        Conta contaOrigem = null;

        return transacaoService.crate(TipoTransacao.DEPOSITO, valor, contaOrigem, contaDestino);
    }

    public Transacao saque(BigDecimal valor, Long contaId){
        Conta contaDestino = null;
        Conta contaOrigem = getRequiredConta(contaId);

        BigDecimal valorNegativo = valor.abs().negate();

        return transacaoService.crate(TipoTransacao.SAQUE, valorNegativo, contaOrigem, contaDestino);
    }

    public String gerarNumeroConta(Long sequencial) {
        int digito = (int) (sequencial % 3);

        if (digito == 0) {
            digito = 3;
        }

        return String.format("%04d-%d", sequencial, digito);
    }
}
