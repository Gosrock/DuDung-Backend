package band.gosrock.domain.domains.cart.domain;


import band.gosrock.domain.common.model.BaseTimeEntity;
import band.gosrock.domain.common.vo.Money;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "tbl_cart_line_item")
public class CartLineItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_line_id")
    private Long id;

    // 상품 아이디
    private Long itemId;
    // 상품 수량
    private Long quantity;
    // 장바구니 담은 유저아이디
    private Long userId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_line_id")
    private List<CartOptionAnswer> cartOptionAnswers = new ArrayList<>();

    @Builder
    public CartLineItem(
            Long itemId, Long quantity, Long userId, List<CartOptionAnswer> cartOptionAnswers) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.userId = userId;
        this.cartOptionAnswers.addAll(cartOptionAnswers);
    }

    public Money getTotalOptionsPrice() {
        return cartOptionAnswers.stream()
                .map(CartOptionAnswer::getOptionPrice)
                .reduce(Money.ZERO, Money::plus);
    }
}
