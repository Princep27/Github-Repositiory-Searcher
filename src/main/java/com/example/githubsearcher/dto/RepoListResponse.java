package com.example.githubsearcher.dto;

import java.util.List;

public class RepoListResponse {

    private List<RepoResponse> repositories;

    public RepoListResponse(List<RepoResponse> repositories) {
        this.repositories = repositories;
    }

    public List<RepoResponse> getRepositories() {
        return repositories;
    }
}
