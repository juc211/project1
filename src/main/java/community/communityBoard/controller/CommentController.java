package community.communityBoard.controller;

import community.communityBoard.dto.CommentDto;
import community.communityBoard.entity.Member;
import community.communityBoard.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    public final CommentService commentService;

    /**
     * 댓글 등록
     */
    @PostMapping("api/boards/{boardId}/comments")
    public ResponseEntity<String> createComment(
            @PathVariable("boardId") Long boardId, @Valid @RequestBody CommentDto.Request requestDto,
            HttpServletRequest request){

        // 1. 세션에서 로그인 정보 가져오기
        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute("loginMember");

        // 2. 로그인 체크
        if (loginMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 3. 로그인한 사용자 id 전달
        Long commentId = commentService.createComment(boardId, requestDto, loginMember.getId());
        //성공시 201 Created 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(commentId + "번 댓글이 등록되었습니다.");
    }

    /**
     * 댓글 목록 조회
     */
    @GetMapping("api/boards/{boardId}/comments")
    public ResponseEntity<List<CommentDto.Response>> getComments(@PathVariable("boardId") Long boardId){
        List<CommentDto.Response> comments = commentService.getCommentsByBoardId(boardId);

        // 200 OK 반환
        return ResponseEntity.ok(comments);
    }

    /**
     * 댓글 수정
     */
    @PatchMapping("/api/comments/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentDto.Request requestDto, HttpServletRequest request){

        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute("loginMember");

        commentService.updateComment(commentId, requestDto, loginMember.getId());

        // 200 OK 반환
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId,
                                                HttpServletRequest request){
            HttpSession session = request.getSession(false);
            Member loginMember = (Member) session.getAttribute("loginMember");

            commentService.deleteComment(commentId, loginMember.getId());
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
    }

}
