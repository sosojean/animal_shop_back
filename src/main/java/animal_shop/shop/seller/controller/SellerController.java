package animal_shop.shop.seller.controller;

import animal_shop.global.dto.ResponseDTO;
import animal_shop.shop.delivery.dto.*;
import animal_shop.shop.delivery.service.DeliveryService;
import animal_shop.shop.item.dto.*;
import animal_shop.shop.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping("/item/new")
    public ResponseEntity<?> registerItem(@RequestHeader(value = "Authorization") String token, @RequestBody ItemDTOList itemDTOList) {
        ResponseDTO responseDTO = null;

        try {
            itemService.save(token, itemDTOList);
            System.out.println("here 5");
            responseDTO = ResponseDTO.builder()
                    .message("save success")
                    .build();
            return ResponseEntity.ok().body(responseDTO);

        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //상품 마이페이지에 뜰려면 보내야 하니까
    @GetMapping("/item/update")
    public ResponseEntity<?> getItems(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody ItemDTOList itemDTOList) {
        ResponseDTO responseDTO = null;
        try {
            itemService.update(token, itemDTOList);
            responseDTO = ResponseDTO.builder()
                    .message("update success")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //상품 업데이트
    @PatchMapping("/item/update")
    public ResponseEntity<?> updateItem(
            @RequestHeader(value = "Authorization") String token,
            @RequestBody ItemDTOList itemDTOList) {
        ResponseDTO responseDTO = null;
        try {
            itemService.update(token, itemDTOList);
            responseDTO = ResponseDTO.builder()
                    .message("update success")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //상품 삭제
    @PatchMapping("/item/discontinue/{itemId}")
    public ResponseEntity<?> deleteItem(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "itemId") String id) {
        ResponseDTO responseDTO = null;
        try {
            itemService.delete(token, id);
            responseDTO = ResponseDTO.builder()
                    .message("discontinue success")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //개별 상품 조회
    @GetMapping("/item/select/{itemId}")
    public ResponseEntity<?> selectItem(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable(value = "itemId") String id) {
        ResponseDTO responseDTO = null;
        try {
            ItemDetailDTO itemDetailDTO = itemService.selectItem(token, id);

            return ResponseEntity.ok().body(itemDetailDTO);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //전체 상품 조회
    @GetMapping("/item/select")
    public ResponseEntity<?> selectAll(@RequestHeader(value = "Authorization") String token,
                                       @RequestParam(value = "page", defaultValue = "1") int page) {
        ResponseDTO responseDTO;
        try {
            ItemDTOListResponse items = itemService.selectAll(token, page - 1);
            return ResponseEntity.ok().body(items);
        } catch (IllegalArgumentException | IllegalStateException e) {
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .error("An unexpected error occurred")
                    .build();
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    //문의 내용 리스트 보기
    @GetMapping("/query/list")
    public ResponseEntity<?> seller_CI(@RequestHeader(value = "Authorization") String token,
                                       @RequestParam(value = "page", defaultValue = "1") int page) {
        ResponseDTO responseDTO = null;
        try {
            QueryResponse orderPage = itemService.find_orders(token, page - 1);

            return ResponseEntity.ok().body(orderPage);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //소비자 문의에 대한 판매자의 답변
    @PatchMapping("/query/comment/{queryId}")
    public ResponseEntity<?> query_comment(@RequestHeader(value = "Authorization") String token,
                                           @PathVariable(value = "queryId") Long queryId,
                                           @RequestBody SellerReplyDTO sellerReplyDTO) {
        ResponseDTO responseDTO = null;
        try {
            itemService.query_comment(token, queryId, sellerReplyDTO);

            responseDTO = ResponseDTO.builder()
                    .message("register Comment")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //소비자 문의에 대한 판매자 답변 삭제
    @PatchMapping("/query/reply/{queryId}")
    ResponseEntity<?> delete_reply(@RequestHeader(value = "Authorization") String token,
                                   @PathVariable(value = "queryId") Long queryId) {
        ResponseDTO responseDTO = null;
        try {
            itemService.delete_reply(token, queryId);

            responseDTO = ResponseDTO.builder()
                    .message("delete reply")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    //판매자 물품 주문 리스트
    @GetMapping("/delivery/list")
    ResponseEntity<?> delivery_list(@RequestHeader(value =  "Authorization")String token,
                                    @RequestParam(name = "page", defaultValue = "1")int page){
        ResponseDTO responseDTO = null;
        try{
            DeliveryDTOResponse deliveryDTOResponse = deliveryService.get_list(token,page - 1);

            return ResponseEntity.ok().body(deliveryDTOResponse);
        }catch (Exception e){
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @GetMapping("/delivery/detail")
    ResponseEntity<?> delivery_detail(@RequestHeader(value =  "Authorization")String token,
                                    @RequestParam(value = "orderItemId") Long orderItemId){
        ResponseDTO responseDTO = null;
        try{
            DeliveryDetailDTO deliveryDetailDTO = deliveryService.get_detail(orderItemId,token);

            return ResponseEntity.ok().body(deliveryDetailDTO);
        }catch (Exception e){
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    @PostMapping("/delivery/approve")
    ResponseEntity<?> delivery_approve(@RequestHeader(value = "Authorization")String token,
                                       @RequestBody DeliveryRequestDTO deliveryRequestDTO) {
        ResponseDTO responseDTO = null;
        try{
            deliveryService.approve(deliveryRequestDTO,token);
            responseDTO = ResponseDTO.builder()
                    .message("delivery approve success")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
    @PostMapping("/delivery/approve_detail")
    ResponseEntity<?> delivery_approve_detail(@RequestHeader(value = "Authorization")String token,
                                       @RequestBody DeliveryApproveDetailDTO deliveryApproveDetailDTO) {
        ResponseDTO responseDTO = null;
        try{
            deliveryService.approve_detail(deliveryApproveDetailDTO,token);
            responseDTO = ResponseDTO.builder()
                    .message("delivery approve success")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/delivery/revoke")
    ResponseEntity<?> delivery_revoke(@RequestHeader(value = "Authorization")String token,
                                       @RequestBody DeliveryRequestDTO deliveryRequestDTO) {
        ResponseDTO responseDTO = null;
        try{
            DeliveryRevokeResponse deliveryRevokeResponse = deliveryService.revoke(deliveryRequestDTO,token);
            return ResponseEntity.ok().body(deliveryRevokeResponse);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/delivery/revoke_detail")
    ResponseEntity<?> delivery_revoke_detail(@RequestHeader(value = "Authorization")String token,
                                             @RequestBody DeliveryRevokeDTO deliveryRevokeDTO) {
        ResponseDTO responseDTO = null;
        try{
            DeliveryRevokeResponse deliveryRevokeResponse = deliveryService.revoke_detail(deliveryRevokeDTO,token);

            return ResponseEntity.ok().body(deliveryRevokeResponse);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }



    @PatchMapping("/discount/ok")
    ResponseEntity<?> item_discount(@RequestHeader(value = "Authorization") String token,
                                    @RequestBody ItemDiscountDTO itemDiscountDTO){
        ResponseDTO responseDTO = null;
        try {
            itemService.discount_rate(token,itemDiscountDTO);
            responseDTO = ResponseDTO.builder()
                    .message("Discount success")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }
    @PatchMapping("/discount/revoke")
    ResponseEntity<?>no_discount(@RequestHeader(value= "Authorization")String token,
                                 @RequestBody ItemDiscountDTO itemDiscountDTO){
        ResponseDTO responseDTO = null;
        try{
            itemService.no_discount(token,itemDiscountDTO);
            responseDTO = ResponseDTO.builder()
                    .message("Discount failure")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e){
            responseDTO = ResponseDTO.builder()
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}
