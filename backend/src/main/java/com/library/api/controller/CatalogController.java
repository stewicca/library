package com.library.api.controller;

import com.library.api.constant.ApiRoute;
import com.library.api.dto.request.CreateBookRequest;
import com.library.api.dto.request.CreateMagazineRequest;
import com.library.api.dto.response.LibraryItemResponse;
import com.library.api.dto.response.WebResponse;
import com.library.api.service.CatalogService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Catalog endpoints. Browsing is open to any authenticated user; mutating the catalog
 * is restricted to staff ({@code LIBRARIAN}/{@code ADMIN}).
 *
 * @author stewicca
 * @version 1.0
 */
@RestController
@RequestMapping(ApiRoute.BOOKS)
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Catalog", description = "Browse, search and manage library items (books & magazines)")
public class CatalogController {

    private final CatalogService catalogService;

    @Operation(summary = "List the catalog, optionally filtered by title and/or type.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<WebResponse<List<LibraryItemResponse>>> list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type) {
        List<LibraryItemResponse> data;
        if (title != null && type != null) {
            data = catalogService.search(title, type);
        } else if (title != null) {
            data = catalogService.search(title);
        } else {
            data = catalogService.listAll();
        }
        return ResponseUtil.ok("Catalog retrieved", data);
    }

    @Operation(summary = "List only items that currently have copies available.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/available")
    public ResponseEntity<WebResponse<List<LibraryItemResponse>>> available() {
        return ResponseUtil.ok("Available items retrieved", catalogService.findAvailable());
    }

    @Operation(summary = "List all catalog titles, sorted alphabetically.")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/titles")
    public ResponseEntity<WebResponse<String[]>> titles() {
        return ResponseUtil.ok("Titles retrieved", catalogService.sortedTitles());
    }

    @Operation(summary = "Add a book to the catalog (staff only).")
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping
    public ResponseEntity<WebResponse<LibraryItemResponse>> addBook(@Valid @RequestBody CreateBookRequest request) {
        return ResponseUtil.build(HttpStatus.CREATED, "Book added", catalogService.addBook(request));
    }

    @Operation(summary = "Add a magazine to the catalog (staff only).")
    @PreAuthorize("hasAnyRole('LIBRARIAN','ADMIN')")
    @PostMapping("/magazines")
    public ResponseEntity<WebResponse<LibraryItemResponse>> addMagazine(
            @Valid @RequestBody CreateMagazineRequest request) {
        return ResponseUtil.build(HttpStatus.CREATED, "Magazine added", catalogService.addMagazine(request));
    }
}
