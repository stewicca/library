package com.library.api.controller;

import com.library.api.constant.ApiRoute;
import com.library.api.dto.request.CreateMemberRequest;
import com.library.api.dto.response.MemberResponse;
import com.library.api.dto.response.WebResponse;
import com.library.api.service.MemberService;
import com.library.api.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Member-management endpoints. All operations are staff-only.
 *
 * @author stewicca
 * @version 1.0
 */
@RestController
@RequestMapping(ApiRoute.MEMBERS)
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
@Tag(name = "Members", description = "Register and look up library members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "List all registered members.")
    @GetMapping
    public ResponseEntity<WebResponse<List<MemberResponse>>> list() {
        return ResponseUtil.ok("Members retrieved", memberService.listAll());
    }

    @Operation(summary = "Look up a single member by id.")
    @GetMapping("/{id}")
    public ResponseEntity<WebResponse<MemberResponse>> getById(@PathVariable String id) {
        return ResponseUtil.ok("Member retrieved", memberService.getById(id));
    }

    @Operation(summary = "Register a new member.")
    @PostMapping
    public ResponseEntity<WebResponse<MemberResponse>> register(@Valid @RequestBody CreateMemberRequest request) {
        return ResponseUtil.build(HttpStatus.CREATED, "Member registered", memberService.register(request));
    }
}
