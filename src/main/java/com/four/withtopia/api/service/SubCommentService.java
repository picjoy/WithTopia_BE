package com.four.withtopia.api.service;


import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.Comment;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.SubComment;
import com.four.withtopia.db.repository.CommentRepository;
import com.four.withtopia.db.repository.SubCommentRepository;
import com.four.withtopia.dto.request.SubCommentRequestDto;
import com.four.withtopia.dto.response.SubCommentResponseDto;
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SubCommentService {

    private final SubCommentRepository subCommentRepository;

    private final CommentRepository commentRepository;

    private final MemberCheckUtils memberCheckUtils;

    // 대댓글 생성
    public SubCommentResponseDto createSubComment(SubCommentRequestDto subCommentRequestDto, HttpServletRequest request) {

        // 토큰 검증 및 멤버 객체 가져오기
        Member member = memberCheckUtils.checkMember(request);

        // 댓글 가져오기
        Comment comment = commentRepository.findById(subCommentRequestDto.getCommentId()).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","해당 댓글이 없습니다.")));

        // 대댓글 빌드
        SubComment subComment = SubComment.builder()
                .comment(comment)
                .nickname(member.getNickName())
                .profileImage(member.getProfileImage())
                .content(subCommentRequestDto.getContent())
                .build();

        // 대댓글 저장
        subCommentRepository.save(subComment);

        return buildSubCommentResponseDto(subComment);


    }

    // 대댓글 조회
    public List<SubCommentResponseDto> getAllSubComment(Long commentId) {

        // 댓글 가져오기
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","해당 댓글이 없습니다.")));


        List<SubComment> subComments = subCommentRepository.findAllByCommentId(comment.getId());

        List<SubCommentResponseDto> subCommentResponseDtos = new ArrayList<>();

        for (SubComment subComment : subComments){
            subCommentResponseDtos.add(
                    SubCommentResponseDto.builder()
                            .id(subComment.getId())
                            .commentId(subComment.getComment().getId())
                            .nickname(subComment.getNickname())
                            .profileImage(subComment.getProfileImage())
                            .content(subComment.getContent())
                            .build()
            );
        }

        return subCommentResponseDtos;

    }

    // 대댓글 수정
    public String updateSubComment(Long subcommentId, SubCommentRequestDto subCommentRequestDto, HttpServletRequest request) {

        // 토큰 검증 및 멤버 객체 가져오기
        Member member = memberCheckUtils.checkMember(request);

        // 대댓글 가져오기
        SubComment subComment = subCommentRepository.findById(subcommentId).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","해당 대댓글이 없습니다.")));

        // 작성자 확인
        if (!subComment.getNickname().equals(member.getNickName())){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","작성자에게만 권한이 있습니다."));
        }

        // 대댓글 수정
        subComment.update(subCommentRequestDto);

        return "Success";
    }

    // 대댓글 삭제
    public String deleteSubComment(Long subcommentId, HttpServletRequest request) {

        // 토큰 검증 및 멤버 객체 가져오기
        Member member = memberCheckUtils.checkMember(request);

        // 대댓글 가져오기
        SubComment subComment = subCommentRepository.findById(subcommentId).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","해당 대댓글이 없습니다.")));

        // 작성자 확인
        if (!subComment.getNickname().equals(member.getNickName())){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","작성자에게만 권한이 있습니다."));
        }

        subCommentRepository.delete(subComment);

        return "Success";
    }

    private SubCommentResponseDto buildSubCommentResponseDto(SubComment subComment) {
        return SubCommentResponseDto.builder()
                .id(subComment.getId())
                .commentId(subComment.getComment().getId())
                .nickname(subComment.getNickname())
                .profileImage(subComment.getProfileImage())
                .content(subComment.getContent())
                .build();
    }
}
