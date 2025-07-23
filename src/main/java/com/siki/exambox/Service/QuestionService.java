package com.siki.exambox.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siki.exambox.Mapper.ExBaseMapper;
import com.siki.exambox.Mapper.QuestionRepository;
import com.siki.exambox.pojo.Question;
import com.siki.exambox.pojo.QuestionType;
import com.siki.exambox.pojo.dto.ExamUploadDTO;
import com.siki.exambox.pojo.dto.QuestionUploadDTO;
import com.siki.exambox.pojo.dto.SearchQuestionDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Service
@Slf4j
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExBaseMapper exBaseMapper;

    @Autowired
    private AssService assService;

//    @Autowired
//    private ElasticsearchRestTemplate elasticsearchTemplate;

    private static final ObjectMapper mapper = new ObjectMapper();

    // 题目类型映射（根据实际试卷调整）
    private static final Map<String, QuestionType> TYPE_MAPPING = new HashMap<>();
    static {
        TYPE_MAPPING.put("选择题", QuestionType.CHOICE);
        TYPE_MAPPING.put("填空题", QuestionType.FILL_IN);
        TYPE_MAPPING.put("解答题", QuestionType.ESSAY);
        TYPE_MAPPING.put("判断题", QuestionType.TRUE_FALSE);
    }

    // 题号模式（如1.、一、(1)等）
    private static final Pattern QUESTION_NUMBER_PATTERN =
            Pattern.compile("^\\s*([一二三四五六七八九十0-9①②③④⑤⑥⑦⑧⑨⑩]+[.、)）]|\\([一二三四五六七八九十0-9]+\\))\\s*");

    // 题目类型模式（如"一、选择题"）
    private static final Pattern QUESTION_TYPE_PATTERN =
            Pattern.compile("^\\s*[一二三四五六七八九十]+[.、)）]\\s*([\\u4e00-\\u9fa5]+题)\\s*");

    public void uploadFile(MultipartFile file, ExamUploadDTO examUploadDTO) throws IOException {
        String textContent = this.extractTextFromPdf(file);
        String ans = "";
        try {
            log.info("开始调用 AssistantService 进行处理");
            ans = assService.chat(textContent);
        } catch (Exception e) {
            log.error("调用 AssistantService 处理失败: {}", e.getMessage());
            throw new RuntimeException(e);
        }finally {
            log.info("AssistantService 处理完成");
            log.info(ans);
        }

        // 解析文本内容并提取题目
        List<Question> questions  = null;
        try {
            questions = parseQuestions(ans,examUploadDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        try (BufferedReader reader = new BufferedReader(
//                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
//
//            Question currentQuestion = null;
//            QuestionType currentType = QuestionType.UNKNOWN;
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                line = line.trim();
//                if (line.isEmpty()) continue;
//
//                // 检查是否为题型标题（如"一、选择题"）
//                Matcher typeMatcher = QUESTION_TYPE_PATTERN.matcher(line);
//                if (typeMatcher.find()) {
//                    String typeName = typeMatcher.group(1);
//                    currentType = TYPE_MAPPING.getOrDefault(typeName, QuestionType.UNKNOWN);
//                    continue;
//                }
//
//                // 检查是否为新题目的开始
//                Matcher numberMatcher = QUESTION_NUMBER_PATTERN.matcher(line);
//                if (numberMatcher.find()) {
//                    // 保存上一题
//                    if (currentQuestion != null) {
//                        questions.add(currentQuestion);
//                    }
//
//                    // 创建新题目
//                    currentQuestion = new Question();
//                    currentQuestion.setId(UUID.randomUUID().toString());
//                    currentQuestion.setFileName(fileName);
//                    currentQuestion.setType(currentType);
//                    currentQuestion.setContent(line);
//                    currentQuestion.setNumber(numberMatcher.group(1).trim());
//                }
//                // 追加到当前题目
//                else if (currentQuestion != null) {
//                    currentQuestion.setContent(currentQuestion.getContent() + "\n" + line);
//                }
//            }
//
//            // 添加最后一个题目
//            if (currentQuestion != null) {
//                questions.add(currentQuestion);
//            }
//        }

        // 批量保存到Elasticsearch
        questionRepository.saveAll(questions);
        log.info("成功上传 {} 道题目到 Elasticsearch", questions.size());
    }

    public List<Question> searchQuestions(SearchQuestionDTO searchQuestionDTO) {
        String keyword = searchQuestionDTO.getKeyword();
        Integer categoryId = searchQuestionDTO.getCategoryId();
        QuestionType type = searchQuestionDTO.getType();
        log.info("搜索题目: keyword={}, categoryId={}, type={}", keyword, categoryId, type);
        if (type != null  && categoryId != null) {
            return questionRepository.searchByCategoryAndType(keyword, categoryId, type);
        } else if (categoryId != null) {
            return questionRepository.searchByCategory(keyword, categoryId);
        } else if (type != null ) {
            return questionRepository.searchByType(keyword, type);
        } else if (keyword == null || keyword.isEmpty()) {
            // 如果没有关键词，则返回所有题目
            return getAllQuestions();
        }else{
            return questionRepository.search(keyword);
        }
    }

    public List<Question> getAllQuestions() {
        return (List<Question>) questionRepository.findAll();
    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        log.info("开始解析PDF文件: {}", file.getOriginalFilename());
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
        finally {
            log.info("PDF文件解析完成: {}", file.getOriginalFilename());
        }
    }

    public static List<Question> parseQuestions(String jsonResponse, ExamUploadDTO examUploadDTO) throws Exception {
        try {
            // 基础校验：确保是JSON数组
            if (!jsonResponse.trim().startsWith("[") || !jsonResponse.trim().endsWith("]")) {
                jsonResponse = "[" + jsonResponse + "]"; // 尝试修正
            }

            // 解析JSON
            JsonNode root = mapper.readTree(jsonResponse);
            List<Question> questions = new ArrayList<>();

            // 遍历数组元素
            if (root.isArray()) {
                for (JsonNode node : root) {
                    Question question = mapper.treeToValue(node, Question.class);
                    // 字段完整性校验
                    if (question.getContent() == null || question.getContent().isEmpty()) {
                        continue; // 跳过无效条目
                    }
                    question.setId(UUID.randomUUID().toString()); // 生成唯一ID
                    question.setFileName(examUploadDTO.getName());
                    question.setCategoryId(examUploadDTO.getCategoryId());
                    questions.add(question);
                }
            }

            return questions;
        } catch (Exception e) {
            // 记录错误日志
            log.error("Failed to parse LLM response: " + e.getMessage());
            // 可实现降级策略（如返回空列表或抛出特定异常）
            throw new Exception("Failed to parse LLM response", e);
        }
    }

    public List<Question> getExamListByCategory(Long categoryId) {
        log.info("获取分类ID为 {} 的题目列表", categoryId);
        List<Question> questions = questionRepository.findByCategoryId(categoryId.intValue());
        if (questions == null || questions.isEmpty()) {
            log.warn("未找到分类ID为 {} 的题目", categoryId);
            return Collections.emptyList();
        }
        return questions;
    }

    public void uploadQuestion(QuestionUploadDTO questionUploadDTO) {
        log.info("上传题目: {}", questionUploadDTO);
        if (questionUploadDTO.getContent() == null || questionUploadDTO.getContent().isEmpty()) {
            log.error("题目内容不能为空");
            throw new IllegalArgumentException("题目内容不能为空");
        }
        //检查分类id是否存在在数据库
        String category = exBaseMapper.queryById(questionUploadDTO.getCategoryId());
        if (category == null) {
            log.error("分类ID {} 不存在", questionUploadDTO.getCategoryId());
            throw new IllegalArgumentException("分类ID不存在");
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUploadDTO, question);
        question.setId(UUID.randomUUID().toString()); // 生成唯一ID
        // 保存到Elasticsearch
        questionRepository.save(question);
        log.info("题目上传成功: {}", question);
    }
}