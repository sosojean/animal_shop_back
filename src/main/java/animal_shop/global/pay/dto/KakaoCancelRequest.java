package animal_shop.global.pay.dto;

import lombok.Getter;

@Getter
public class KakaoCancelRequest {
    private String tid;
    private String itemName;
    private String itemQuantity;
    private int cancelAmount;
    private int cancelTaxFreeAmount;
    private int cancelVatAmount;
}
