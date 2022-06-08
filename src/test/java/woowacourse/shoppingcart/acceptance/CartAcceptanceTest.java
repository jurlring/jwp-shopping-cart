package woowacourse.shoppingcart.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static woowacourse.shoppingcart.acceptance.ProductAcceptanceTest.getAccessToken;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import woowacourse.auth.dto.ExceptionResponse;
import woowacourse.shoppingcart.dto.CartRequest;
import woowacourse.shoppingcart.dto.ProductIdsRequest;
import woowacourse.shoppingcart.dto.ProductResponse;

@DisplayName("장바구니 관련 기능")
@Sql(scripts = "classpath:product-data.sql")
public class CartAcceptanceTest extends AcceptanceTest {

    @DisplayName("장바구니 아이템 1개 추가 후, 장바구니 아이템 목록 조회")
    @Test
    void addCartItem() {
        //given
        String accessToken = getAccessToken();
        addCartItemApi(accessToken, 1L, 5);

        //when
        ExtractableResponse<Response> response = findCartItemsApi(accessToken);
        List<ProductResponse> productResponses = response.jsonPath().getList(".", ProductResponse.class);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(productResponses.size()).isEqualTo(1);
        productResponses.forEach(
                productResponse -> assertThat(productResponse).extracting("id", "name", "price", "imageUrl",
                                "savedQuantity")
                        .containsExactly(1L, "콜드 브루 몰트", 4800,
                                "https://image.istarbucks.co.kr/upload/store/skuimg/2021/02/[9200000001636]_20210225093600536.jpg",
                                5));
    }

    @DisplayName("존재하지 않는 회원이 장바구니에 아이템을 담을 경우, 예외를 발생시킨다.")
    @Test
    void addCartItemWhoNonCustomerException() {
        //when
        ExtractableResponse<Response> response = addCartItemApi("", 1L, 5);

        //then
        assertThat(response.as(ExceptionResponse.class).getMessage()).isEqualTo("존재하지 않는 회원입니다.");
    }

    @DisplayName("장바구니에 양수가 아닌 수의 아이템을 담을 경우, 예외를 발생시킨다.")
    @Test
    void addCartItemNotPositiveQuantityException() {
        //given
        String accessToken = getAccessToken();

        //when
        ExtractableResponse<Response> response = addCartItemApi(accessToken, 1L, 0);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.as(ExceptionResponse.class).getMessage()).isEqualTo("올바르지 않은 상품 수량 형식입니다.");
    }

    @DisplayName("장바구니에 담긴 아이템의 수량을 수정한다.")
    @Test
    void updateCartItemQuantity() {
        //given
        String accessToken = getAccessToken();
        addCartItemApi(accessToken, 1L, 5);

        //when
        updateCartItemQuantityApi(accessToken, 1L, 10);

        //then
        ExtractableResponse<Response> response = findCartItemsApi(accessToken);
        List<ProductResponse> productResponses = response.jsonPath().getList(".", ProductResponse.class);

        assertThat(productResponses.size()).isEqualTo(1);
        productResponses.forEach(productResponse -> assertThat(productResponse.getSavedQuantity()).isEqualTo(10));
    }

    @DisplayName("장바구니 상품의 개수를 수정할 때, 음수인 경우 예외를 발생시킨다.")
    @Test
    void updateCartNotPositiveQuantity() {
        //given
        String accessToken = getAccessToken();
        addCartItemApi(accessToken, 1L, 5);

        //when
        ExtractableResponse<Response> response = updateCartItemQuantityApi(accessToken, 1L, 0);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.as(ExceptionResponse.class).getMessage()).isEqualTo("올바르지 않은 상품 수량 형식입니다.");
    }

    @DisplayName("장바구니 아이템 3개 추가 후, 장바구니에서 1개를 삭제하고 장바구니 목록 조회")
    @Test
    void deleteCartItem() {
        //given
        String accessToken = getAccessToken();
        addCartItemApi(accessToken, 1L, 5);
        addCartItemApi(accessToken, 2L, 5);
        addCartItemApi(accessToken, 3L, 5);

        //when
        ExtractableResponse<Response> deleteResponse = deleteCartItemApi(accessToken, List.of(2L));

        ExtractableResponse<Response> response = findCartItemsApi(accessToken);
        List<ProductResponse> productResponses = response.jsonPath().getList(".", ProductResponse.class);

        //then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(productResponses.size()).isEqualTo(2);

    }

    @DisplayName("장바구니에 존재하지 않는 상품을 장바구니에서 삭제할 경우, 예외를 발생시킨다.")
    @Test
    void deleteCartWhenNotSaveCartException() {
        //given
        String accessToken = getAccessToken();
        addCartItemApi(accessToken, 1L, 5);
        addCartItemApi(accessToken, 2L, 5);
        addCartItemApi(accessToken, 3L, 5);

        //when
        ExtractableResponse<Response> deleteResponse = deleteCartItemApi(accessToken, List.of(4L));

        //then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(deleteResponse.as(ExceptionResponse.class).getMessage()).isEqualTo("유효하지 않은 장바구니입니다.");
    }

    public static ExtractableResponse<Response> addCartItemApi(String accessToken, Long productId, int quantity) {
        CartRequest cartRequest = new CartRequest(productId, quantity);

        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(cartRequest)
                .when().post("/customers/carts")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> findCartItemsApi(String accessToken) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/customers/carts")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> deleteCartItemApi(String accessToken, List<Long> productIds) {
        ProductIdsRequest productIdsRequest = new ProductIdsRequest(productIds);
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(productIdsRequest)
                .when().delete("/customers/carts")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> updateCartItemQuantityApi(String accessToken, Long productId,
                                                                          int quantity) {
        CartRequest cartRequest = new CartRequest(productId, quantity);
        return RestAssured
                .given().log().all()
                .auth().oauth2(accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(cartRequest)
                .when().patch("/customers/carts")
                .then().log().all()
                .extract();
    }

}
