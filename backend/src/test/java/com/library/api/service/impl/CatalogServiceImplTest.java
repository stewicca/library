package com.library.api.service.impl;

import com.library.api.dto.request.CreateBookRequest;
import com.library.api.dto.request.CreateMagazineRequest;
import com.library.api.dto.response.LibraryItemResponse;
import com.library.api.entity.Book;
import com.library.api.entity.LibraryItem;
import com.library.api.entity.Magazine;
import com.library.api.repository.LibraryItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceImplTest {

    @Mock
    private LibraryItemRepository libraryItemRepository;

    @InjectMocks
    private CatalogServiceImpl catalogService;

    private Book book() {
        return Book.builder().id("b1").title("Clean Code").author("Martin").availableCopies(2).isbn("isbn").build();
    }

    private Magazine magazine() {
        return Magazine.builder().id("m1").title("Clean Times").author("Editor").availableCopies(1).edition(7).build();
    }

    @Test
    @DisplayName("search(title) delegates to a case-insensitive title query")
    void searchByTitle() {
        when(libraryItemRepository.findByTitleContainingIgnoreCase("clean"))
                .thenReturn(List.of(book(), magazine()));

        List<LibraryItemResponse> results = catalogService.search("clean");

        assertThat(results).hasSize(2);
        assertThat(results).extracting(LibraryItemResponse::type)
                .containsExactlyInAnyOrder("BOOK", "MAGAZINE");
    }

    @Test
    @DisplayName("search(title, type) — overload narrows results to the requested type")
    void searchByTitleAndType() {
        when(libraryItemRepository.findByTitleContainingIgnoreCase("clean"))
                .thenReturn(List.of(book(), magazine()));

        List<LibraryItemResponse> onlyBooks = catalogService.search("clean", "BOOK");

        assertThat(onlyBooks).hasSize(1);
        assertThat(onlyBooks.getFirst().type()).isEqualTo("BOOK");
    }

    @Test
    @DisplayName("sortedTitles returns titles sorted alphabetically as an array")
    void sortedTitles() {
        Book zebra = Book.builder().id("b2").title("Zebra").author("a").availableCopies(1).isbn("i").build();
        when(libraryItemRepository.findAll()).thenReturn(List.<LibraryItem>of(zebra, book()));

        String[] titles = catalogService.sortedTitles();

        assertThat(titles).containsExactly("Clean Code", "Zebra");
    }

    @Test
    @DisplayName("addBook persists a Book and returns its polymorphic description")
    void addBook() {
        when(libraryItemRepository.save(any(LibraryItem.class))).thenAnswer(inv -> inv.getArgument(0));

        LibraryItemResponse response = catalogService.addBook(
                new CreateBookRequest("Clean Code", "Martin", 5, "978-0132350884"));

        assertThat(response.type()).isEqualTo("BOOK");
        assertThat(response.availableCopies()).isEqualTo(5);
        assertThat(response.description()).contains("Book:").contains("ISBN");
    }

    @Test
    @DisplayName("addMagazine persists a Magazine and returns its polymorphic description")
    void addMagazine() {
        when(libraryItemRepository.save(any(LibraryItem.class))).thenAnswer(inv -> inv.getArgument(0));

        LibraryItemResponse response = catalogService.addMagazine(
                new CreateMagazineRequest("Clean Times", "Editor", 3, 7));

        assertThat(response.type()).isEqualTo("MAGAZINE");
        assertThat(response.description()).contains("Magazine:").contains("edition 7");
    }
}
