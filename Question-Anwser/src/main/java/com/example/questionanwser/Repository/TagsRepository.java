package com.example.questionanwser.Repository;


import com.example.questionanwser.Model.Tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagsRepository extends JpaRepository<Tags,Long> {
    List<Tags> findByNameContainingIgnoreCase(String name);
    Tags findByNameIgnoreCase(String name);

    Optional<Tags> findByName(String name);

    @Query("SELECT t FROM Tags t LEFT JOIN t.posts p GROUP BY t.tagId ORDER BY COUNT(p) DESC")
    Page<Tags> findPopularTags(Pageable pageable);
}
