package com.assignment.bookservice.service;

import com.assignment.bookservice.dto.AuthorDto;
import com.assignment.bookservice.dto.AuthorResponseDto;
import com.assignment.bookservice.entity.Author;
import com.assignment.bookservice.entity.Book;
import com.assignment.bookservice.exception.AuthorNotFoundException;
import com.assignment.bookservice.repository.AuthorRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@AllArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    public Author findById(Long authorId) {
        log.debug("Retrieving author with id {}", authorId);
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException(format("Author with id %s not found", authorId)));
    }

    public Author saveAuthor(AuthorDto authorDto) {
        log.debug("Saving author...");
        Author author = Author.builder()
                .firstName(authorDto.getFirstName())
                .lastName(authorDto.getLastName())
                .build();
        return authorRepository.save(author);
    }

    public Page<AuthorResponseDto> findAuthors(Integer pageNumber, Integer pageSize) {
        Page<Author> authors = authorRepository.findAll(PageRequest.of(pageNumber, pageSize));
        List<AuthorResponseDto> responseDtoList = authors.getContent().stream()
                .map(author -> AuthorResponseDto.builder()
                        .id(author.getId())
                        .firstName(author.getFirstName())
                        .lastName(author.getLastName())
                        .books(author.getBooks())
                        .totalBookWorth(calculateTotalBookWorth(author.getBooks()))
                        .build())
                .collect(Collectors.toList());
        return new PageImpl<>(responseDtoList);
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    private BigDecimal calculateTotalBookWorth(List<Book> books) {
        return books.stream()
                .map(book -> book.getPrice().multiply(BigDecimal.valueOf(book.getUnitsSold())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
