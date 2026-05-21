package trabalho.ada.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.exception.BusinessException;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.Conta;
import trabalho.ada.model.Transacao;
import trabalho.ada.repository.ContaRepository;
import trabalho.ada.resource.conta.dto.CreateContaRequest;

import java.math.BigDecimal;

@ApplicationScoped
public class ContaService extends Service{

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
        conta.setTipo(TipoConta.valueOf(request.tipo().toString()));
        conta.setNumero(numeroConta);
        conta.setCliente(cliente);
        conta.persist();

        return conta;
    }

    public Conta getConta(Long id){
        Conta conta = getRequiredConta(id);

        if(!contaPertenceAoCliente(conta)){
            throw new BusinessException(this.CONTA_NAO_PERTENCE_AO_CLIENTE);
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

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O valor do depósito deve ser maior que zero.");
        }

        Conta contaDestino = getRequiredConta(contaId);

        if(contaDestino.getTipo().equals(TipoConta.ELETRONICA)){
            throw new BusinessException("Conta do tipo ELETRONICA não permite depósitos.");
        }

        if(!contaPertenceAoCliente(contaDestino)){
            throw new BusinessException(this.CONTA_NAO_PERTENCE_AO_CLIENTE);
        }

        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

        return transacaoService.crate(TipoTransacao.DEPOSITO, valor, null, contaDestino);
    }

    public Transacao saque(BigDecimal valor, Long contaId){

        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("O valor do saque deve ser maior que zero.");
        }

        Conta contaOrigem = getRequiredConta(contaId);

        if(contaOrigem.getTipo().equals(TipoConta.ELETRONICA)){
            throw new BusinessException("Conta do tipo ELETRONICA não permite saques.");
        }

        if(!contaPertenceAoCliente(contaOrigem)){
            throw new BusinessException(this.CONTA_NAO_PERTENCE_AO_CLIENTE);
        }

        verificaSaldo(contaOrigem, valor);

        BigDecimal valorNegativo = valor.abs().negate();

        contaOrigem.setSaldo(contaOrigem.getSaldo().add(valorNegativo));

        return transacaoService.crate(TipoTransacao.SAQUE, valorNegativo, contaOrigem, null);
    }

    private void verificaSaldo(Conta conta, BigDecimal valor){
        if(valor.compareTo(conta.getSaldo()) > 0){
            String tipoTransacao;
            if(conta.getTipo().equals(TipoConta.ELETRONICA)){
                tipoTransacao = "a trasferência.";
            }else {
                tipoTransacao = "o saque.";
            }

            throw new BusinessException("Saldo insuficiente para realizar " + tipoTransacao);
        }
    }

    public Transacao transferencia(BigDecimal valor, Long contaOrigemId, Long contaDestinoId){

        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("O valor da transferência deve ser maior que zero.");
        }

        if(contaDestinoId.equals(contaOrigemId)){
            throw new BusinessException("A conta de origem é igual a conta de destino.");
        }

        Conta contaOrigem = getRequiredConta(contaOrigemId);
        Conta contaDestino = getRequiredConta(contaDestinoId);

        if(!contaPertenceAoCliente(contaOrigem)){
            throw new BusinessException(this.CONTA_NAO_PERTENCE_AO_CLIENTE);
        }

        verificaSaldo(contaOrigem, valor);

        contaOrigem.setSaldo(contaOrigem.getSaldo().add(valor.negate()));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

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
