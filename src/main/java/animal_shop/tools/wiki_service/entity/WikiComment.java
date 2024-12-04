package animal_shop.tools.wiki_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WikiComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long memberNickName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wiki_id")
    private Wiki wiki;

    private String content;

    private LocalDateTime createdDate = LocalDateTime.now();


}
