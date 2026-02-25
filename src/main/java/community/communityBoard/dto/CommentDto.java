package community.communityBoard.dto;

import community.communityBoard.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentDto {

    @Getter
    @NoArgsConstructor
//댓글을 작성하거나 수정할 때 사용
    public static class Request {

        @NotBlank(message = "댓글을 입력해주세요.")
        @Size(max = 500, message = "댓글은 500자 이내로 작성해주세요.")
        private String content;
    }

    @Getter
    @NoArgsConstructor
//조회된 댓글 정보를 클라이언트에게 보여줄 때 사용
    public static class Response {

        private Long id;
        private String content;
        private String writer;

        public Response(Comment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
            this.writer = comment.getMember().getNickname();
        }
    }
}
