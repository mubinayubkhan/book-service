package com.assignment.bookservice.controller;

import com.assignment.bookservice.configuration.SecurityConfiguration;
import com.assignment.bookservice.dto.AuthorDto;
import com.assignment.bookservice.entity.Author;
import com.assignment.bookservice.entity.Book;
import com.assignment.bookservice.exception.AuthorNotFoundException;
import com.assignment.bookservice.service.AuthorService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@Import(AuthorController.class)
@ContextConfiguration(classes = SecurityConfiguration.class)
class AuthorControllerTest {

    @MockBean
    private AuthorService mockAuthorService;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mockMvc);
    }

    @Test
    void shouldReturnAuthor() throws Exception {
        when(mockAuthorService.findById(1L)).thenReturn(getAuthor());

        MvcResult mvcResult = mockMvc.perform(get("/authors/1"))
                .andExpect(status().isOk())
                .andReturn();

        Author author = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Author.class);
        assertThat(author).isEqualTo(getAuthor());
    }

    @Test
    void verifyGetAuthorFail_404() throws Exception {
        doThrow(new AuthorNotFoundException("Author with id 1 not found"))
                .when(mockAuthorService).findById(any());

        MvcResult mvcResult = mockMvc.perform(get("/authors/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        String errorMsg = mvcResult.getResponse().getContentAsString();
        assertThat(errorMsg).isEqualTo("Author with id 1 not found");
    }

    @Test
    void verifyGetAuthorFail_500() throws Exception {
        doThrow(new RuntimeException("unknown exception"))
                .when(mockAuthorService).findById(any());

        MvcResult mvcResult = mockMvc.perform(get("/authors/1"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        String errorMsg = mvcResult.getResponse().getContentAsString();
        assertThat(errorMsg).isEqualTo("Exception occurred when fetching author with id 1");
    }

    @Test
    void shouldSaveAuthor() throws Exception {
        when(mockAuthorService.saveAuthor(any())).thenReturn(getAuthor());

        MvcResult mvcResult = mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getAuthorDto())))
                .andExpect(status().isCreated())
                .andReturn();

        Author author = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Author.class);
        assertThat(author).isEqualTo(getAuthor());
    }

    @Test
    void shouldDeleteAuthorWithBasicAuth() throws Exception {
        when(mockAuthorService.findById(any()))
                .thenReturn(Author.builder()
                        .id(1L)
                        .firstName("firstName")
                        .lastName("lastName")
                        .build());

        String encoding = Base64.getEncoder().encodeToString(("admin:password").getBytes());

        mockMvc.perform(delete("/authors/1")
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + encoding))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFailIfNotAuth_401() throws Exception {
        mockMvc.perform(delete("/authors/1"))
                .andExpect(status().isUnauthorized());
    }

    private AuthorDto getAuthorDto() {
        return AuthorDto.builder()
                .firstName("firstName")
                .lastName("lastName")
                .build();
    }

    private Author getAuthor() {
        return Author.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .books(List.of(Book.builder()
                        .id(1L)
                        .title("title")
                        .description("desc")
                        .authorId(1L)
                        .unitsSold(10)
                        .price(new BigDecimal("2"))
                        .build()))
                .build();
    }
}