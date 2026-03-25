package community.communityBoard.controller;

import community.communityBoard.dto.BoardDto;
import community.communityBoard.entity.Board;
import community.communityBoard.security.CustomUserDetails;
import community.communityBoard.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController // JSON 반환을 위한 @RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시글 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<BoardDto.Response>> getBoardList() {
        return ResponseEntity.ok(boardService.findBoards());
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardDto.Response> boardDetail(@PathVariable("id") Long id) {
        Board board = boardService.findOne(id);
        return ResponseEntity.ok(new BoardDto.Response(board));
    }

    /**
     * 게시글 등록
     */
    @PostMapping
    public ResponseEntity<String> postBoard(@Valid @RequestBody BoardDto.Request dto,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // 게시글 생성 및 등록
        Long boardId = boardService.createBoard(dto, userDetails.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(boardId + "번 게시글이 성공적으로 등록되었습니다.");
    }

    /**
     * 게시글 수정
     */
    @PatchMapping("/{id}") //PostMapping -> patchMapping 변경으로 URL에 동작을 작성하지 않아도 의미 전달
    public ResponseEntity<String> updateBoard(
            @PathVariable("id") Long id, @Valid @RequestBody BoardDto.Request dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 서비스 호출 시 세션에 저장된 유저의 ID를 함께 전달
        boardService.updateBoard(id, dto, userDetails.getMemberId());
        return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable("id") Long id,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {

        boardService.deleteBoard(id, userDetails.getMemberId());
        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
    }
}
