package community.communityBoard.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
// 클라이언트가 서버로 게시글 정보를 보낼 때 사용
public class BoardPostDto {

    @NotEmpty(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    @NotEmpty(message = "내용을 입력해주세요.")
    private String content;

    @NotEmpty(message = "작성자 이름을 입력해주세요.")
    @Size(min = 2, max = 20, message = "작성자 이름은 2-20자 사이여야 합니다.")
    private String writer;


}
