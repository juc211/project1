package community.communityBoard.dto;
import community.communityBoard.entity.Board;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
// 서버가 클라이언트에게 게시글 정보를 보여줄 때 사용
public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime regDate; // 날짜를 문자열로 포맷팅

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.writer = board.getMember().getNickname(); // Member 엔티티의 닉네임을 작성자로 사용
        this.regDate = board.getRegDate();
    }
}
