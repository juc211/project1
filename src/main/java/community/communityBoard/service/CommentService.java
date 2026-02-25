package community.communityBoard.service;

import community.communityBoard.dto.CommentDto;
import community.communityBoard.entity.Board;
import community.communityBoard.entity.Comment;
import community.communityBoard.entity.Member;
import community.communityBoard.repository.BoardRepository;
import community.communityBoard.repository.CommentRepository;
import community.communityBoard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    /**
     * 댓글 등록
     */
    @Transactional
    public Long createComment(Long boardId, CommentDto.Request requestDto, Long memberId) {
        // 게시글 존재 여부 확인
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + boardId));

        // 회원 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다. id=" + memberId));

        // 댓글 생성 및 게시글 연결 (빌더 패턴 적용)
        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .member(member)
                .board(board)
                .build();

        return commentRepository.save(comment).getId();
    }

    /**
     * 특정 게시글의 댓글 목록 조회
     */
    public List<CommentDto.Response> getCommentsByBoardId(Long boardId) {
        // 게시글 존재 여부 확인
        if (!boardRepository.existsById(boardId)) {
            throw new IllegalArgumentException("존재하지 않는 게시글의 댓글을 조회할 수 없습니다. id=" + boardId);
        }
        return commentRepository.findAllByBoardId(boardId).stream()
                .map(CommentDto.Response::new)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(Long commentId, CommentDto.Request requestDto, Long currentMemberId) {
        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + commentId));

        // 권한 검증
        if (!comment.getMember().getId().equals(currentMemberId)) {
            throw new IllegalArgumentException("본인의 댓글만 수정할 수 있습니다.");
        }

        // Dirty Checking
        comment.updateContent(requestDto.getContent());
    }

    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId, Long currentMemberId) {
        // 댓글 존재 여부 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + commentId));

        // 권한 검증
        if (!comment.getMember().getId().equals(currentMemberId)) {
            throw new IllegalArgumentException("본인의 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }
}
