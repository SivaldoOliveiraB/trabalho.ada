package trabalho.ada.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.exception.BusinessException;
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

    @Inject
    JsonWebToken jwt;

    public Conta create(CreateContaRequest request){
        Cliente cliente = clienteService.getRequiredCliente(request.cliente().id());

        Long seq = contaRepository.proximoNumero();
        String numeroConta = gerarNumeroConta(seq);

        Conta conta = new Conta();
        conta.setTipo(TipoConta.valueOf(request.tipo().toString()));
        conta.setNumero(numeroConta);
        conta.setCliente(cliente);
        conta.persist();

        return conta;
    }

    public Conta getConta(Long id){
        Conta conta = getRequiredConta(id);
        Long idClienteToken = Long.valueOf(jwt.getClaim("id").toString());
        Long idClienteConta = conta.getCliente().getId();

        if( (jwt.getGroups().contains("CLIENTE") ) && !( idClienteToken.equals(idClienteConta)) ){
            throw new BusinessException("A conta consultada não pertence ao cliente logado.");
        }

        conta.setTrasacoes(Transacao.findByContaId(id));

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

    public Transacao transferencia(BigDecimal valor, Long contaOrigemId, Long contaDestinoId){
        Conta contaOrigem = getRequiredConta(contaOrigemId);
        Conta contaDestino = getRequiredConta(contaDestinoId);

        return transacaoService.crate(TipoTransacao.TRANSFERENCIA, valor, contaOrigem, contaDestino);
    }

    public String gerarNumeroConta(Long sequencial) {
        int digito = (int) (sequencial % 3);

        if (digito == 0) {
            digito = 3;
        }

        return String.format("%04d-%d", sequencial, digito);
    }
}
