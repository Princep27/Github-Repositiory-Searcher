package com.example.githubsearcher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class SearchRequest {

    @Schema(description = "Repository name to search for (partial or full match)",
            example = "spring boot", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "query must not be blank")
    private String query;

    @Schema(description = "Programming language (optional)", example = "Java")
    private String language;

    @Schema(description = "Sort by: stars (default), forks or updated",
            example = "stars", allowableValues = {"stars", "forks", "updated"})
    private String sort;

    @Schema(description = "Sort direction: desc (default, highest first) or asc (lowest first)",
            example = "desc", allowableValues = {"asc", "desc"})
    private String order;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}