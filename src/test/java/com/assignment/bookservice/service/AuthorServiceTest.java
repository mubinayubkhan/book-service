package com.assignment.bookservice.service;

import com.assignment.bookservice.dto.AuthorDto;
import com.assignment.bookservice.dto.AuthorResponseDto;
import com.assignment.bookservice.entity.Author;
import com.assignment.bookservice.entity.Book;
import com.assignment.bookservice.exception.AuthorNotFoundException;
import com.assignment.bookservice.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository mockAuthorRepository;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void verifyAuthorFoundById() {
        //given
        Author author = getAuthorById(1L);
        when(mockAuthorRepository.findById(1L)).thenReturn(Optional.of(author));

        //when
        Author actualAuthor = authorService.findById(1L);

        //then
        assertThat(actualAuthor).isEqualTo(author);
    }

    @Test
    void verifyAuthorNotFoundExceptionIsThrownIsNotExist() {
        //given
        doThrow(new AuthorNotFoundException("Author with id 1 not found"))
                .when(mockAuthorRepository).findById(any());

        //then
        assertThatThrownBy(() -> authorService.findById(1L)).isInstanceOf(AuthorNotFoundException.class);
    }

    @Test
    void verifyAuthorIsSaved() {
        //when
        authorService.saveAuthor(new AuthorDto());

        //then
        verify(mockAuthorRepository, times(1)).save(any());
    }

    @Test
    void verifyFindAuthorsByPagination() {
        //given
        Page<Author> authors = new PageImpl<>(getAuthors());
        when(mockAuthorRepository.findAll(any(Pageable.class))).thenReturn(authors);

        //when
        Page<AuthorResponseDto> authorsPage = authorService.findAuthors(0, 10);

        //then
        assertThat(authorsPage.getTotalElements()).isEqualTo(2);

        AuthorResponseDto dto1 = authorsPage.getContent().get(0);
        assertThat(dto1.getId()).isEqualTo(1L);
        assertThat(dto1.getFirstName()).isEqualTo("firstName1");
        assertThat(dto1.getLastName()).isEqualTo("lastName1");
        assertThat(dto1.getBooks().size()).isEqualTo(2);
        assertThat(dto1.getTotalBookWorth()).isEqualTo(new BigDecimal("50.70"));

        AuthorResponseDto dto2 = authorsPage.getContent().get(1);
        assertThat(dto2.getId()).isEqualTo(2L);
        assertThat(dto2.getFirstName()).isEqualTo("firstName2");
        assertThat(dto2.getLastName()).isEqualTo("lastName2");
        assertThat(dto2.getBooks().size()).isEqualTo(2);
        assertThat(dto2.getTotalBookWorth()).isEqualTo(new BigDecimal("279.0"));
    }

    @Test
    void verifyDeleteAuthor() {
        //when
        authorService.deleteAuthor(1L);

        //then
        verify(mockAuthorRepository, times(1)).deleteById(1L);
    }

    private Author getAuthorById(long authorId) {
        return Author.builder()
                .id(authorId)
                .build();
    }

    private List<Author> getAuthors() {
        return List.of(
                Author.builder()
                        .id(1L)
                        .firstName("firstName1")
                        .lastName("lastName1")
                        .books(List.of(
                                Book.builder()
                                        .id(1L)
                                        .price(new BigDecimal("2.22"))
                                        .unitsSold(10)
                                        .build(),
                                Book.builder()
                                        .id(2L)
                                        .price(new BigDecimal("1.9"))
                                        .unitsSold(15)
                                        .build()
                        ))
                        .build(),
                Author.builder()
                        .id(2L)
                        .firstName("firstName2")
                        .lastName("lastName2")
                        .books(List.of(
                                Book.builder()
                                        .id(10L)
                                        .price(new BigDecimal("9"))
                                        .unitsSold(9)
                                        .build(),
                                Book.builder()
                                        .id(20L)
                                        .price(new BigDecimal("9.9"))
                                        .unitsSold(20)
                                        .build()
                        ))
                        .build()
        );
    }
}