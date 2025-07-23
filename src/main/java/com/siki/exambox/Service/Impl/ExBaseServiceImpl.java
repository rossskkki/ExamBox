package com.siki.exambox.Service.Impl;

import com.siki.exambox.Mapper.ExBaseMapper;
import com.siki.exambox.Service.ExBaseService;
import com.siki.exambox.pojo.category;
import com.siki.exambox.pojo.dto.AddCategoryDTO;
import com.siki.exambox.pojo.dto.ExamUploadDTO;
import com.siki.exambox.pojo.exambase;
import com.siki.exambox.pojo.vo.SearchQuestionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ExBaseServiceImpl implements ExBaseService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private ExBaseMapper exBaseMapper;

//    @Autowired
//    private QuestionParseService questionParseService; // 假设有一个PDF解析服务

    @Override
    @Transactional
    public exambase uploadPaper(MultipartFile file, ExamUploadDTO examUploadDTO) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }

        // 获取文件原始名称
        String originalFilename = file.getOriginalFilename();
        // 生成新的文件名
        String fileName = UUID.randomUUID().toString() + getFileExtension(originalFilename);

        // 确保上传目录存在
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 构建文件保存路径
        Path filePath = Paths.get(uploadDir, fileName);

        // 保存文件到服务器
        Files.copy(file.getInputStream(), filePath);
        log.info("文件已保存到: {}", filePath);

        // 构建exambase对象并保存到数据库
        exambase exam = new exambase();
        exam.setName(examUploadDTO.getName());
        exam.setCategoryId(examUploadDTO.getCategoryId());
        exam.setPath(fileName);

        exBaseMapper.insertExam(exam);
        log.info("试卷信息已保存到数据库, id: {}", exam.getId());

        // 解析PDF文件并保存题目到数据库
//        questionParseService.parseAndSave(file, exam.getId());

        return exam;
    }

    @Override
    public List<category> getCategoryList() {
        List<category> categories = exBaseMapper.getCategoryList();
        log.info("获取试卷分类列表，数量: {}", categories.size());
        return categories;
    }

    @Override
    public List<exambase> getExamListByCategory(Long categoryId) {
        List<exambase> exams = exBaseMapper.getExamListByCategory(categoryId);
        log.info("获取分类ID为 {} 的试卷列表，数量: {}", categoryId, exams.size());
        return exams;
    }

    @Override
    public void addCategory(AddCategoryDTO category) {
        if (category == null || category.getName() == null || category.getName().isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        // 这里可以添加逻辑将分类保存到数据库
         exBaseMapper.insertCategory(category);
        log.info("添加试卷分类: {}", category.getName());
    }

    @Override
    public List<SearchQuestionVO> getExamListByName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("试卷名称不能为空");
        }
        List<SearchQuestionVO> exams = exBaseMapper.getExamListByName(name);
        log.info("根据试卷名称获取试卷列表: {}, 数量: {}", name, exams.size());
        return exams;
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}
