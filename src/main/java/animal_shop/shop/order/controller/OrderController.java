package animal_shop.shop.order.controller;

import animal_shop.global.dto.ResponseDTO;
import animal_shop.shop.delivery.dto.DeliveryRevokeDTO;
import animal_shop.shop.delivery.dto.DeliveryRevokeResponse;
import animal_shop.shop.order.dto.OrderDTOList;
import animal_shop.shop.order.service.OrderService;
import animal_shop.shop.order_item.dto.OrderHistDTOResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shop")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "/order")
    public ResponseEntity<?> order(@RequestHeader(value = "Authorization") String token,
                                   @RequestBody OrderDTOList orderDTOList){
        ResponseDTO responseDTO;

        try{
            orderService.order(orderDTOList,token);
            responseDTO = ResponseDTO.builder()
                    .message("order success")
                    .build();

            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> orderHist(@RequestHeader(value = "Authorization") String token,
                                       @RequestParam(value = "page" , defaultValue = "1")int page){
        ResponseDTO responseDTO;
        try{
            OrderHistDTOResponse  orderHistDTOResponse = orderService.getOrderList(token,page-1);
                return ResponseEntity.ok().body(orderHistDTOResponse);
        }catch(Exception e){
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //주문 전체 취소
    @PatchMapping("/order/cancel")
    public ResponseEntity<?> cancelOrder (@RequestHeader(value = "Authorization") String token,
                                          @RequestBody Long orderId){
        ResponseDTO responseDTO;
        try{
            DeliveryRevokeResponse deliveryRevokeResponse = orderService.cancelOrder(token, orderId);

            return ResponseEntity.ok().body(deliveryRevokeResponse);
        }catch(Exception e){
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    @PatchMapping("/order/cancel_detail")
    public ResponseEntity<?> cancelOrderDetail (@RequestHeader(value = "Authorization") String token,
                                                @RequestBody DeliveryRevokeDTO deliveryRevokeDTO){
        ResponseDTO responseDTO;
        try{
            DeliveryRevokeResponse deliveryRevokeResponse = orderService.cancelOrderDetail(token, deliveryRevokeDTO);

            return ResponseEntity.ok().body(deliveryRevokeResponse);
        }catch(Exception e){
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
