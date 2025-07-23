package com.siki.exambox.common;

import com.siki.exambox.pojo.Question;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EsIndexInitializer {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    // 项目启动时执行
    @PostConstruct
    public void initIndex() {
        // 1. 获取索引操作对象
        IndexOperations indexOps = esTemplate.indexOps(Question.class);

        // 2. 检查索引是否存在，不存在则创建
        if (!indexOps.exists()) {
            // 3. 可以自定义索引设置（分片、副本等）
            indexOps.createWithMapping();
            // createWithMapping() 会根据 User 类的注解自动生成映射
            log.info("索引 Question 创建成功");
        }
    }
}