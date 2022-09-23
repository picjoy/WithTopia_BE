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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final MemberCheckUtils memberCheckUtils;
    private final MemberRepository memberRepository;

    @Transactional
    public VoteResponseDto vote(VoteRequestDto requestDto, HttpServletRequest request){
        //토큰 검증 및 투표하는 멤버 객체 가져오기
        Member voteByMember = memberCheckUtils.checkMember(request);

        // 자신에게 투표하지 못하게 막기
        if(voteByMember.getNickName().equals(requestDto.getNickname())){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","자신에게 투표할 수 없습니다."));
        }

        // 이 멤버가 투표를 했는가? 만일 했다면 에러코드 발생
        Vote checkVote = voteRepository.findByVoteByAndVoteTo(voteByMember.getNickName(), requestDto.getNickname());
        if(checkVote != null){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","이미 투표를 완료했습니다."));
        }

        // 이 멤버가 투표를 안 했다면?
        // 투표를 생성하고, 좋아요를 올린다.
        Vote vote = Vote.builder()
                .voteBy(voteByMember.getNickName())
                .voteTo(requestDto.getNickname())
                .build();

        voteRepository.save(vote);

        // 투표 받는 멤버 찾기
        Optional<Member> voteToMember = memberRepository.findByNickName(requestDto.getNickname());

        // 투표 받는 멤버가 없다면 에러코드 발생
        if(voteToMember == null || voteToMember.isEmpty()){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","조회된 멤버가 없습니다"));
        }

        // 싫어요를 받으면 lick count 내리기
        if(!requestDto.isVote()){
            if(voteToMember.get().getLikeCount() == 0){
                // 인기도가 이미 0이라면
                throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","더이상 내려갈 인기도가 없습니다."));
            }
            Long likeCnt = voteToMember.get().getLikeCount()- 1;
            VoteResponseDto responseDto = getVoteResponseDto(voteToMember, likeCnt);
            return responseDto ;
        }

        // 좋아요를 받는다면 like count 올라가기
        Long likeCnt = voteToMember.get().getLikeCount() + 1;
        VoteResponseDto responseDto = getVoteResponseDto(voteToMember, likeCnt);
        return  responseDto ;
    }

    private VoteResponseDto getVoteResponseDto(Optional<Member> voteToMember, Long likeCnt) {
        if (voteToMember.isEmpty()){
            throw new PrivateException(new ErrorCode(HttpStatus.BAD_REQUEST,"400","멤버가 존재하지 않습니다."));
        }
        Member newMember = voteToMember.get();
        newMember.updatePopularity(likeCnt);
        memberRepository.save(newMember);
        VoteResponseDto responseDto = VoteResponseDto.createVoteResponse(newMember);
        return responseDto;
    }

}
