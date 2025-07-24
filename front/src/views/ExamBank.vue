<template>
  <div class="exam-bank-container">
    <van-nav-bar title="试卷" />
    <div class="main-content">
      <van-sidebar v-model="activeCategory" @change="onCategoryChange">
        <van-sidebar-item v-for="category in categories" :key="category.id" :title="category.name" />
      </van-sidebar>
      <div class="exam-list-content">
        <van-empty v-if="!exams.length" description="暂无试卷" />
        <van-list v-else>
          <van-cell v-for="exam in exams" :key="exam.id" :title="exam.name" is-link @click="openPdf(exam.path)"  />
        </van-list>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { getCategoryList, getExamListByCategory } from '../api/exambase';

export default {
  name: 'ExamBank',
  setup() {
    const activeCategory = ref(0);
    const categories = ref([]);
    const exams = ref([]);

    const fetchCategories = async () => {
      try {
        const response = await getCategoryList();
        categories.value = response.data.data;
        if (categories.value.length > 0) {
          fetchExams(categories.value[0].id);
        }
      } catch (error) {
        console.error('获取分类失败', error);
      }
    };

    const fetchExams = async (categoryId) => {
      try {
        const response = await getExamListByCategory(categoryId);
        exams.value = response.data.data;
      } catch (error) {
        console.error('获取试卷列表失败', error);
      }
    };

    const onCategoryChange = (index) => {
      const categoryId = categories.value[index].id;
      fetchExams(categoryId);
    };

    const openPdf = (path) => {
      // 假设PDF在 public/upload-files 下
      const url = `/upload-files/${path}`;
      window.open(url, '_blank');
    };

    onMounted(() => {
      fetchCategories();
    });

    return {
      activeCategory,
      categories,
      exams,
      onCategoryChange,
      openPdf
    };
  },
};
</script>

<style scoped>
.exam-bank-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 16px;
}

.main-content {
  display: flex;
  flex: 1;
}

.exam-list-content {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}
</style>

// ... existing code ...