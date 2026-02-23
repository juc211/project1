package community.communityBoard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
//댓글을 작성하거나 수정할 때 사용
public class CommentRequestDto {

    @NotBlank(message = "댓글을 입력해주세요.")
    @Size(max = 500, message = "댓글은 500자 이내로 작성해주세요.")
    private String content;
    
}
