package com.assignment.bookservice.controller;

import com.assignment.bookservice.dto.AuthorDto;
import com.assignment.bookservice.dto.AuthorResponseDto;
import com.assignment.bookservice.entity.Author;
import com.assignment.bookservice.exception.AuthorNotFoundException;
import com.assignment.bookservice.response.PaginatedResponse;
import com.assignment.bookservice.service.AuthorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static java.lang.String.format;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Author controller class for fetching, creating and deleting author resource.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAuthor(@PathVariable Long id) {
        try {
            log.debug("Retrieving author with id {}", id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(authorService.findById(id));
        } catch (AuthorNotFoundException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            String errorMsg = "Exception occurred when fetching author with id " + id;
            log.warn(errorMsg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg);
        }
    }

    @PostMapping
    public ResponseEntity<Object> saveAuthor(@RequestBody @Valid AuthorDto authorDto) {
        try {
            log.debug("Saving author..");
            Author author = authorService.saveAuthor(authorDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(author);
        } catch (Exception ex) {
            String errorMsg = "Exception occurred when saving author";
            log.warn(errorMsg);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getAuthors(
            @RequestParam(name = "page") Integer pageNumber,
            @RequestParam(name = "pageSize") Integer pageSize) {
        try {
            Page<AuthorResponseDto> authors = authorService.findAuthors(pageNumber, pageSize);
            return ResponseEntity.ok(
                    PaginatedResponse.<AuthorResponseDto>builder()
                            .recordCount(authors.getNumberOfElements())
                            .totalRecordCount(authors.getTotalElements())
                            .response(authors.getContent())
                            .build());
        } catch (Exception ex) {
            String errorMsg = format("Exception occurred when getting authors with page number %s and page size %s",
                    pageNumber, pageSize);
            log.warn(errorMsg, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAuthor(@PathVariable Long id) {
        try {
            Author author = authorService.findById(id);
            if (isEmpty(author.getBooks())) {
                authorService.deleteAuthor(id);
            }
            return ResponseEntity.noContent().build();
        } catch (AuthorNotFoundException ex) {
            log.warn(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ex.getMessage());
        } catch (Exception ex) {
            String errorMsg = "Exception occurred when deleting author with id: " + id;
            log.warn(errorMsg, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg);
        }
    }
}
