package com.assignment.bookservice.controller;

import com.assignment.bookservice.dto.BookDto;
import com.assignment.bookservice.entity.Book;
import com.assignment.bookservice.exception.AuthorNotFoundException;
import com.assignment.bookservice.exception.BookAlreadyExistsException;
import com.assignment.bookservice.exception.BookNotFoundException;
import com.assignment.bookservice.response.PaginatedResponse;
import com.assignment.bookservice.service.BookService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static java.lang.String.format;

/**
 * BookController.java
 *
 * RestController handling Rest API Calls (GET, POST & DELETE)
 * for Book resource
 *
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBook(@PathVariable Long id) {
        try {
            Book book = bookService.findBook(id);
            return ResponseEntity.ok(book);
        } catch (BookNotFoundException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            String errorMsg = "Exception occurred when fetching book with id " + id;
            log.warn(errorMsg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getBooks(
            @RequestParam(name = "page") Integer pageNumber,
            @RequestParam(name = "pageSize") Integer pageSize) {
        try {
            Page<Book> books = bookService.findBooks(pageNumber, pageSize);
            return ResponseEntity.ok(
                    PaginatedResponse.<Book>builder()
                            .recordCount(books.getNumberOfElements())
                            .totalRecordCount(books.getTotalElements())
                            .response(books.getContent())
                            .build());
        } catch (Exception ex) {
            String errorMsg = format("Exception occurred when fetching books for page number %s and page size %s",
                    pageNumber, pageSize);
            log.warn(errorMsg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg);
        }
    }

    @PostMapping
    public ResponseEntity<Object> saveBook(@RequestBody @Valid BookDto bookDto) {
        try {
            bookService.saveBook(bookDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (AuthorNotFoundException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        } catch (BookAlreadyExistsException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            String errorMsg = "Exception occurred when saving book";
            log.warn(errorMsg, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBook(@PathVariable Long id) {
        try {
            bookService.findBook(id);
            bookService.deleteBook(id);
            return ResponseEntity.noContent()
                    .build();
        } catch (BookNotFoundException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            String errorMsg = "Exception occurred when deleting book with id " + id;
            log.warn(errorMsg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg);
        }
    }
}
