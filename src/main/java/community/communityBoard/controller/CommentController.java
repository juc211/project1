package community.communityBoard.controller;

import community.communityBoard.dto.CommentDto;
import community.communityBoard.security.CustomUserDetails;
import community.communityBoard.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    public final CommentService commentService;

    /**
     * 댓글 등록
     * jwt 토큰 필요
     */
    @PostMapping("api/boards/{boardId}/comments")
    public ResponseEntity<String> createComment(
            @PathVariable("boardId") Long boardId, @Valid @RequestBody CommentDto.Request dto,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        Long commentId = commentService.createComment(boardId, dto, userDetails.getMemberId());
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
     * jwt 토큰 필요
     */
    @PatchMapping("/api/comments/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentDto.Request dto,
            @AuthenticationPrincipal CustomUserDetails userDetails){

        commentService.updateComment(commentId, dto, userDetails.getMemberId());
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }

    /**
     * 댓글 삭제
     * jwt 토큰 필요
     */
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId,
                                                @AuthenticationPrincipal CustomUserDetails userDetails){

            commentService.deleteComment(commentId, userDetails.getMemberId());
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
    }

}
