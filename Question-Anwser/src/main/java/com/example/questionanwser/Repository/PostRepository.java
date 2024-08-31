package com.example.questionanwser.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.questionanwser.Model.Post;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Page<Post> findByTagsName(String tagName, Pageable pageable);
    @Query(value = """
SELECT
    q.post_id AS post_id,
    q.created_at AS created_at,
    q.title AS title,
    q.content AS content,
    q.downvotes AS downvotes,
    q.upvotes AS upvotes,
    q.id AS id,
    ts_headline(q.title, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') as title_highlighted,
    ts_headline(q.content, websearch_to_tsquery(:q), 'startSel=<em> stopSel=</em>') as content_highlighted,
    greatest(ts_rank_cd(q.search_vector, websearch_to_tsquery(:q)), ts_rank_cd(q.search_vector, websearch_to_tsquery('simple', :q))) as ranking 
FROM
    posts q
WHERE
    q.search_vector @@ websearch_to_tsquery(:q) OR
    q.search_vector @@ websearch_to_tsquery('simple', :q)

UNION ALL

SELECT
    q.post_id AS post_id,
    q.created_at AS created_at,
    q.title AS title,
    q.content AS content,
    q.downvotes AS downvotes,
    q.upvotes AS upvotes,
    q.id AS id,
    ts_headline(q.title, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*'), 'startSel=<em> stopSel=</em>') as title_highlighted,
    ts_headline(q.content, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*'), 'startSel=<em> stopSel=</em>') as content_highlighted,
    ts_rank_cd(q.search_vector, to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*')||':*')) as ranking
FROM
    posts q
WHERE
    q.search_vector @@ to_tsquery(regexp_substr(:q,  '([[:word:]]|[[:digit:]])*') ||':*') AND
    :q ~* '^([[:word:]]|[[:digit:]])*$'
ORDER BY
    ranking DESC

""", nativeQuery = true)
    List<Post> findBySearch(@Param("q") String query);

    Page<Post> findByUserId(Long userId, Pageable pageable);
    Page<Post> findByUser_Id(Long userId,Pageable pageable);

    @Query(value = "SELECT p.* FROM posts p INNER JOIN followers f ON p.post_id = f.post_id WHERE f.username = :username",
            countQuery = "SELECT count(p.post_id) FROM posts p INNER JOIN followers f ON p.post_id = f.post_id WHERE f.username = :username",
            nativeQuery = true)
    Page<Post> findFollowedPostsByUsername(@Param("username") String username, Pageable pageable);


    @Query("SELECT COUNT(u) FROM Post p JOIN p.upvoters u WHERE u = :username")
    Long sumUpvotesForPostsByUser(@Param("username") String username);

    @Query("SELECT COUNT(u) FROM Post p JOIN p.downvoters u WHERE u = :username")
    Long sumDownvotesForPostsByUser(@Param("username") String username);
    @Query("SELECT COUNT(p) FROM Post p WHERE :username MEMBER OF p.followers")
    Long countFollowedPostsByUser(@Param("username") String username);





    @Query("SELECT COUNT(p) FROM Post p JOIN p.upvoters u WHERE u = :username")
    int countTotalUpvotesByUsername(@Param("username") String username);

    @Query("SELECT COUNT(p) FROM Post p JOIN p.downvoters d WHERE d = :username")
    int countTotalDownvotesByUsername(@Param("username") String username);

    Long countByUser_Id(int userId);

}
