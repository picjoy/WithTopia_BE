package com.four.withtopia.api.service;

import com.four.withtopia.db.domain.Rank;
import com.four.withtopia.db.repository.RankRepository;
import com.four.withtopia.dto.request.VoteRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RankService {

    private final RankRepository rankRepository;

    @Transactional
    public Rank rankSave(VoteRequestDto requestDto){
        // 좋아요 받은 사람 등록 전에 좋아요를 받은 내역이 있는지 체크
        Rank voteCheck = rankRepository.findByNickname(requestDto.getNickname());
        if(voteCheck == null){
            if(!requestDto.isVote()){
                return null;
            }
            else{
                // 좋아요를 받은 내역이 없는 사람이 좋아요를 받는다면 랭크에 좋아요 받은 사람을 등록하고 like count 올라가기
                Rank rankSave = Rank.builder()
                        .nickname(requestDto.getNickname())
                        .likeCnt(1L)
                        .build();

                rankRepository.save(rankSave);
                return rankSave;
            }
        }
        //좋아요를 받은 내역이 있는 사람이 싫어요를 받는다면 like count 내려가기
        if(!requestDto.isVote()){
            if(voteCheck.getLikeCnt() == 0){
                return null;
            }
            Long likeCnt = voteCheck.getLikeCnt() - 1;
            voteCheck.updateLikeCnt(likeCnt);
            rankRepository.save(voteCheck);
            return voteCheck;
        }

        // 좋아요를 받은 내역이 있는 사람이 좋아요를 받는다면 like count 올라가기
        Long likeCnt = voteCheck.getLikeCnt() + 1;
        voteCheck.updateLikeCnt(likeCnt);
        rankRepository.save(voteCheck);

        return voteCheck;
    }
}
