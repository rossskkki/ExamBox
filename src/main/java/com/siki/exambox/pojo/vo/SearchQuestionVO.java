package com.siki.exambox.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchQuestionVO {
    private Long id;
    private String name;
    private  String categoryName;
    private  String path;
}
