package com.siki.exambox.pojo.dto;

import com.siki.exambox.pojo.QuestionType;
import lombok.Data;

import java.util.List;

@Data
public class QuestionUploadDTO {
    private  String content; // 题目内容
    private Integer categoryId; // 分类ID
    private List<String> options; // 选项列表
    private QuestionType type; // 题目类型
    private String answer; // 答案内容
}
