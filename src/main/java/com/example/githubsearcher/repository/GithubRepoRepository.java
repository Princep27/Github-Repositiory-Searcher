package com.example.githubsearcher.repository;

import com.example.githubsearcher.entity.GithubRepo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GithubRepoRepository extends JpaRepository<GithubRepo, Long> {

    @Query("SELECT r FROM GithubRepo r "
            + "WHERE r.stars >= :minStars "
            + "AND (:language = '' OR LOWER(r.language) = LOWER(:language)) "
            + "AND (:owner = '' OR LOWER(r.owner) = LOWER(:owner))")
    List<GithubRepo> findWithFilters(@Param("minStars") int minStars,
                                     @Param("language") String language,
                                     @Param("owner") String owner,
                                     Sort sort);
}