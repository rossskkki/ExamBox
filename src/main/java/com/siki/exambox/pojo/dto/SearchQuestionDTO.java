package com.siki.exambox.pojo.dto;

import com.siki.exambox.pojo.QuestionType;
import lombok.Data;

@Data
public class SearchQuestionDTO {
    private String keyword; // 搜索关键词
    private Integer categoryId; // 分类ID
    private QuestionType type; // 题目类型
}
