package com.example.githubsearcher.service;

import com.example.githubsearcher.client.GithubApiClient;
import com.example.githubsearcher.dto.GithubOwner;
import com.example.githubsearcher.dto.GithubRepoItem;
import com.example.githubsearcher.dto.RepoResponse;
import com.example.githubsearcher.dto.SearchRequest;
import com.example.githubsearcher.entity.GithubRepo;
import com.example.githubsearcher.exception.InvalidRequestException;
import com.example.githubsearcher.mapper.RepoMapper;
import com.example.githubsearcher.repository.GithubRepoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubRepoServiceTest {

    @Mock
    private GithubApiClient githubApiClient;

    @Mock
    private GithubRepoRepository repository;

    private GithubRepoService service;

    @BeforeEach
    void setUp() {
        service = new GithubRepoService(githubApiClient, repository, new RepoMapper());
    }

    private GithubRepoItem createItem() {
        GithubOwner owner = new GithubOwner();
        owner.setLogin("alice");

        GithubRepoItem item = new GithubRepoItem();
        item.setId(1L);
        item.setName("demo");
        item.setOwner(owner);
        item.setLanguage("Java");
        item.setStars(50);
        item.setUpdatedAt(Instant.parse("2024-01-01T12:00:00Z"));
        return item;
    }

    private GithubRepo createEntity() {
        GithubRepo repo = new GithubRepo();
        repo.setId(1L);
        repo.setName("demo");
        repo.setOwner("alice");
        repo.setStars(50);
        return repo;
    }

    @Test
    void searchAndSave_sendsNameLanguageSortAndOrderToClientAndSavesResults() {
        List<GithubRepoItem> items = new ArrayList<>();
        items.add(createItem());
        List<GithubRepo> saved = new ArrayList<>();
        saved.add(createEntity());
        when(githubApiClient.searchRepositories("spring boot", "Java", "forks", "asc")).thenReturn(items);
        when(repository.saveAll(anyList())).thenReturn(saved);

        SearchRequest request = new SearchRequest();
        request.setQuery(" spring boot ");
        request.setLanguage("Java");
        request.setSort("forks");
        request.setOrder("asc");
        List<RepoResponse> result = service.searchAndSave(request);

        assertEquals(1, result.size());
        assertEquals("alice", result.get(0).getOwner());
        verify(repository).saveAll(anyList());
    }

    @Test
    void searchAndSave_usesStarsAndDescendingByDefault() {
        when(githubApiClient.searchRepositories("zzz", null, "stars", "desc")).thenReturn(new ArrayList<>());
        when(repository.saveAll(anyList())).thenReturn(new ArrayList<>());

        SearchRequest request = new SearchRequest();
        request.setQuery("zzz");

        assertTrue(service.searchAndSave(request).isEmpty());
    }

    @Test
    void searchAndSave_rejectsInvalidSort() {
        SearchRequest request = new SearchRequest();
        request.setQuery("spring");
        request.setSort("bogus");

        assertThrows(InvalidRequestException.class, () -> service.searchAndSave(request));
    }

    @Test
    void searchAndSave_rejectsInvalidOrder() {
        SearchRequest request = new SearchRequest();
        request.setQuery("spring");
        request.setOrder("sideways");

        assertThrows(InvalidRequestException.class, () -> service.searchAndSave(request));
    }

    @Test
    void findStoredRepositories_filtersByLanguageOwnerAndSortsByUpdatedAscending() {
        service.findStoredRepositories("Java", "alice", 100, "updated", "asc");

        verify(repository).findWithFilters(100, "Java", "alice", Sort.by(Sort.Direction.ASC, "lastUpdated"));
    }

    @Test
    void findStoredRepositories_usesDefaultsWhenNoFiltersGiven() {
        service.findStoredRepositories(null, null, null, null, null);

        verify(repository).findWithFilters(0, "", "", Sort.by(Sort.Direction.DESC, "stars"));
    }

    @Test
    void findStoredRepositories_rejectsNegativeMinStars() {
        assertThrows(InvalidRequestException.class,
                () -> service.findStoredRepositories(null, null, -5, null, null));
    }

    @Test
    void findStoredRepositories_rejectsInvalidOrder() {
        assertThrows(InvalidRequestException.class,
                () -> service.findStoredRepositories(null, null, null, null, "sideways"));
    }
}