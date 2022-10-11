package com.assignment.bookservice.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "book")
@EqualsAndHashCode
public class Book {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private int unitsSold;
    private String genre;
    private Long authorId;
}
