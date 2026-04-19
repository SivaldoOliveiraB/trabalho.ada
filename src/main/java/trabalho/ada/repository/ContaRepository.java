package trabalho.ada.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class ContaRepository {

    @Inject
    EntityManager em;

    public Long proximoNumero() {
        return ((Number) em
                .createNativeQuery("SELECT nextval('conta_seq')")
                .getSingleResult())
                .longValue();
    }
}