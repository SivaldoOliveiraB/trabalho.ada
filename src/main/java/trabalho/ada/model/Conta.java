package trabalho.ada.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.Formula;
import trabalho.ada.enums.TipoConta;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    //@OneToMany(mappedBy = "contaOrigem")
    //private List<Transacao> saques = new ArrayList<>();

    //@OneToMany(mappedBy = "contaDestino")
    //private List<Transacao> depositos = new ArrayList<>();

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

    /*
    public Transacao getDeposito() {
        return this.depositos.get(0);
    }

    public void setDeposito(Transacao deposito) {
        this.depositos.add(deposito);
    }

     */
}
