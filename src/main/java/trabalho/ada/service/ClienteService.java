package trabalho.ada.service;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.PageResult;

@ApplicationScoped
public class ClienteService {

    public PageResult<Cliente> list(int page, int size){
        var query = Cliente.findAll(Sort.by("nome"));
        var result = query.page(Page.of(page, size));
        return new PageResult<>(result.list(), page, size, result.count());
    }

    public Cliente findById(Long id){ return getRequiredCliente(id); }

    public Cliente getRequiredCliente(Long id) {
        Cliente cliente = Cliente.findById(id);
        if (cliente == null) {
            throw new NotFoundException("Cliente com o id " + id + " não encontrado");
        }
        return cliente;
    }
}
