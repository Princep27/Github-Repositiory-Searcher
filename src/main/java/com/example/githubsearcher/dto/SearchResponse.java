package com.example.githubsearcher.dto;

import java.util.List;

public class SearchResponse {

    private String message;
    private List<RepoResponse> repositories;

    public SearchResponse(String message, List<RepoResponse> repositories) {
        this.message = message;
        this.repositories = repositories;
    }

    public String getMessage() {
        return message;
    }

    public List<RepoResponse> getRepositories() {
        return repositories;
    }
}
