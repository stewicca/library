package com.library.api.service.impl;

import com.library.api.dto.request.CreateMemberRequest;
import com.library.api.dto.response.MemberResponse;
import com.library.api.entity.Member;
import com.library.api.exception.BusinessRuleException;
import com.library.api.exception.ResourceNotFoundException;
import com.library.api.repository.MemberRepository;
import com.library.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Default {@link MemberService}.
 *
 * @author stewicca
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MemberResponse register(CreateMemberRequest request) {
        if (memberRepository.existsByMemberNumber(request.memberNumber())) {
            throw new BusinessRuleException("Member number already registered: " + request.memberNumber());
        }
        Member member = Member.builder()
                .memberNumber(request.memberNumber())
                .name(request.name())
                .email(request.email())
                .build();
        return MemberResponse.from(memberRepository.save(member));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MemberResponse> listAll() {
        return memberRepository.findAll().stream().map(MemberResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MemberResponse getById(String id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
        return MemberResponse.from(member);
    }
}
