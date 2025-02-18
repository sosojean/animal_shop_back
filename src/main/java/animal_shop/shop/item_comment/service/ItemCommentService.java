package animal_shop.shop.item_comment.service;
import animal_shop.community.member.entity.Member;
import animal_shop.community.member.service.MemberService;
import animal_shop.global.security.TokenProvider;
import animal_shop.shop.item.entity.Item;
import animal_shop.shop.item.repository.ItemRepository;
import animal_shop.shop.item_comment.dto.ItemCommentDTO;
import animal_shop.shop.item_comment.dto.ItemCommentDTOResponse;
import animal_shop.shop.item_comment.dto.RequsetItemCommentDTO;
import animal_shop.shop.item_comment.entity.ItemComment;
import animal_shop.shop.item_comment.repository.ItemCommentRepository;
import animal_shop.shop.item_comment_like.entity.ItemCommentLike;
import animal_shop.shop.item_comment_like.repository.ItemCommentLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ItemCommentService {

    @Autowired
    ItemCommentRepository itemCommentRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    MemberService memberService;

    @Autowired
    ItemCommentLikeRepository itemCommentLikeRepository;


    @Transactional
    public ItemCommentDTOResponse getCommentsByItemId(Long itemId, String token, int page,String about) {

        List<ItemCommentDTO> commentDTOS = new ArrayList<>();
        Pageable pageable = (Pageable) PageRequest.of(page,20, Sort.by(Sort.Direction.DESC, "createdDate"));

        //해당 상품의 댓글들 조회
        Page<ItemComment> comments;

        //해당 상품의 댓글들 조회
        if(Objects.equals(about, "heart")){
            comments = itemCommentRepository.findByMostHeartsNative(itemId, pageable);
        }else if(Objects.equals(about,"rating")){
            comments = itemCommentRepository.findByHighestRatingNative(itemId, pageable);
        }else if(Objects.equals(about,"picture")){
            comments = itemCommentRepository.findByMostPhotosAndRatingNative(itemId, pageable);
        }else{
            comments = itemCommentRepository.findByItemId(itemId, pageable);
        }


        //댓글 좋아요 기능
        if(token!=null){
            String userId = tokenProvider.extractIdByAccessToken(token);
            for(ItemComment comment : comments){
                    ItemCommentDTO commentDTO = new ItemCommentDTO(comment);
                    ItemCommentLike commentLike = itemCommentLikeRepository.findByItemCommentIdAndMemberId(comment.getId(), Long.valueOf(userId));
                    commentDTO.setHeart(commentLike!=null);
                    commentDTOS.add(commentDTO);
            }
        }else{
            commentDTOS = comments.stream()
                    .map(ItemCommentDTO::new)  // Comment 객체를 CommentDTO로 변환
                    .toList();
        }
        return ItemCommentDTOResponse
                .builder()
                .comments(commentDTOS)
                .total_count(comments.getTotalElements())
                .build();

    }
    @Transactional
    public void createComment(String token, Long itemId, RequsetItemCommentDTO requestItemCommentDTO) {
        String userId = tokenProvider.extractIdByAccessToken(token);

        Member member = memberService.getByUserId(Long.valueOf(userId));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("item not found"));

        //댓글 등록시 item의 별점과 댓글 수 증가
        item.setTotal_rating(item.getTotal_rating() + requestItemCommentDTO.getRating());
        item.setComment_count(item.getComment_count()+1);

        //만약 별점이 0이거나 6이상이면 에러
        if(requestItemCommentDTO.getRating() < 1 || requestItemCommentDTO.getRating() > 5){
            throw new IllegalArgumentException("rating error");
        }

        ItemComment comment = ItemComment.builder()
                .contents(requestItemCommentDTO.getContents())
                .item(item)
                .rating(requestItemCommentDTO.getRating())
                .countHeart(0L)
                .comment_thumbnail_url(requestItemCommentDTO.getThumbnailUrls())
                .member(member)
                .build();

        itemRepository.save(item);

        itemCommentRepository.save(comment);
    }
    @Transactional
    public ItemCommentDTO updateComment(String token, Long commentId, RequsetItemCommentDTO requestItemCommentDTO) {
        String userId = tokenProvider.extractIdByAccessToken(token);

        ItemComment comment = itemCommentRepository.findById(commentId).orElseThrow(()-> new IllegalArgumentException("comment not found"));
        Item item = comment.getItem();
        //총 평점 수정
        if(requestItemCommentDTO.getRating() != null){
            item.setTotal_rating(item.getTotal_rating() - comment.getRating() + requestItemCommentDTO.getRating());
            comment.setRating(requestItemCommentDTO.getRating());
        }
        if(requestItemCommentDTO.getContents() != null){
            comment.setContents(requestItemCommentDTO.getContents());
        }
        if(requestItemCommentDTO.getThumbnailUrls() != null){
            comment.setComment_thumbnail_url(requestItemCommentDTO.getThumbnailUrls());
        }

        itemCommentRepository.save(comment);

        return new ItemCommentDTO(comment);
    }
    @Transactional
    public void deleteComment(String token, Long commentId) {
        Long userId = Long.valueOf(tokenProvider.extractIdByAccessToken(token));
        ItemComment comment = itemCommentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("comment not found"));

        Item item = comment.getItem();
        item.setComment_count(item.getComment_count()-1);
        //item의 평점 삭제
        item.setTotal_rating(item.getTotal_rating() - comment.getRating());
        itemRepository.save(item);

        if(userId.equals(comment.getMember().getId())){
            itemCommentRepository.delete(comment);
        }else{
            throw new IllegalArgumentException("comment is not present");
        }
    }
    @Transactional
    public boolean checkCommentWriter(String token, Long commentId) {
        String userId = tokenProvider.extractIdByAccessToken(token);
        ItemComment comment = itemCommentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("comment not found : " + commentId));
        if(String.valueOf(comment.getMember().getId()).equals(userId)){
            return true;
        }else{
            return false;
        }
    }

}
