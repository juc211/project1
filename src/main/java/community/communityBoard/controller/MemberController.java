package community.communityBoard.controller;

import community.communityBoard.dto.MemberDto;
import community.communityBoard.entity.Member;
import community.communityBoard.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> login(@RequestBody MemberDto.LoginRequest dto, HttpServletRequest request){

        // 1. 서비스 호출로 검증
        Member loginMember = memberService.login(dto.getEmail(), dto.getPassword());

        // 2. 로그인 성공처리 : 세션 생성
        HttpSession session = request.getSession();

        // 3. 세션에 로그인 회원 정보 보관
        session.setAttribute("loginMember", loginMember);

        return ResponseEntity.ok("로그인 성공");
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        // 세션 호출
        HttpSession session = request.getSession(false);

        //세션 무효화-로그아웃 처리
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("로그아웃 성공");
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
    public ResponseEntity<String> updateMember(@Valid @RequestBody MemberDto.UpdateRequest dto, HttpServletRequest request) {

        // 1. 세션 호출
        HttpSession session = request.getSession(false);

        // 2. 세션에 저장된 로그인 정보 가져오기
        Member loginMember = (Member) session.getAttribute("loginMember");

        // 3. 로그인된 세션의 ID로 회원 정보 수정
        memberService.update(loginMember.getId(), dto);
        return ResponseEntity.ok("회원 정보 수정 성공");
    }
}
