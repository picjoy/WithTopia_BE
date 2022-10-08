package com.four.withtopia.api.service;

import com.four.withtopia.config.error.ErrorCode;
import com.four.withtopia.config.expection.PrivateException;
import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.domain.Vote;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.db.repository.VoteRepository;
import com.four.withtopia.dto.request.VoteRequestDto;
import com.four.withtopia.dto.response.VoteResponseDto;
import com.four.withtopia.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final MemberCheckUtils memberCheckUtils;
    private final MemberRepository memberRepository;

    public VoteResponseDto vote(VoteRequestDto requestDto, HttpServletRequest request){
        System.out.println("투표 결과 = " + requestDto.isVote());

        //토큰 검증 및 투표하는 멤버 객체 가져오기
        Member voteByMember = memberCheckUtils.checkMember(request);
        Member voteToMember = memberRepository.findByNickName(requestDto.getNickname()).orElseThrow(
                () -> new PrivateException(new ErrorCode(HttpStatus.NOT_FOUND, "404", "조회된 멤버가 없습니다."))
        );

        // 자신에게 투표하지 못하게 막기
        if(voteByMember.getNickName().equals(requestDto.getNickname())){
            throw new PrivateException(new ErrorCode(HttpStatus.OK,"200","자신에게 투표할 수 없습니다."));
        }

        // 이 멤버가 투표를 했는가? 만일 했다면 에러코드 발생
        Vote checkVote = voteRepository.findByVoteByIdAndVoteToId(voteByMember.getMemberId(), voteToMember.getMemberId());
        if(checkVote != null){
            throw new PrivateException(new ErrorCode(HttpStatus.OK,"200","이미 투표를 완료했습니다."));
        }

        // 이 멤버가 투표를 안 했다면?
        // 투표를 생성하고, 좋아요를 올린다.
        voteRepository.save(Vote.builder()
                .voteBy(voteByMember.getNickName())
                .voteById(voteByMember.getMemberId())
                .voteTo(requestDto.getNickname())
                .voteToId(voteToMember.getMemberId())
                .build());

        // 싫어요를 받으면 lick count 내리기
        if(!requestDto.isVote()){
            if(voteToMember.getLikeCount() == 0){
                // 인기도가 이미 0이라면
                throw new PrivateException(new ErrorCode(HttpStatus.OK,"200","더이상 내려갈 인기도가 없습니다."));
            }
            Long likeCnt = voteToMember.getLikeCount()- 1;
            System.out.println("voteByMember = " + voteByMember.getLikeCount());
            return getVoteResponseDto(voteToMember, likeCnt);
        }

        // 좋아요를 받는다면 like count 올라가기
        Long likeCnt = voteToMember.getLikeCount() + 1;
        System.out.println("voteToMember = " + voteToMember.getLikeCount());
        return  getVoteResponseDto(voteToMember, likeCnt);
    }

    private VoteResponseDto getVoteResponseDto(Member voteToMember, Long likeCnt) {

        voteToMember.updatePopularity(likeCnt);
        memberRepository.save(voteToMember);

        return VoteResponseDto.createVoteResponse(voteToMember);
    }

}
