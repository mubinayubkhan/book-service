package com.assignment.bookservice.controller;

import com.assignment.bookservice.configuration.SecurityConfiguration;
import com.assignment.bookservice.dto.BookDto;
import com.assignment.bookservice.entity.Book;
import com.assignment.bookservice.exception.AuthorNotFoundException;
import com.assignment.bookservice.exception.BookAlreadyExistsException;
import com.assignment.bookservice.exception.BookNotFoundException;
import com.assignment.bookservice.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(BookController.class)
@ContextConfiguration(classes = SecurityConfiguration.class)
class BookControllerTest {

    @MockBean
    private BookService mockBookService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void shouldReturnBook() throws Exception {
        when(mockBookService.findBook(1L)).thenReturn(getBook());

        MvcResult mvcResult = mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andReturn();

        Book book = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Book.class);
        assertThat(book).isEqualTo(getBook());
    }

    @Test
    void verifyGetBookFail_404() throws Exception {
        doThrow(new BookNotFoundException("Book with id 1 not found"))
                .when(mockBookService).findBook(any());

        MvcResult mvcResult = mockMvc.perform(get("/books/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        String errorMsg = mvcResult.getResponse().getContentAsString();
        assertThat(errorMsg).isEqualTo("Book with id 1 not found");
    }

    @Test
    void verifyGetBookFail_500() throws Exception {
        //given
        doThrow(new RuntimeException("unknown exception"))
                .when(mockBookService).findBook(any());

        //when
        MvcResult mvcResult = mockMvc.perform(get("/books/1"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        //then
        String errorMsg = mvcResult.getResponse().getContentAsString();
        assertThat(errorMsg).isEqualTo("Exception occurred when fetching book with id 1");
    }

    @Test
    void shouldSaveBook() throws Exception {
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getBookDto())))
                .andExpect(status().isCreated());

        verify(mockBookService, times(1)).saveBook(getBookDto());
    }

    @Test
    void verifySaveBookFail_409() throws Exception {
        doThrow(new BookAlreadyExistsException("Book already exists for this author"))
                .when(mockBookService).saveBook(any());

        MvcResult mvcResult = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getBookDto())))
                .andExpect(status().isConflict())
                .andReturn();

        String errorMsg = mvcResult.getResponse().getContentAsString();
        assertThat(errorMsg).isEqualTo("Book already exists for this author");
    }

    @Test
    void verifySaveBookFail_404() throws Exception {
        doThrow(new AuthorNotFoundException("Author with id 1 not found"))
                .when(mockBookService).saveBook(any());

        MvcResult mvcResult = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getBookDto())))
                .andExpect(status().isNotFound())
                .andReturn();

        String errorMsg = mvcResult.getResponse().getContentAsString();
        assertThat(errorMsg).isEqualTo("Author with id 1 not found");
    }

    @Test
    void verifySaveBookFail_500() throws Exception {
        doThrow(new RuntimeException("unknown exception"))
                .when(mockBookService).saveBook(any());

        MvcResult mvcResult = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getBookDto())))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String errorMsg = mvcResult.getResponse().getContentAsString();
        assertThat(errorMsg).isEqualTo("Exception occurred when saving book");
    }

    @Test
    void shouldDeleteABookWithBasicAuth() throws Exception {
        String encoding = Base64.getEncoder().encodeToString(("admin:password").getBytes());

        mockMvc.perform(delete("/books/1")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + encoding))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFailIfNotAuth_401() throws Exception {
        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verifyDeleteABookFail_404() throws Exception {
        //given
        doThrow(new BookNotFoundException("Book with id 1 not found"))
                .when(mockBookService).findBook(any());

        String encoding = Base64.getEncoder().encodeToString(("admin:password").getBytes());

        //when
        MvcResult mvcResult = mockMvc.perform(delete("/books/1")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + encoding))
                .andExpect(status().isNotFound())
                .andReturn();

        //then
        String errorMsg = mvcResult.getResponse().getContentAsString();
        assertThat(errorMsg).isEqualTo("Book with id 1 not found");
    }

    private BookDto getBookDto() {
        return BookDto.builder()
                .title("title")
                .description("desc")
                .authorId(1L)
                .genre("Fantasy")
                .price(new BigDecimal("2"))
                .unitsSold(10)
                .build();
    }

    private Book getBook() {
        return Book.builder()
                .id(1L)
                .title("title")
                .description("desc")
                .authorId(1L)
                .unitsSold(10)
                .genre("Fantasy")
                .price(new BigDecimal("2"))
                .build();
    }
}