package com.example.githubsearcher.repository;

import com.example.githubsearcher.entity.GithubRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class GithubRepoRepositoryTest {

    @Autowired
    private GithubRepoRepository repository;

    private final Sort byStars = Sort.by(Sort.Direction.DESC, "stars");

    private GithubRepo createRepo(Long id, String owner, String language, int stars) {
        GithubRepo repo = new GithubRepo();
        repo.setId(id);
        repo.setName("repo" + id);
        repo.setOwner(owner);
        repo.setLanguage(language);
        repo.setStars(stars);
        repo.setLastUpdated(Instant.now());
        return repo;
    }

    @Test
    void savingSameIdTwiceUpdatesInsteadOfDuplicating() {
        repository.saveAndFlush(createRepo(1L, "alice", "Java", 10));
        repository.saveAndFlush(createRepo(1L, "alice", "Java", 99));

        assertEquals(1, repository.count());
        assertEquals(99, repository.findById(1L).get().getStars());
    }

    @Test
    void filtersByLanguageAndMinStarsAndSortsByStars() {
        repository.save(createRepo(1L, "alice", "Java", 500));
        repository.save(createRepo(2L, "alice", "Java", 50));
        repository.save(createRepo(3L, "bob", "java", 200));
        repository.save(createRepo(4L, "bob", "Go", 900));

        List<GithubRepo> result = repository.findWithFilters(100, "JAVA", "", byStars);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
    }

    @Test
    void filtersByOwnerIgnoringCase() {
        repository.save(createRepo(1L, "alice", "Java", 500));
        repository.save(createRepo(2L, "bob", "Java", 300));
        repository.save(createRepo(3L, "alice", "Go", 100));

        List<GithubRepo> result = repository.findWithFilters(0, "", "ALICE", byStars);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
    }

    @Test
    void returnsEverythingAboveMinStarsWhenNoLanguageOrOwnerGiven() {
        repository.save(createRepo(1L, "alice", "Java", 500));
        repository.save(createRepo(2L, "bob", "Go", 900));
        repository.save(createRepo(3L, "bob", "Go", 5));

        List<GithubRepo> result = repository.findWithFilters(100, "", "", byStars);

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
    }
}