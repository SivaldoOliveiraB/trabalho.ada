package integracao;


import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import trabalho.ada.model.Cliente;
import trabalho.ada.resource.cliente.dto.CreateClienteRequestDTO;
import trabalho.ada.resource.cliente.dto.UpdateClienteRequestDTO;
import trabalho.ada.service.ClienteService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class ClienteResourceTest {

    @InjectMock
    ClienteService clienteService;

    @Test
    @TestSecurity(user = "gerente", roles = {"GERENTE"})
    void deveCriarCliente() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Alice Souza");
        cliente.setEmail("alice@banco.com");

        Mockito.when(clienteService.create(Mockito.any(CreateClienteRequestDTO.class)))
                .thenReturn(cliente);

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "nome": "Alice Souza",
                        "cpf": "123.456.789-00",
                        "email": "alice@banco.com",
                        "senha": "senha123"
                    }
                    """)
                .when()
                .post("/clientes")
                .then()
                .statusCode(201)
                .body("id", equalTo(1))
                .body("nome", equalTo("Alice Souza"))
                .body("email", equalTo("alice@banco.com"));
    }

    @Test
    @TestSecurity(user = "gerente", roles = {"GERENTE"})
    void deveBuscarClientePorId() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Alice Souza");
        cliente.setEmail("alice@banco.com");

        Mockito.when(clienteService.getRequiredCliente(1L))
                .thenReturn(cliente);

        given()
                .when()
                .get("/clientes/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("nome", equalTo("Alice Souza"))
                .body("email", equalTo("alice@banco.com"));
    }

    @Test
    @TestSecurity(user = "gerente", roles = {"GERENTE"})
    void deveAtualizarCliente() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Alice Souza Silva");
        cliente.setEmail("alice.silva@banco.com");

        Mockito.when(
                clienteService.update(
                        Mockito.eq(1L),
                        Mockito.any(UpdateClienteRequestDTO.class)
                )
        ).thenReturn(cliente);

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "nome": "Alice Souza Silva",
                        "email": "alice.silva@banco.com",
                        "senha": "novaSenha456"
                    }
                    """)
                .when()
                .put("/clientes/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("nome", equalTo("Alice Souza Silva"))
                .body("email", equalTo("alice.silva@banco.com"));
    }

    @Test
    void deveNegarAcessoSemAutenticacao() {

        given()
                .when()
                .get("/clientes")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "cliente", roles = {"CLIENTE"})
    void deveNegarAcessoParaRoleInvalida() {

        given()
                .when()
                .get("/clientes")
                .then()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "gerente", roles = {"GERENTE"})
    void deveRetornar404QuandoClienteNaoExiste() {

        Mockito.when(clienteService.getRequiredCliente(99L))
                .thenThrow(new NotFoundException());

        given()
                .when()
                .get("/clientes/99")
                .then()
                .statusCode(404);
    }
}