package community.communityBoard.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    public Comment(String content, String writer, Board board){
        this.content = content;
        this.writer = writer;
        this.board = board;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateWriter(String writer) {
        this.writer = writer;
    }
}
