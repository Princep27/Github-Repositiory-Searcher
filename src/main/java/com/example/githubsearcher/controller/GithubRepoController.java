package com.example.githubsearcher.controller;

import com.example.githubsearcher.dto.ErrorResponse;
import com.example.githubsearcher.dto.RepoListResponse;
import com.example.githubsearcher.dto.RepoResponse;
import com.example.githubsearcher.dto.SearchRequest;
import com.example.githubsearcher.dto.SearchResponse;
import com.example.githubsearcher.service.GithubRepoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "GitHub Repositories", description = "Search GitHub and view stored repositories")
@RestController
@RequestMapping("/api/github")
public class GithubRepoController {

    private final GithubRepoService service;

    public GithubRepoController(GithubRepoService service) {
        this.service = service;
    }

    @Operation(summary = "Search GitHub and save the results",
            description = "Searches GitHub by repository name (partial or full match), optional programming language, "
                    + "sort field (stars, forks or updated) and sort direction (asc or desc). "
                    + "The results are saved in the database. "
                    + "Repositories that already exist are updated, not duplicated.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search completed (the list can be empty)",
                    content = @Content(schema = @Schema(implementation = SearchResponse.class))),
            @ApiResponse(responseCode = "400", description = "Empty query, invalid sort or invalid order value",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "429", description = "GitHub rate limit reached",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "GitHub failed or returned an invalid response",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/search")
    public SearchResponse searchRepositories(@Valid @RequestBody SearchRequest request) {
        List<RepoResponse> repositories = service.searchAndSave(request);

        String message = "Repositories fetched and saved successfully";
        if (repositories.isEmpty()) {
            message = "No repositories found";
        }
        return new SearchResponse(message, repositories);
    }

    @Operation(summary = "Get stored repositories",
            description = "Returns repositories saved in the database. All filters are optional and can be combined. "
                    + "Sorted by stars (default), forks or updated, in descending (default) or ascending order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stored repositories returned",
                    content = @Content(schema = @Schema(implementation = RepoListResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid sort, order or minStars value",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/repositories")
    public RepoListResponse getStoredRepositories(
            @Parameter(description = "Programming language (not case sensitive)", example = "Java")
            @RequestParam(required = false) String language,

            @Parameter(description = "Owner (user or organization) name (not case sensitive)", example = "user123")
            @RequestParam(required = false) String owner,

            @Parameter(description = "Minimum stars count", example = "100")
            @RequestParam(required = false) Integer minStars,

            @Parameter(description = "Sort by: stars (default), forks or updated",
                    schema = @Schema(allowableValues = {"stars", "forks", "updated"}, defaultValue = "stars"))
            @RequestParam(required = false) String sort,

            @Parameter(description = "Sort direction: desc (default, highest first) or asc (lowest first)",
                    schema = @Schema(allowableValues = {"asc", "desc"}, defaultValue = "desc"))
            @RequestParam(required = false) String order) {

        List<RepoResponse> repositories = service.findStoredRepositories(language, owner, minStars, sort, order);
        return new RepoListResponse(repositories);
    }
}