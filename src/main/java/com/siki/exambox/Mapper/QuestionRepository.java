package com.siki.exambox.Mapper;

import com.siki.exambox.pojo.Question;
import com.siki.exambox.pojo.QuestionType;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends ElasticsearchRepository<Question, String> {
//    List<Question> findByContentContaining(String keyword);
//    List<Question> findByContentContainingAndType(String keyword, QuestionType type);
//    List<Question> findByContentContainingAndCategoryId(String keyword, Integer categoryId);
//    List<Question> findByContentContainingAndCategoryIdAndType(String keyword, Integer categoryId, QuestionType type);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"content\": \"?0\"}}]}}")
    List<Question> search(String keyword);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"content\": \"?0\"}}, {\"term\": {\"type\": \"?1\"}}]}}")
    List<Question> searchByType(String keyword, QuestionType type);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"content\": \"?0\"}}, {\"term\": {\"categoryId\": \"?1\"}}]}}")
    List<Question> searchByCategory(String keyword, Integer categoryId);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"content\": \"?0\"}}, {\"term\": {\"categoryId\": \"?1\"}}, {\"term\": {\"type\": \"?2\"}}]}}")
    List<Question> searchByCategoryAndType(String keyword, Integer categoryId, QuestionType type);

    @Query("{\"term\": {\"categoryId\": \"?0\"}}")
    List<Question> findByCategoryId(int i);
}