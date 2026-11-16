package com.example.githubsearcher.controller;

import com.example.githubsearcher.dto.RepoResponse;
import com.example.githubsearcher.exception.GithubApiException;
import com.example.githubsearcher.exception.InvalidRequestException;
import com.example.githubsearcher.exception.RateLimitExceededException;
import com.example.githubsearcher.service.GithubRepoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GithubRepoController.class)
class GithubRepoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GithubRepoService service;

    private List<RepoResponse> oneRepository() {
        RepoResponse repo = new RepoResponse();
        repo.setId(123456L);
        repo.setName("spring-boot-example");
        repo.setOwner("user123");
        repo.setLanguage("Java");
        repo.setStars(450);
        repo.setForks(120);
        repo.setLastUpdated(Instant.parse("2024-01-01T12:00:00Z"));

        List<RepoResponse> list = new ArrayList<>();
        list.add(repo);
        return list;
    }

    @Test
    void search_returnsSavedRepositories() throws Exception {
        when(service.searchAndSave(any())).thenReturn(oneRepository());

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"spring boot\",\"language\":\"Java\",\"sort\":\"stars\",\"order\":\"asc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Repositories fetched and saved successfully"))
                .andExpect(jsonPath("$.repositories[0].id").value(123456))
                .andExpect(jsonPath("$.repositories[0].owner").value("user123"))
                .andExpect(jsonPath("$.repositories[0].lastUpdated").value("2024-01-01T12:00:00Z"));
    }

    @Test
    void search_returnsMessageWhenNothingFound() throws Exception {
        when(service.searchAndSave(any())).thenReturn(new ArrayList<>());

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"zzz\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No repositories found"))
                .andExpect(jsonPath("$.repositories").isEmpty());
    }

    @Test
    void search_returns400WhenQueryIsBlank() throws Exception {
        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\" \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("query must not be blank"));
    }

    @Test
    void search_returns400WhenOrderIsInvalid() throws Exception {
        when(service.searchAndSave(any())).thenThrow(new InvalidRequestException("order must be one of: asc, desc"));

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"spring\",\"order\":\"sideways\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("order must be one of: asc, desc"));
    }

    @Test
    void search_returns429WhenRateLimitReached() throws Exception {
        when(service.searchAndSave(any())).thenThrow(new RateLimitExceededException("rate limit"));

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"spring\"}"))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("rate limit"));
    }

    @Test
    void search_returns502WhenGithubFails() throws Exception {
        when(service.searchAndSave(any())).thenThrow(new GithubApiException("github down"));

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"spring\"}"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getRepositories_returnsStoredRepositories() throws Exception {
        when(service.findStoredRepositories("Java", "user123", 100, "stars", "asc")).thenReturn(oneRepository());

        mockMvc.perform(get("/api/github/repositories")
                        .param("language", "Java")
                        .param("owner", "user123")
                        .param("minStars", "100")
                        .param("sort", "stars")
                        .param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositories[0].name").value("spring-boot-example"))
                .andExpect(jsonPath("$.repositories[0].owner").value("user123"));
    }

    @Test
    void getRepositories_returns400WhenMinStarsIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/github/repositories").param("minStars", "abc"))
                .andExpect(status().isBadRequest());
    }
}