package com.siki.exambox.Controller;

import com.siki.exambox.Service.ExBaseService;
import com.siki.exambox.Service.QuestionService;
import com.siki.exambox.common.Result;
import com.siki.exambox.pojo.Question;
import com.siki.exambox.pojo.category;
import com.siki.exambox.pojo.dto.AddCategoryDTO;
import com.siki.exambox.pojo.dto.ExamUploadDTO;
import com.siki.exambox.pojo.dto.QuestionUploadDTO;
import com.siki.exambox.pojo.dto.SearchQuestionDTO;
import com.siki.exambox.pojo.exambase;
import com.siki.exambox.pojo.vo.SearchQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/exambase")
@Slf4j
public class ExBaseController {

    @Autowired
    private ExBaseService exBaseService;

    @Autowired
    private QuestionService questionService;

    /**
     * 上传试卷
     * @param file
     * @param examUploadDTO
     * @return
     */
    @PostMapping("/upload")
    public Result<exambase> uploadPaper(@RequestParam("file") MultipartFile file, ExamUploadDTO examUploadDTO) throws Exception {
        log.info("上传试卷：{}", examUploadDTO);
        exambase exam = exBaseService.uploadPaper(file, examUploadDTO);
        questionService.uploadFile(file,examUploadDTO); // 上传题目到 Elasticsearch
        return Result.success(exam);
    }

    /**
     * 上传题目
     * @param questionUploadDTO
     */
    @PostMapping("/uploadQuestion")
    public Result<String> uploadQuestion(@RequestBody QuestionUploadDTO questionUploadDTO) throws Exception {
        log.info("上传题目, {}", questionUploadDTO);
        questionService.uploadQuestion(questionUploadDTO);
        return Result.success("题目上传成功");
    }

    /**
     * 获取试卷分类列表
     */
    @GetMapping("/categories")
    public Result <List<category>> getCategoryList() {
        log.info("获取试卷分类列表");
        List<category> categories = exBaseService.getCategoryList();
        return Result.success(categories);
    }

    /**
     * 获取指定分类的试卷列表
     * @param categoryId
     * @return
     */
    @GetMapping("/exam/{id}")
    public Result<List<exambase>> getExamListByCategory(@PathVariable("id") Long categoryId) {
        log.info("获取分类ID为 {} 的试卷列表", categoryId);
        List<exambase> exams = exBaseService.getExamListByCategory(categoryId);
        return Result.success(exams);
    }

    /**
     * 获取指定分类的题目列表
     * @param categoryId
     * @return
     */
    @GetMapping("/questions/{id}")
    public Result<List<Question>> getQuestionListByCategory(@PathVariable("id") Long categoryId) {
        log.info("获取分类ID为 {} 的题目列表", categoryId);
        List<Question> exams = questionService.getExamListByCategory(categoryId);
        return Result.success(exams);
    }

    /**
     * 添加试卷分类
     * @param category
     * @return
     */
    @PostMapping("/addCategory")
    public Result<String> addCategory(@RequestBody AddCategoryDTO category) {
        log.info("添加试卷分类：{}", category);
        exBaseService.addCategory(category);
        return Result.success("分类添加成功");
    }

    /**
     * 根据试卷名称获取试卷列表
      * @param name
     * @return
     */
    @PostMapping("/exam/search")
    public Result<List<SearchQuestionVO>> getExamListByName(@RequestParam String name) {
        log.info("根据试卷名称获取试卷列表: {}", name);
        List<SearchQuestionVO> exams = exBaseService.getExamListByName(name);
        return Result.success(exams);
    }

    /**
     * 搜索题目
     * @param searchQuestionDTO
     * @return
     */
    @PostMapping("/search")
    public Result<List<Question>> searchQuestions(@RequestBody SearchQuestionDTO searchQuestionDTO) {
        log.info("搜索题目: {}", searchQuestionDTO);
        List<Question> questions = questionService.searchQuestions(searchQuestionDTO);
        log.info("搜索到 {} 道题目", questions.size());
        return Result.success(questions);
    }
}
