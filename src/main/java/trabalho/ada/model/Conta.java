package trabalho.ada.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.Formula;
import trabalho.ada.enums.TipoConta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "conta")
public class Conta extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoConta tipo;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Formula("(SELECT s.saldo FROM view_saldo s WHERE s.numero = numero)")
    private BigDecimal saldo;

    @Transient
    private List<Transacao> trasacoes;

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public TipoConta getTipo() {
        return tipo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public BigDecimal getSaldo(){
        return saldo;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setTipo(TipoConta tipo) {
        this.tipo = tipo;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public List<Transacao> getTrasacoes() {
        return trasacoes;
    }

    public void setTrasacoes(List<Transacao> trasacoes) {
        this.trasacoes = trasacoes;
    }

    public List<Transacao> getTransacoesHoje() {
        if (trasacoes == null || trasacoes.isEmpty()) {
            return List.of();
        }

        LocalDate hoje = LocalDate.now();

        return trasacoes.stream()
                .filter(t -> t.getDataHora().toLocalDate().equals(hoje))
                .toList();
    }

}
