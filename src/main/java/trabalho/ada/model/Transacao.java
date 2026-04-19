package trabalho.ada.model;

import jakarta.persistence.*;
import trabalho.ada.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacao")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoTransacao tipo;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "conta_origem_id")
    private Conta conta_origem;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "conta_destino_id")
    private Conta conta_destino;
}
