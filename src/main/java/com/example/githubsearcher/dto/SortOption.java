package com.example.githubsearcher.dto;

import com.example.githubsearcher.exception.InvalidRequestException;

public enum SortOption {

    STARS("stars", "stars"),
    FORKS("forks", "forks"),
    UPDATED("updated", "lastUpdated");

    private final String githubValue;
    private final String entityField;

    SortOption(String githubValue, String entityField) {
        this.githubValue = githubValue;
        this.entityField = entityField;
    }

    public String getGithubValue() {
        return githubValue;
    }

    public String getEntityField() {
        return entityField;
    }

    public static SortOption fromString(String text) {
        if (text == null || text.isBlank()) {
            return STARS;
        }
        for (SortOption option : SortOption.values()) {
            if (option.githubValue.equalsIgnoreCase(text.trim())) {
                return option;
            }
        }
        throw new InvalidRequestException("sort must be one of: stars, forks, updated");
    }
}
