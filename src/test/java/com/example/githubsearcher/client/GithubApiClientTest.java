package com.example.githubsearcher.client;

import com.example.githubsearcher.dto.GithubRepoItem;
import com.example.githubsearcher.exception.GithubApiException;
import com.example.githubsearcher.exception.RateLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GithubApiClientTest {

    private MockRestServiceServer server;
    private GithubApiClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        client = new GithubApiClient(builder, "https://api.github.com", "");
    }

    @Test
    void sendsNameLanguageSortAndOrderToGithub() {
        server.expect(requestTo(containsString("q=spring%20boot%20in%3Aname%20language%3A%22Java%22")))
                .andExpect(requestTo(containsString("sort=forks")))
                .andExpect(requestTo(containsString("order=asc")))
                .andRespond(withSuccess("{\"items\": []}", MediaType.APPLICATION_JSON));

        client.searchRepositories("spring boot", "Java", "forks", "asc");

        server.verify();
    }

    @Test
    void leavesOutLanguageWhenItIsEmpty() {
        server.expect(requestTo(containsString("q=spring%20in%3Aname&sort=stars&order=desc")))
                .andRespond(withSuccess("{\"items\": []}", MediaType.APPLICATION_JSON));

        client.searchRepositories("spring", "", "stars", "desc");

        server.verify();
    }

    @Test
    void returnsRepositoriesFromGithubResponse() {
        String json = """
                {"total_count": 1, "items": [{
                  "id": 1, "name": "demo", "description": "a demo", "language": "Java",
                  "owner": {"login": "alice"},
                  "stargazers_count": 10, "forks_count": 2, "watchers_count": 10, "open_issues_count": 1,
                  "updated_at": "2024-01-01T12:00:00Z"}]}
                """;
        server.expect(requestTo(containsString("/search/repositories")))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<GithubRepoItem> items = client.searchRepositories("spring boot", null, "stars", "desc");

        assertEquals(1, items.size());
        assertEquals("demo", items.get(0).getName());
        assertEquals("alice", items.get(0).getOwner().getLogin());
        assertEquals(10, items.get(0).getStars());
    }

    @Test
    void returnsEmptyListWhenNothingFound() {
        server.expect(requestTo(containsString("/search/repositories")))
                .andRespond(withSuccess("{\"total_count\": 0, \"items\": []}", MediaType.APPLICATION_JSON));

        List<GithubRepoItem> items = client.searchRepositories("spring boot", null, "stars", "desc");

        assertTrue(items.isEmpty());
    }

    @Test
    void throwsRateLimitExceptionWhenGithubReturns403() {
        server.expect(requestTo(containsString("/search/repositories")))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        assertThrows(RateLimitExceededException.class,
                () -> client.searchRepositories("spring boot", null, "stars", "desc"));
    }

    @Test
    void throwsGithubApiExceptionWhenGithubFails() {
        server.expect(requestTo(containsString("/search/repositories")))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(GithubApiException.class,
                () -> client.searchRepositories("spring boot", null, "stars", "desc"));
    }

    @Test
    void throwsGithubApiExceptionWhenResponseHasNoItems() {
        server.expect(requestTo(containsString("/search/repositories")))
                .andRespond(withSuccess("{\"message\": \"unexpected\"}", MediaType.APPLICATION_JSON));

        assertThrows(GithubApiException.class,
                () -> client.searchRepositories("spring boot", null, "stars", "desc"));
    }
}