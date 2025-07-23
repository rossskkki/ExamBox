package com.siki.exambox.Mapper;

import com.siki.exambox.pojo.category;
import com.siki.exambox.pojo.dto.AddCategoryDTO;
import com.siki.exambox.pojo.exambase;
import com.siki.exambox.pojo.vo.SearchQuestionVO;
import jdk.jfr.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ExBaseMapper {

    @Insert("INSERT INTO exambase(name, category_id, path) VALUES(#{name}, #{categoryId}, #{path})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertExam(exambase exam);

    @Select("SELECT * FROM category")
    List<category> getCategoryList();

    @Select("SELECT * FROM exambase WHERE category_id = #{categoryId}")
    List<exambase> getExamListByCategory(Long categoryId);

    @Insert("INSERT INTO category(name) VALUES(#{name})")
    void insertCategory(AddCategoryDTO category);

    @Select("SELECT exambase.id as id, exambase.name as name, exambase.path as path, category.name as categoryName FROM exambase INNER JOIN category ON exambase.category_id = category.id WHERE exambase.name LIKE CONCAT('%', #{name}, '%')")
    List<SearchQuestionVO> getExamListByName(String name);

    @Select("SELECT * FROM category WHERE id = #{categoryId}")
    String queryById(Integer categoryId);
}
