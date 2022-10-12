package com.assignment.bookservice.service;

import com.assignment.bookservice.dto.BookDto;
import com.assignment.bookservice.entity.Book;
import com.assignment.bookservice.exception.BookAlreadyExistsException;
import com.assignment.bookservice.exception.BookNotFoundException;
import com.assignment.bookservice.repository.BookRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.String.format;

/**
 * BookService.java
 *
 * Service class providing business functionality for Book resource
 *
 */
@Slf4j
@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;

    public Book findBook(Long id) {
        log.debug("Retrieving book with id {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(format("Book not found with id: %s", id)));
    }

    public void saveBook(BookDto bookDto) {
        //check for duplicate book entry
        Optional<Book> bookExists = bookRepository.findByTitleAndAuthorId(bookDto.getTitle(), bookDto.getAuthorId());
        if (bookExists.isPresent()) {
            throw new BookAlreadyExistsException(format("Book with title %s and author with id %s already exists", bookDto.getTitle(), bookDto.getAuthorId()));
        }

        //check if author exists
        authorService.findById(bookDto.getAuthorId());

        Book book = toBook(bookDto);
        bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        log.debug("Deleting book with id {}", id);
        bookRepository.deleteById(id);
        log.debug("Deleted book with id {}", id);
    }

    public Page<Book> findBooks(Integer pageNumber, Integer pageSize) {
        return bookRepository.findAll(PageRequest.of(pageNumber, pageSize));
    }

    private static Book toBook(BookDto bookDto) {
        return Book.builder()
                .title(bookDto.getTitle())
                .description(bookDto.getDescription())
                .authorId(bookDto.getAuthorId())
                .price(bookDto.getPrice())
                .unitsSold(bookDto.getUnitsSold())
                .genre(bookDto.getGenre())
                .build();
    }
}
