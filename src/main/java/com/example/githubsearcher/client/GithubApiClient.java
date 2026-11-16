package com.example.githubsearcher.client;

import com.example.githubsearcher.dto.GithubRepoItem;
import com.example.githubsearcher.dto.GithubSearchResult;
import com.example.githubsearcher.exception.GithubApiException;
import com.example.githubsearcher.exception.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Component
public class GithubApiClient {

    private final RestClient restClient;
    private final String baseUrl;

    public GithubApiClient(RestClient.Builder restClientBuilder,
                           @Value("${github.api.url}") String baseUrl,
                           @Value("${github.token:}") String token) {
        this.baseUrl = baseUrl;

        restClientBuilder.defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json");
        restClientBuilder.defaultHeader("X-GitHub-Api-Version", "2022-11-28");
        if (token != null && !token.isBlank()) {
            restClientBuilder.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        }
        this.restClient = restClientBuilder.build();
    }

    public List<GithubRepoItem> searchRepositories(String name, String language, String sort, String order) {
        String query = buildQuery(name, language);

        // "{q}" is replaced by the query and encoded safely (spaces, quotes, etc.)
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/search/repositories")
                .queryParam("q", "{q}")        // name and language
                .queryParam("sort", sort)      // stars, forks or updated
                .queryParam("order", order)    // asc or desc
                .queryParam("per_page", 30)
                .encode()
                .buildAndExpand(query)
                .toUri();

        try {
            GithubSearchResult result = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(GithubSearchResult.class);

            if (result == null || result.getItems() == null) {
                throw new GithubApiException("GitHub returned an invalid response");
            }
            return result.getItems();

        } catch (HttpClientErrorException ex) {
            // GitHub answers with 403 or 429 when the rate limit is reached
            int status = ex.getStatusCode().value();
            if (status == 403 || status == 429) {
                throw new RateLimitExceededException("GitHub rate limit exceeded, please try again later");
            }
            throw new GithubApiException("GitHub API returned an error (status " + status + ")");

        } catch (RestClientException ex) {
            // GitHub is down, unreachable, or sent something that is not valid JSON
            throw new GithubApiException("GitHub is unavailable or returned an invalid response");
        }
    }

    private String buildQuery(String name, String language) {
        String query = name.trim() + " in:name";
        if (language != null && !language.isBlank()) {
            query = query + " language:\"" + language.trim() + "\"";
        }
        return query;
    }
}