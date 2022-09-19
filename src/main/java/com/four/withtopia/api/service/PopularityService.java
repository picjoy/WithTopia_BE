package com.four.withtopia.api.service;

import com.four.withtopia.db.domain.Member;
import com.four.withtopia.db.repository.MemberRepository;
import com.four.withtopia.dto.response.MemberResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularityService {

    private final MemberRepository memberRepository;
    // 인기 상위 멤버 3명 조회
    public List<MemberResponseDto> topMember() {
        List<Member> topMember = memberRepository.findTop3ByLikeCountGreaterThanOrderByLikeCountDescCreatedAtAsc(3L);
        List<MemberResponseDto> topThreeDto = new ArrayList<>();
        for (Member member : topMember){
            topThreeDto.add(MemberResponseDto.memberResponseDto(member));
        }
        return topThreeDto;
    }

    // 전체 멤버 랭킹 조회
    public Page<Member> totalMemberRank(int page) {

        PageRequest pageable = PageRequest.of(page-1,6);

        Page<Member> totalMember = memberRepository.findAllByOrderByLikeCountDescCreatedAtAsc(pageable);

        return totalMember;

    }
}
