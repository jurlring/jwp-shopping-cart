package woowacourse.shoppingcart.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.shoppingcart.dto.CartRequest;
import woowacourse.shoppingcart.dto.CustomerRequest;
import woowacourse.shoppingcart.dto.ProductResponse;

@SpringBootTest
@Sql(scripts = {"classpath:schema.sql", "classpath:product-data.sql"})
@Transactional
class CartServiceTest {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CartService cartService;

    @DisplayName("장바구니에 담긴 상품의 수량을 증가시킨다.")
    @Test
    void addCartItemQuantity() {
        saveCustomer();
        CartRequest savingCartItem = new CartRequest(1L, 5);
        cartService.addCart(1L, savingCartItem);

        CartRequest updatingCartItem = new CartRequest(1L, 10);
        cartService.updateCartQuantity(1L, updatingCartItem);

        List<ProductResponse> cartProductsByCustomerId = cartService.findCartProductByCustomerId(1L);

        assertThat(cartProductsByCustomerId.size()).isEqualTo(1);
        cartProductsByCustomerId.forEach(product -> assertThat(product.getSavedQuantity()).isEqualTo(10));
    }


    void saveCustomer() {
        CustomerRequest customer =
                new CustomerRequest("email", "Pw123456!", "name", "010-2222-3333", "address");
        customerService.save(customer);
    }
}