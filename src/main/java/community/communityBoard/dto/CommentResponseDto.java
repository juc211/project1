package community.communityBoard.dto;

import community.communityBoard.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String content;
    private String writer;

    public CommentResponseDto(Comment comment ){
        this.id = comment.getId();
        this.content = comment.getContent();
        this.writer = comment.getWriter();
    }
}
