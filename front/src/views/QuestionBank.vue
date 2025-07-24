<template>
  <div class="question-bank-container">
    <van-nav-bar title="题库" />
    <div class="main-content">
      <van-sidebar v-model="activeCategory" @change="onCategoryChange">
        <van-sidebar-item
          v-for="category in categories"
          :key="category.id"
          :title="category.name"
        />
      </van-sidebar>
      <div class="question-list-content">
        <van-empty v-if="!questions.length" description="暂无题目" />
        <van-list v-else>
          <van-cell-group v-for="question in questions" :key="question.id">
            <van-cell :title="`ID:${question.id}`" class="small-text" >
              <template #label>
                <div class="question-content">{{ question.content }}</div>
                <div class="question-options" v-if="question.options">
                  <div v-for="(option, index) in question.options" :key="index">{{ String.fromCharCode(65 + index) }}. {{ option }}</div>
                </div>
              </template>
            </van-cell>
            <van-button type="primary" size="small" @click="toggleAnswer(question.id)">查看答案</van-button>
            <van-cell class="answer" v-if="shownAnswers[question.id]" title="答案" :value="question.answer" />
            <div class="question-file" v-if="question.fileName">选自：{{ question.fileName }}</div>
          </van-cell-group>
        </van-list>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { getCategoryList, getQuestionListByCategory } from "../api/exambase";

export default {
  name: "QuestionBank",
  setup() {
    const activeCategory = ref(0);
    const categories = ref([]);
    const questions = ref([]);
    const shownAnswers = ref({});

    const fetchCategories = async () => {
      try {
        const response = await getCategoryList();
        categories.value = response.data.data;
        if (categories.value.length > 0) {
          fetchQuestions(categories.value[0].id);
        }
      } catch (error) {
        console.error("获取分类失败", error);
      }
    };

    const fetchQuestions = async (categoryId) => {
      try {
        const response = await getQuestionListByCategory(categoryId);
        questions.value = response.data.data;
      } catch (error) {
        console.error("获取试卷列表失败", error);
      }
    };

    const onCategoryChange = (index) => {
      const categoryId = categories.value[index].id;
      fetchQuestions(categoryId);
    };

    const openPdf = (path) => {
      // 假设PDF在 public/upload-files 下
      const url = `/upload-files/${path}`;
      window.open(url, "_blank");
    };

    const toggleAnswer = (id) => {
      shownAnswers.value[id] = !shownAnswers.value[id];
    };

    onMounted(() => {
      fetchCategories();
    });

    return {
      activeCategory,
      categories,
      questions,
      onCategoryChange,
      openPdf,
      shownAnswers,
      toggleAnswer,
    };
  },
};
</script>

<style scoped>
.question-bank-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 16px;
}

.main-content {
  display: flex;
  flex: 1;
}

.question-list-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}
</style>

<style scoped>
.question-bank-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 16px;
}

.main-content {
  display: flex;
  flex: 1;
}

.question-list-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}
.question-content {
  font-size: 16px;
  font-weight: bold;
  color: black;
}

.question-options{
  font-size: 14px;
  color: black;
}

.small-text {
  font-size: 9px;
  color: gray;
  margin-top: 10px;
}
van-cell-group {
  margin-bottom: 20px;
}

.question-file{
  font-size: 11px;
  color: gray;
  text-align: right;
}

.answer{
  flex-direction: column;
}

.answer :deep(.van-cell__value){
  text-align: left;
}
</style>
