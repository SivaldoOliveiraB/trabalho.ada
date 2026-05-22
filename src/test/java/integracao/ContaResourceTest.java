package integracao;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import trabalho.ada.enums.TipoConta;
import trabalho.ada.enums.TipoTransacao;
import trabalho.ada.model.Cliente;
import trabalho.ada.model.Conta;


import trabalho.ada.model.Transacao;
import trabalho.ada.resource.conta.dto.CreateContaRequest;
import trabalho.ada.service.ContaService;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class ContaResourceTest {

    @InjectMock
    ContaService contaService;

    @Test
    @TestSecurity(user = "gerente", roles = {"GERENTE"})
    void deveCriarConta() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Alice Souza");
        cliente.setEmail("alice@banco.com");

        Conta conta = new Conta();
        conta.setId(10L);
        conta.setNumero("0001-0");
        conta.setTipo(TipoConta.CORRENTE);
        conta.setSaldo(BigDecimal.ZERO);
        conta.setCliente(cliente);

        Mockito.when(contaService.create(Mockito.any(CreateContaRequest.class)))
                .thenReturn(conta);

        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "tipo": "CORRENTE",
                        "cliente": {
                            "id": 1
                        }
                    }
                    """)
                .when()
                .post("/contas")
                .then()
                .statusCode(201)
                .body("id", equalTo(10))
                .body("numero", equalTo("0001-0"))
                .body("tipo", equalTo("CORRENTE"))
                .body("titular.nome", equalTo("Alice Souza"));
    }

    @Test
    @TestSecurity(user = "cliente", roles = {"CLIENTE"})
    void deveBuscarContaPorId() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Alice Souza");
        cliente.setEmail("alice@banco.com");

        Conta conta = new Conta();
        conta.setId(10L);
        conta.setNumero("0001-0");
        conta.setTipo(TipoConta.CORRENTE);
        conta.setSaldo(new BigDecimal("1500.00"));
        conta.setCliente(cliente);

        Mockito.when(contaService.getConta(10L))
                .thenReturn(conta);

        given()
                .when()
                .get("/contas/10")
                .then()
                .statusCode(200)
                .body("id", equalTo(10))
                .body("numero", equalTo("0001-0"))
                .body("tipo", equalTo("CORRENTE"));
    }

    @Test
    @TestSecurity(user = "cliente", roles = {"CLIENTE"})
    void deveDepositar() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Alice Souza");

        Conta conta = new Conta();
        conta.setId(10L);
        conta.setNumero("0001-0");
        conta.setSaldo(new BigDecimal("2000.00"));
        conta.setCliente(cliente);

        Transacao transacao = new Transacao(
                TipoTransacao.DEPOSITO,
                new BigDecimal("500.00")
        );

        transacao.setContaDestino(conta);

        Mockito.when(
                contaService.deposito(
                        new BigDecimal("500.00"),
                        10L
                )
        ).thenReturn(transacao);

        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "valor": 500.00
                }
                """)
                .when()
                .post("/contas/10/deposito")
                .then()
                .statusCode(201)
                .body("tipo", equalTo("DEPOSITO"))
                .body("valor", equalTo(500.00F));
    }

    @Test
    @TestSecurity(user = "cliente", roles = {"CLIENTE"})
    void deveSacar() {

        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Alice Souza");

        Conta conta = new Conta();
        conta.setId(10L);
        conta.setNumero("0001-0");
        conta.setCliente(cliente);

        Transacao transacao = new Transacao(
                TipoTransacao.SAQUE,
                new BigDecimal("200.00")
        );

        conta.setSaldo(new BigDecimal("200.00"));

        transacao.setContaOrigem(conta);

        Mockito.when(
                contaService.saque(
                        new BigDecimal("200.00"),
                        10L
                )
        ).thenReturn(transacao);

        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "valor": 200.00
                }
                """)
                .when()
                .post("/contas/10/saque")
                .then()
                .statusCode(201)
                .body("tipo", equalTo("SAQUE"))
                .body("valor", equalTo(200.00F));
    }

    @Test
    @TestSecurity(user = "cliente", roles = {"CLIENTE"})
    void deveTransferir() {

        Cliente clienteOrigem = new Cliente();
        clienteOrigem.setId(1L);
        clienteOrigem.setNome("Alice Souza");

        Cliente clienteDestino = new Cliente();
        clienteDestino.setId(2L);
        clienteDestino.setNome("Bob Lima");

        Conta contaOrigem = new Conta();
        contaOrigem.setId(10L);
        contaOrigem.setNumero("0001-0");
        contaOrigem.setTipo(TipoConta.CORRENTE);
        contaOrigem.setCliente(clienteOrigem);

        Conta contaDestino = new Conta();
        contaDestino.setId(11L);
        contaDestino.setNumero("0002-1");
        contaDestino.setTipo(TipoConta.ELETRONICA);
        contaDestino.setCliente(clienteDestino);

        Transacao transacao = new Transacao(
                TipoTransacao.TRANSFERENCIA,
                new BigDecimal("300.00")
        );

        transacao.setContaOrigem(contaOrigem);
        transacao.setContaDestino(contaDestino);

        Mockito.when(
                contaService.transferencia(
                        new BigDecimal("300.00"),
                        10L,
                        11L
                )
        ).thenReturn(transacao);

        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                    "contaDestino": {
                        "id": 11
                    },
                    "valor": 300.00
                }
                """)
                .when()
                .post("/contas/10/transferencia")
                .then()
                .statusCode(201)
                .body("tipo", equalTo("TRANSFERENCIA"))
                .body("valor", equalTo(300.00F));
    }

    @Test
    void deveRetornar401SemAutenticacao() {

        given()
                .when()
                .get("/contas/10")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "usuario", roles = {"USUARIO"})
    void deveRetornar403ParaRoleInvalida() {

        given()
                .when()
                .get("/contas/10")
                .then()
                .statusCode(403);
    }
}