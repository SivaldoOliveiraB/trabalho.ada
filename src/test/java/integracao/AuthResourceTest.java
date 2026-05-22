package integracao;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import trabalho.ada.resource.auth.dto.TokenResponse;
import trabalho.ada.service.AuthService;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class AuthResourceTest {

    @InjectMock
    AuthService authService;

    @Test
    void deveRealizarLoginComSucesso() {

        // arrange
        String email = "alice@banco.com";
        String senha = "senha123";
        String token = "eyJhbGciOiJSUzI1NiJ9.fake-token";

        Mockito.when(authService.login(email, senha))
                .thenReturn(new TokenResponse(token));

        // act + assert
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "email": "alice@banco.com",
                        "senha": "senha123"
                    }
                    """)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", equalTo(token));

        Mockito.verify(authService)
                .login(email, senha);
    }
}