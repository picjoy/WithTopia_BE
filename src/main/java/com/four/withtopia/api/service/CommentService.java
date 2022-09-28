package com.four.withtopia.api.service;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.Comment;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.Post;
import com.four.withtopia.db.domain.Room;
import com.four.withtopia.db.repository.CommentRepository;
import com.four.withtopia.db.repository.PostRespository;
import com.four.withtopia.dto.request.CommentRequestDto;
import com.four.withtopia.dto.response.CommentResponseDto;
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final MemberCheckUtils memberCheckUtils;

    private final PostRespository postRespository;

    // 댓글 작성
    public CommentResponseDto createComment(CommentRequestDto commentRequestDto, HttpServletRequest request) {

        // 토큰 검증 및 멤버 객체 가져오기
        Member member =  memberCheckUtils.checkMember(request);

        // 게시물 찾기
        Post post = postRespository.findById(commentRequestDto.getPostId()).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","해당 게시글이 없습니다.")));

        // 댓글 빌드
        Comment comment = Comment.builder()
                .post(post)
                .nickname(member.getNickName())
                .profileImage(member.getProfileImage())
                .content(commentRequestDto.getContent())
                .build();

        // 저장
        commentRepository.save(comment);

        return buildCommentResponseDto(comment);

    }


    // 댓글 조회
    public Page<Comment> getAllComment(Long postId, int page) {
        PageRequest pageable = PageRequest.of(page-1,10);

        // 게시물 찾기
        Post post = postRespository.findById(postId).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","해당 게시글이 없습니다.")));

        Page<Comment> getComment = commentRepository.findAllByPostOrderByModifiedAtAsc(post, pageable);

        return getComment;
    }

    // 댓글 수정
    public String updateComment(Long commentId, CommentRequestDto commentRequestDto, HttpServletRequest request) {

        // 토큰 검증 및 멤버 객체 가져오기
        Member member =  memberCheckUtils.checkMember(request);

        // 댓글 찾기
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","해당 댓글이 없습니다.")));

        // 만약 작성자가 일치하지 않다면?
        if (!comment.getNickname().equals(member.getNickName())){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","작성자에게만 권한이 있습니다."));
        }

        // 댓글 수정
        comment.update(commentRequestDto);

        return "Success";

    }

    // 댓글 삭제
    public String deleteComment(Long commentId, HttpServletRequest request) {

        // 토큰 검증 및 멤버 객체 가져오기
        Member member =  memberCheckUtils.checkMember(request);

        // 댓글 찾기
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","해당 댓글이 없습니다.")));

        // 만약 작성자가 일치하지 않다면?
        if (!comment.getNickname().equals(member.getNickName())){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","작성자에게만 권한이 있습니다."));
        }

        // 댓글 삭제
        commentRepository.delete(comment);

        return "Success";
    }

    private CommentResponseDto buildCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .nickname(comment.getNickname())
                .profileImage(comment.getProfileImage())
                .content(comment.getContent())
                .build();
    }



}
