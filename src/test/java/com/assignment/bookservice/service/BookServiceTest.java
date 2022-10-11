package com.assignment.bookservice.service;

import com.assignment.bookservice.dto.BookDto;
import com.assignment.bookservice.entity.Book;
import com.assignment.bookservice.exception.AuthorNotFoundException;
import com.assignment.bookservice.exception.BookAlreadyExistsException;
import com.assignment.bookservice.exception.BookNotFoundException;
import com.assignment.bookservice.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository mockBookRepository;

    @Mock
    private AuthorService mockAuthorService;

    @InjectMocks
    private BookService bookService;

    @Test
    void verifyBookIsReturned() {
        //given
        Book book = getBookWithId(1L);
        when(mockBookRepository.findById(1L)).thenReturn(Optional.of(book));

        //when
        Book actualBook = bookService.findBook(1L);

        //then
        assertThat(actualBook).isEqualTo(book);
    }

    @Test
    void verifyBookNotFoundExceptionIsThrown() {
        //given
        doThrow(new BookNotFoundException("Book with id 1 not found")).when(mockBookRepository).findById(any());

        //then
        assertThatThrownBy(() -> bookService.findBook(1L)).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void verifyBookAlreadyExistExceptionIsThrown() {
        //given
        Book book = getBookWithId(1L);
        when(mockBookRepository.findByTitleAndAuthorId(any(), any())).thenReturn(Optional.of(book));

        //when
        assertThatThrownBy(() -> bookService.saveBook(new BookDto())).isInstanceOf(BookAlreadyExistsException.class);

        //then
        verifyNoInteractions(mockAuthorService);
        verify(mockBookRepository, never()).save(any());
    }

    @Test
    void verifyBookIsNotSavedIfAuthorDoesntExist() {
        //given
        when(mockBookRepository.findByTitleAndAuthorId(any(), any())).thenReturn(Optional.empty());
        doThrow(new AuthorNotFoundException("Author with id 1 not found")).when(mockAuthorService).findById(any());

        //when
        assertThatThrownBy(() -> bookService.saveBook(new BookDto())).isInstanceOf(AuthorNotFoundException.class);
    }

    @Test
    void verifyBookIsSaved() {
        //given
        when(mockBookRepository.findByTitleAndAuthorId(any(), any())).thenReturn(Optional.empty());

        //when
        bookService.saveBook(BookDto.builder().title("title").description("desc").build());

        //then
        verify(mockBookRepository, times(1)).findByTitleAndAuthorId(any(), any());
        verify(mockAuthorService, times(1)).findById(any());
        verify(mockBookRepository, times(1)).save(any());
    }

    @Test
    void verifyBookIsDeleted() {
        //when
        bookService.deleteBook(1L);

        //then
        verify(mockBookRepository, times(1)).deleteById(1L);
    }

    @Test
    void verifyBooksAreRetrieved() {
        //when
        bookService.findBooks(1, 10);

        //then
        verify(mockBookRepository, times(1)).findAll(PageRequest.of(1, 10));
    }

    private Book getBookWithId(Long id) {
        return Book.builder()
                .id(id)
                .title("title")
                .description("desc")
                .build();
    }
}