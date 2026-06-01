package com.library.api.service;

import com.library.api.dto.request.CreateMemberRequest;
import com.library.api.dto.response.MemberResponse;

import java.util.List;

/**
 * Member-management use cases.
 *
 * @author stewicca
 * @version 1.0
 */
public interface MemberService {

    /**
     * Register a new member.
     *
     * @param request the new member's details
     * @return the stored member
     * @throws com.library.api.exception.BusinessRuleException if the member number is taken
     */
    MemberResponse register(CreateMemberRequest request);

    /** @return all registered members. */
    List<MemberResponse> listAll();

    /**
     * @param id member id
     * @return the member
     * @throws com.library.api.exception.ResourceNotFoundException if no such member exists
     */
    MemberResponse getById(String id);
}
