package com.assignment.bookservice.dto;

import com.assignment.bookservice.entity.Book;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * AuthorResponseDTO.java
 *
 * Data Transfer Object handling response of Author resource
 *
 */
@Data
@Builder
public class AuthorResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private List<Book> books;
    private BigDecimal totalBookWorth;
}
