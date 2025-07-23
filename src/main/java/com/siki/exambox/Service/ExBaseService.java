package com.siki.exambox.Service;

import com.siki.exambox.pojo.category;
import com.siki.exambox.pojo.dto.AddCategoryDTO;
import com.siki.exambox.pojo.dto.ExamUploadDTO;
import com.siki.exambox.pojo.exambase;
import com.siki.exambox.pojo.vo.SearchQuestionVO;
import jdk.jfr.Category;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ExBaseService {

    exambase uploadPaper(MultipartFile file, ExamUploadDTO examUploadDTO) throws Exception;

    List<category> getCategoryList();

    List<exambase> getExamListByCategory(Long categoryId);

    void addCategory(AddCategoryDTO category);

    List<SearchQuestionVO> getExamListByName(String name);
}
