package com.library.api.dto.response;

import com.library.api.entity.Member;
import lombok.Builder;

/**
 * Client-facing view of a library member.
 *
 * @author stewicca
 * @version 1.0
 */
@Builder
public record MemberResponse(
        String id,
        String memberNumber,
        String name,
        String email
) {
    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .memberNumber(member.getMemberNumber())
                .name(member.getName())
                .email(member.getEmail())
                .build();
    }
}
