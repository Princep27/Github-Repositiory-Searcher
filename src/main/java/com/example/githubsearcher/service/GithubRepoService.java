package com.example.githubsearcher.service;

import com.example.githubsearcher.client.GithubApiClient;
import com.example.githubsearcher.dto.GithubRepoItem;
import com.example.githubsearcher.dto.RepoResponse;
import com.example.githubsearcher.dto.SearchRequest;
import com.example.githubsearcher.dto.SortOption;
import com.example.githubsearcher.dto.SortOrder;
import com.example.githubsearcher.entity.GithubRepo;
import com.example.githubsearcher.exception.InvalidRequestException;
import com.example.githubsearcher.mapper.RepoMapper;
import com.example.githubsearcher.repository.GithubRepoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GithubRepoService {

    private final GithubApiClient githubApiClient;
    private final GithubRepoRepository repository;
    private final RepoMapper repoMapper;

    public GithubRepoService(GithubApiClient githubApiClient, GithubRepoRepository repository, RepoMapper repoMapper) {
        this.githubApiClient = githubApiClient;
        this.repository = repository;
        this.repoMapper = repoMapper;
    }

    public List<RepoResponse> searchAndSave(SearchRequest request) {
        SortOption sortOption = SortOption.fromString(request.getSort());
        SortOrder sortOrder = SortOrder.fromString(request.getOrder());

        // The client applies the criteria: name, language, sort and order
        List<GithubRepoItem> items = githubApiClient.searchRepositories(
                request.getQuery().trim(),
                request.getLanguage(),
                sortOption.getGithubValue(),
                sortOrder.getGithubValue());

        List<GithubRepo> reposToSave = new ArrayList<>();
        for (GithubRepoItem item : items) {
            reposToSave.add(repoMapper.toEntity(item));
        }

        // The id comes from GitHub, so saveAll() inserts new ids and updates existing ones (upsert).
        List<GithubRepo> savedRepos = repository.saveAll(reposToSave);
        return repoMapper.toResponseList(savedRepos);
    }

    public List<RepoResponse> findStoredRepositories(String language, String owner, Integer minStars,
                                                     String sort, String order) {
        int minimumStars = 0;
        if (minStars != null) {
            minimumStars = minStars;
        }
        if (minimumStars < 0) {
            throw new InvalidRequestException("minStars must not be negative");
        }

        SortOption sortOption = SortOption.fromString(sort);
        SortOrder sortOrder = SortOrder.fromString(order);
        Sort sorting = Sort.by(sortOrder.getDirection(), sortOption.getEntityField());

        // An empty text means "no filter"
        String languageFilter = "";
        if (language != null) {
            languageFilter = language.trim();
        }
        String ownerFilter = "";
        if (owner != null) {
            ownerFilter = owner.trim();
        }

        List<GithubRepo> repos = repository.findWithFilters(minimumStars, languageFilter, ownerFilter, sorting);
        return repoMapper.toResponseList(repos);
    }
}