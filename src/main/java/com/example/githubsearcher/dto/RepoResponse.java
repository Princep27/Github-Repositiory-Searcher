package com.example.githubsearcher.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;


public class RepoResponse {

    @Schema(description = "Repository id from GitHub", example = "123456")
    private Long id;

    @Schema(description = "Repository name", example = "spring-boot-example")
    private String name;

    @Schema(description = "Repository description", example = "An example repository for Spring Boot")
    private String description;

    @Schema(description = "Owner (user or organization) name", example = "user123")
    private String owner;

    @Schema(description = "Main programming language", example = "Java")
    private String language;

    @Schema(description = "Stars count", example = "450")
    private int stars;

    @Schema(description = "Forks count", example = "120")
    private int forks;

    @Schema(description = "Watchers count", example = "450")
    private int watchers;

    @Schema(description = "Open issues count", example = "3")
    private int openIssues;

    @Schema(description = "Last updated date", example = "2024-01-01T12:00:00Z")
    private Instant lastUpdated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public int getForks() {
        return forks;
    }

    public void setForks(int forks) {
        this.forks = forks;
    }

    public int getWatchers() {
        return watchers;
    }

    public void setWatchers(int watchers) {
        this.watchers = watchers;
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public void setOpenIssues(int openIssues) {
        this.openIssues = openIssues;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}