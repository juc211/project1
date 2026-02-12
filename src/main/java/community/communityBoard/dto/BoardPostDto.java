package community.communityBoard.dto;

import community.communityBoard.entity.Board;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class BoardPostDto {

    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;

    @NotEmpty(message = "내용을 입력해주세요.")
    private String content;

    @NotEmpty(message = "작성자 이름을 입력해주세요.")
    private String writer;


}
