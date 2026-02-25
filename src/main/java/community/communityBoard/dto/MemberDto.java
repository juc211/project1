package community.communityBoard.dto;

import community.communityBoard.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberDto {

    @Getter
    @NoArgsConstructor
    // 회원가입 요청 DTO
    public static class JoinRequest {
        @NotEmpty(message = "이메일을 입력해주세요.")
        @Email(message = "이메일 형식에 맞지 않습니다.")
        private String email;

        @NotEmpty(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 입력해주세요.")
        private String password;

        @NotEmpty(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하로 입력해주세요.")
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    // 로그인 요청 DTO
    public static class LoginRequest {
        @NotEmpty(message = "이메일을 입력해주세요.")
        private String email;

        @NotEmpty(message = "비밀번호를 입력해주세요.")
        private String password;
    }

    @Getter
    // 회원 정보 응답 DTO (공통)
    public static class Response {
        private Long id;
        private String email;
        private String nickname;
        // 비밀번호는 응답에 포함하지 않는 것이 보안상 안전함

        public Response(Member member) {
            this.id = member.getId();
            this.email = member.getEmail();
            this.nickname = member.getNickname();
        }
    }

    @Getter
    @NoArgsConstructor
    // 회원 정보 수정 요청 DTO
    public static class UpdateRequest {
        @NotEmpty(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상, 10자 이하로 입력해주세요.")
        private String nickname;

        @NotEmpty(message = "비밀번호를 입력해주세요.")
        private String password;
    }
}
