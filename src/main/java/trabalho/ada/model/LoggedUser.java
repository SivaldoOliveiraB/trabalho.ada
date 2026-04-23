package trabalho.ada.model;

import trabalho.ada.enums.Role;

public record LoggedUser(Long id, String email, String name, Role role) {

    public boolean isGerente() {
        return role.equals(Role.GERENTE);
    }

    public boolean isClient() { return role.equals(Role.CLIENTE); }
}
