package trabalho.ada.model;

import trabalho.ada.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transacao {
    private long id;
    private TipoTransacao tipo;
    private BigDecimal valor;
    private LocalDate dataHora;
    private Conta conta_origem;
    private Conta conta_destino;
}
