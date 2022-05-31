package woowacourse.shoppingcart.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import woowacourse.auth.dto.TokenRequest;
import woowacourse.auth.dto.TokenResponse;
import woowacourse.shoppingcart.domain.Customer;
import woowacourse.shoppingcart.dto.CustomerRequest;
import woowacourse.shoppingcart.dto.CustomerResponse;

@DisplayName("회원 관련 기능")
public class CustomerAcceptanceTest extends AcceptanceTest {

    @DisplayName("회원가입")
    @Test
    void addCustomer() {
        // given
        Customer customer = new Customer("email", "Pw123456!", "name", "010-1234-5678", "address");

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(customer)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/customers")
                .then().log().all()
                .extract();
        CustomerResponse customerResponse = response.jsonPath().getObject(".", CustomerResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(customerResponse).extracting("email", "name", "phone", "address")
                .containsExactly("email", "name", "010-1234-5678", "address");
    }

    @DisplayName("내 정보 조회")
    @Test
    void getMe() {
    }

    @DisplayName("내 정보 수정")
    @Test
    void updateMe() {
        // given
        Customer customer = new Customer("email", "Pw123456!", "name", "010-1234-5678", "address");
        RestAssured.given().log().all()
                .body(customer)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/customers")
                .then().log().all()
                .extract();

        String accessToken = RestAssured.given().log().all()
                .body(new TokenRequest("email", "Pw123456!"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/customers/login")
                .then().log().all()
                .extract().as(TokenResponse.class).getAccessToken();
        //when
        RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .body(new CustomerRequest("email", "Pw123456!", "judy", "010-1111-2222", "address2"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/customers")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        //then
        CustomerResponse customerResponse = RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/customers")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().as(CustomerResponse.class);

        assertThat(customerResponse).extracting("email", "name", "phone", "address")
                .containsExactly("email", "judy", "010-1111-2222", "address2");
    }

    @DisplayName("회원탈퇴")
    @Test
    void deleteMe() {
        // given
        Customer customer = new Customer("email", "Pw123456!", "name", "010-1234-5678", "address");
        RestAssured.given().log().all()
                .body(customer)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/customers")
                .then().log().all()
                .extract();

        String accessToken = RestAssured.given().log().all()
                .body(new TokenRequest("email", "Pw123456!"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/customers/login")
                .then().log().all()
                .extract().as(TokenResponse.class).getAccessToken();
        //when
        RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .when()
                .delete("/customers")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
        //then
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/customers")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        //TODO : 예외 메시지 추가 확인
    }
}
