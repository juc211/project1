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

}
