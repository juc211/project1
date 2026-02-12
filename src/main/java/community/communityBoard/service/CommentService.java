package community.communityBoard.service;

import community.communityBoard.dto.CommentRequestDto;
import community.communityBoard.dto.CommentResponseDto;
import community.communityBoard.entity.Board;
import community.communityBoard.entity.Comment;
import community.communityBoard.repository.BoardRepository;
import community.communityBoard.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 클래스 레벨에서의 트랜잭션 설정 (읽기 전용)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    /**
     * 댓글 등록
     */
    @Transactional // 메서드 레벨에서의 트랜잭션 설정 (더 우선순위로 쓰기 권한을 가짐)
    public Long  createComment(Long boardId, CommentRequestDto requestDto) {
        // 게시글 존재 여부 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + boardId));

        // 댓글 생성 및 게시글 연결 (빌터 패턴 적용)
        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .writer(requestDto.getWriter())
                .board(board)
                .build();

        return commentRepository.save(comment).getId();
    }

    /**
     * 특정 게시글의 댓글 목록 조회
     */
    public List<CommentResponseDto> getCommentsByBoardId(Long boardId) {
        return commentRepository.findAllByBoardId(boardId).stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(Long commentId, CommentRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + commentId));

        comment.updateContent(requestDto.getContent());
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId) {

        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + commentId));

        commentRepository.delete(comment);
    }
}