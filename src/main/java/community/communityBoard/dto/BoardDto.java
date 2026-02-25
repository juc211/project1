package community.communityBoard.dto;

import community.communityBoard.entity.Board;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class BoardDto {

    @Getter
    @NoArgsConstructor
// 클라이언트가 서버로 게시글 정보를 보낼 때 사용
    public static class Request {

        @NotEmpty(message = "제목을 입력해주세요.")
        @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
        private String title;

        @NotEmpty(message = "내용을 입력해주세요.")
        private String content;

    }

    @Getter
    @NoArgsConstructor
// 서버가 클라이언트에게 게시글 정보를 보여줄 때 사용
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String writer;
        private LocalDateTime regDate; // 날짜를 문자열로 포맷팅

        public Response(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            this.writer = board.getMember().getNickname(); // Member 엔티티의 닉네임을 작성자로 사용
            this.regDate = board.getRegDate();
        }
    }
}
