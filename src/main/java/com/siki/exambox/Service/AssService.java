package com.siki.exambox.Service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel"
)
public interface AssService {
//    public String chat(String message);
    @SystemMessage("请解析以下试卷文本，提取题目信息，并严格按照以下JSON格式返回（不要包含额外字段或注释）：\n" +
            "[\n" +
            "  {\n" +
            "    \"number\": \"题号\",\n" +
            "    \"content\": \"题目文本\",\n" +
            "    \"type\": \"CHOICE/FILL_IN/TRUE_FALSE/ESSAY/UNKNOWN\",\n" +
            "    \"options\": [\"选项A\", \"选项B\"], // 选择题才有\n" +
            "    \"answer\": \"答案内容\"\n" +
            "  }\n" +
            "]\n" +
            "请注意：莫名其妙出现在不该出现的地方的数字可能为页码，应忽略。你响应内容的开头与结尾都应该是方括号[]，中间是JSON对象的数组形式。请确保返回的JSON格式正确且完整。" )
    public String chat(@UserMessage String message);
}
