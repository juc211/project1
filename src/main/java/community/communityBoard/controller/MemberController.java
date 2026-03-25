package community.communityBoard.controller;

import community.communityBoard.dto.MemberDto;
import community.communityBoard.entity.Member;
import community.communityBoard.security.CustomUserDetails;
import community.communityBoard.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 가입
     * SecurityConfig에서 permitAll(); 로 설정되어있기 때문에 토큰없이 접근
     */
    @PostMapping("/signup")
    public ResponseEntity<String> join(@Valid @RequestBody MemberDto.JoinRequest dto) {
        memberService.join(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<MemberDto.LoginResponse> login(@RequestBody MemberDto.LoginRequest dto){
        MemberDto.LoginResponse response = memberService.login(dto);

        return ResponseEntity.ok(response);
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {

        return ResponseEntity.ok("로그아웃 성공 - 클라이언트에서 토큰 삭제");
    }

    /**
     * 회원 전체 조회
     */
    @GetMapping
    public ResponseEntity<List<MemberDto.Response>> getMembers() {
        List<MemberDto.Response> members = memberService.findMembers().stream()
                .map(MemberDto.Response::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(members);
    }

    /**
     * 회원 개별 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberDto.Response> getMember(@PathVariable("id") Long id) {
        Member member = memberService.findOne(id);
        return ResponseEntity.ok(new MemberDto.Response(member));
    }

    /**
     * 회원 정보 수정 (로그인 한 본인)
     */
    @PatchMapping("/me")
    public ResponseEntity<String> updateMember(@Valid @RequestBody MemberDto.UpdateRequest dto,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {

        memberService.update(userDetails.getMemberId(), dto);
        return ResponseEntity.ok("회원 정보 수정 성공");
    }
}
