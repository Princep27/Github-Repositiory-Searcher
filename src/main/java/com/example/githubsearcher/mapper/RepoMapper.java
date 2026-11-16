package com.example.githubsearcher.mapper;

import com.example.githubsearcher.dto.GithubRepoItem;
import com.example.githubsearcher.dto.RepoResponse;
import com.example.githubsearcher.entity.GithubRepo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RepoMapper {

    public GithubRepo toEntity(GithubRepoItem item) {
        GithubRepo repo = new GithubRepo();
        repo.setId(item.getId());
        repo.setName(item.getName());
        repo.setDescription(item.getDescription());
        if (item.getOwner() != null) {
            repo.setOwner(item.getOwner().getLogin());
        }
        repo.setLanguage(item.getLanguage());
        repo.setStars(item.getStars());
        repo.setForks(item.getForks());
        repo.setWatchers(item.getWatchers());
        repo.setOpenIssues(item.getOpenIssues());
        repo.setLastUpdated(item.getUpdatedAt());
        return repo;
    }

    public RepoResponse toResponse(GithubRepo repo) {
        RepoResponse response = new RepoResponse();
        response.setId(repo.getId());
        response.setName(repo.getName());
        response.setDescription(repo.getDescription());
        response.setOwner(repo.getOwner());
        response.setLanguage(repo.getLanguage());
        response.setStars(repo.getStars());
        response.setForks(repo.getForks());
        response.setWatchers(repo.getWatchers());
        response.setOpenIssues(repo.getOpenIssues());
        response.setLastUpdated(repo.getLastUpdated());
        return response;
    }

    public List<RepoResponse> toResponseList(List<GithubRepo> repos) {
        List<RepoResponse> responses = new ArrayList<>();
        for (GithubRepo repo : repos) {
            responses.add(toResponse(repo));
        }
        return responses;
    }
}
