package com.library.api.service.impl;

import com.library.api.dto.request.CreateBookRequest;
import com.library.api.dto.request.CreateMagazineRequest;
import com.library.api.dto.response.LibraryItemResponse;
import com.library.api.entity.Book;
import com.library.api.entity.LibraryItem;
import com.library.api.entity.Magazine;
import com.library.api.repository.LibraryItemRepository;
import com.library.api.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Default {@link CatalogService}. Reads are marked read-only; the search overloads share a
 * single derived query and differ only in post-filtering.
 *
 * @author stewicca
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {

    private final LibraryItemRepository libraryItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LibraryItemResponse> listAll() {
        return libraryItemRepository.findAll().stream().map(LibraryItemResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryItemResponse> findAvailable() {
        return libraryItemRepository.findAvailable().stream().map(LibraryItemResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryItemResponse> search(String title) {
        return libraryItemRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(LibraryItemResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryItemResponse> search(String title, String type) {
        return libraryItemRepository.findByTitleContainingIgnoreCase(title).stream()
                .filter(item -> item.itemType().equalsIgnoreCase(type))
                .map(LibraryItemResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String[] sortedTitles() {
        String[] titles = libraryItemRepository.findAll().stream()
                .map(LibraryItem::getTitle)
                .toArray(String[]::new);
        Arrays.sort(titles);
        return titles;
    }

    @Override
    @Transactional
    public LibraryItemResponse addBook(CreateBookRequest request) {
        Book book = Book.builder()
                .title(request.title())
                .author(request.author())
                .availableCopies(request.availableCopies())
                .isbn(request.isbn())
                .build();
        return LibraryItemResponse.from(libraryItemRepository.save(book));
    }

    @Override
    @Transactional
    public LibraryItemResponse addMagazine(CreateMagazineRequest request) {
        Magazine magazine = Magazine.builder()
                .title(request.title())
                .author(request.author())
                .availableCopies(request.availableCopies())
                .edition(request.edition())
                .build();
        return LibraryItemResponse.from(libraryItemRepository.save(magazine));
    }
}
