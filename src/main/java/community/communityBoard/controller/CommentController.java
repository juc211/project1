package community.communityBoard.controller;

import community.communityBoard.dto.CommentRequestDto;
import community.communityBoard.dto.CommentResponseDto;
import community.communityBoard.service.CommentService;
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
            @PathVariable("boardId") Long boardId, @Valid @RequestBody CommentRequestDto requestDto){
        Long commentId = commentService.createComment(boardId, requestDto);

        //성공시 201 Created 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(commentId + "번 댓글이 등록되었습니다.");
    }

    /**
     * 댓글 목록 조회
     */
    @GetMapping("api/boards/{boardId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable("boardId") Long boardId){
        List<CommentResponseDto> comments = commentService.getCommentsByBoardId(boardId);

        // 200 OK 반환
        return ResponseEntity.ok(comments);
    }

    /**
     * 댓글 수정
     */
    @PatchMapping("/api/comments/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable("commentId") Long commentId, @Valid @RequestBody CommentRequestDto requestDto){
        commentService.updateComment(commentId, requestDto);

        // 200 OK 반환
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId){
            commentService.deleteComment(commentId);
            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
    }

}
