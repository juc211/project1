package community.communityBoard.dto;

import community.communityBoard.entity.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
//조회된 댓글 정보를 클라이언트에게 보여줄 때 사용
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
