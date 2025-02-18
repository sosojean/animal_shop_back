package animal_shop.shop.cart.dto;

import animal_shop.shop.cart_item.entity.CartItem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CartDetailDTO {

    private Long cartItemId;

    private Long itemId;

    private String itemNm;

    private int count;

    private String option_name;

    private Long option_price;

    private Long discount_rate;

    private String imgUrl;

    public CartDetailDTO() {}

    public CartDetailDTO(CartItem cartItem){
        this.cartItemId = cartItem.getId();
        this.itemId = cartItem.getItem().getId();
        this.itemNm = cartItem.getItem().getName();
        this.discount_rate = cartItem.getOption().getDiscount_rate();
        this.count = cartItem.getCount();
        this.option_name = cartItem.getOption().getName();
        this.option_price = cartItem.getOption().getPrice();
        this.imgUrl = cartItem.getItem().getThumbnail_url().get(0);
    }

}
