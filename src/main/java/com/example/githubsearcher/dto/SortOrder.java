package com.example.githubsearcher.dto;

import com.example.githubsearcher.exception.InvalidRequestException;
import org.springframework.data.domain.Sort;

public enum SortOrder {

    ASC("asc", Sort.Direction.ASC),
    DESC("desc", Sort.Direction.DESC);

    private final String githubValue;
    private final Sort.Direction direction;

    SortOrder(String githubValue, Sort.Direction direction) {
        this.githubValue = githubValue;
        this.direction = direction;
    }

    public String getGithubValue() {
        return githubValue;
    }

    public Sort.Direction getDirection() {
        return direction;
    }

    public static SortOrder fromString(String text) {
        if (text == null || text.isBlank()) {
            return DESC;
        }
        for (SortOrder order : SortOrder.values()) {
            if (order.githubValue.equalsIgnoreCase(text.trim())) {
                return order;
            }
        }
        throw new InvalidRequestException("order must be one of: asc, desc");
    }
}