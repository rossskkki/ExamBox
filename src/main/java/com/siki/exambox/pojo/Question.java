package com.siki.exambox.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "questions")
@Data
public class Question {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    @JsonProperty("content")
    private String content;

    @Field(type = FieldType.Keyword)
    private Integer categoryId; // 分类ID

    @Field(type = FieldType.Text)
    private String fileName;

    @Field(type = FieldType.Keyword)
    @JsonProperty("number")
    private String number; // 题号

    @Field(type = FieldType.Text)
    @JsonProperty("options")
    private List<String> options; // 选项

    @Field(type = FieldType.Keyword)
    @JsonProperty("type")
    private QuestionType type; // 题目类型

    @Field(type = FieldType.Text)
    @JsonProperty("answer")
    private String answer; // 答案内容
}