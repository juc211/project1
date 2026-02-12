package community.communityBoard.dto;
import community.communityBoard.entity.Board;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private String writer;
    private String regDate; // 날짜를 문자열로 예쁘게 포맷팅해서 보낼 수 있음

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.writer = board.getWriter();
        this.regDate = board.getRegDate().toString();
    }
}
