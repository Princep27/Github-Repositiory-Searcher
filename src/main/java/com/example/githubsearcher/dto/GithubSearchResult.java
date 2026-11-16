package com.example.githubsearcher.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GithubSearchResult {

    private List<GithubRepoItem> items;

    public List<GithubRepoItem> getItems() {
        return items;
    }

    public void setItems(List<GithubRepoItem> items) {
        this.items = items;
    }
}
