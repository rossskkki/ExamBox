<template>
  <div class="search-container">
    <van-nav-bar
      title="题目搜索"
      left-text="返回"
      left-arrow
      @click-left="goBack"
    />

    <!-- 搜索条件区域 -->
    <div class="search-filters">
      <van-search
        v-model="searchKeyword"
        placeholder="请输入题目关键字"
        show-action
        @search="onSearch"
      >
        <template #action>
          <div @click="onSearch">搜索</div>
        </template>
      </van-search>

      <van-cell-group>
        <van-cell
          title="分类"
          :value="selectedCategoryName || '请选择分类'"
          is-link
          @click="handleCategoryClick"
        />

        <van-cell
          title="题目类型"
          :value="selectedTypeName || '请选择题目类型'"
          is-link
          @click="handleTypeClick"
        />
      </van-cell-group>
    </div>

    <!-- 分类选择器 -->
    <van-popup
      v-model:show="showCategoryPicker"
      position="bottom"
      :close-on-click-overlay="true"
      @update:show="onCategoryPickerUpdate"
    >
      <van-picker
        :columns="categoryColumns"
        @confirm="onCategoryConfirm"
        @cancel="onCategoryCancel"
        show-toolbar
        title="选择分类"
      />
    </van-popup>

    <!-- 题目类型选择器 -->
    <van-popup
      v-model:show="showTypePicker"
      position="bottom"
      :close-on-click-overlay="true"
      @update:show="onTypePickerUpdate"
    >
      <van-picker
        :columns="typeColumns"
        @confirm="onTypeConfirm"
        @cancel="onTypeCancel"
        show-toolbar
        title="选择题目类型"
      />
    </van-popup>

      <div class="question-list-content">
        <van-empty v-if="!searchResults.length" description="暂无题目" />
        <van-list v-else>
          <van-cell-group v-for="question in searchResults" :key="question.id">
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
</template>

<script>
import { ref, computed, onMounted, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import {getCategoryList} from "../api/exambase";


export default {
  name: 'ExamSearch',
  setup() {
    const { proxy } = getCurrentInstance();
    const router = useRouter();

    // data
    const searchKeyword = ref('');
    const searchResults = ref([]);
    const loading = ref(false);
    const finished = ref(false);
    const categoryOptions = ref([]);
    const selectedCategoryId = ref(null);
    const selectedCategoryName = ref('');
    const showCategoryPicker = ref(false);
    const typeOptions = ref([]);
    const selectedType = ref(null);
    const selectedTypeName = ref('');
    const showTypePicker = ref(false);
    const shownAnswers = ref({});

    // computed
    const categoryColumns = computed(() => categoryOptions.value);
    const typeColumns = computed(() => typeOptions.value);

    // methods
    const goBack = () => {
      router.go(-1);
    };

    const forceClosePickers = () => {
      showCategoryPicker.value = false;
      showTypePicker.value = false;
      console.log('强制关闭选择器');
    };

    const handleCategoryClick = () => {
      console.log('点击分类，当前状态:', showCategoryPicker.value);
      // 先确保其他选择器关闭
      showTypePicker.value = false;
      showCategoryPicker.value = true;
      console.log('设置分类选择器为:', showCategoryPicker.value);
    };

    const handleTypeClick = () => {
      console.log('点击类型，当前状态:', showTypePicker.value);
      // 先确保其他选择器关闭
      showCategoryPicker.value = false;
      showTypePicker.value = true;
      console.log('设置类型选择器为:', showTypePicker.value);
    };

    const loadCategories = async () => {
      try {
        const res = await getCategoryList();
        categoryOptions.value = [
          { text: '全部分类', value: null },
          ...res.data.data.map(item => ({
            text: item.name,
            value: item.id
          }))
        ];
      } catch (error) {
        console.error('加载分类失败:', error);
        categoryOptions.value = [{ text: '全部分类', value: null }];
      }
    };

    const loadQuestionTypes = async () => {
      typeOptions.value = [
        { text: '全部类型', value: null },
        { text: '单选题', value: 'CHOICE' },
        { text: '判断题', value: 'TRUE_FALSE' },
        { text: '填空题', value: 'FILL_IN' },
        { text: '简答题', value: 'ESSAY' }
      ];
    };

    const toggleAnswer = (id) => {
      shownAnswers.value[id] = !shownAnswers.value[id];
    };

    const onCategoryConfirm = ({ selectedOptions }) => {
      console.log('分类确认:', selectedOptions);
      if (selectedOptions && selectedOptions.length > 0) {
        selectedCategoryId.value = selectedOptions[0].value;
        selectedCategoryName.value = selectedOptions[0].text;
      }
      onSearch();
      showCategoryPicker.value = false;
      console.log('分类选择器关闭');
    };

    const onCategoryCancel = () => {
      console.log('分类取消');
      showCategoryPicker.value = false;
    };

    const onTypeConfirm = ({ selectedOptions }) => {
      console.log('类型确认:', selectedOptions);
      if (selectedOptions && selectedOptions.length > 0) {
        selectedType.value = selectedOptions[0].value;
        selectedTypeName.value = selectedOptions[0].text;
      }
      onSearch();
      showTypePicker.value = false;
      console.log('类型选择器关闭');
    };

    const onTypeCancel = () => {
      console.log('类型取消');
      showTypePicker.value = false;
    };

    // 监听选择器状态变化
    const onCategoryPickerUpdate = (value) => {
      console.log('分类选择器状态更新:', value);
      if (!value) {
        showCategoryPicker.value = false;
      }
    };

    const onTypePickerUpdate = (value) => {
      console.log('类型选择器状态更新:', value);
      if (!value) {
        showTypePicker.value = false;
      }
    };

    const onLoad = async () => {
      if (searchKeyword.value.trim()) {
        try {
          loading.value = true;
          const searchBody = {
            keyword: searchKeyword.value.trim() || null,
            categoryId: selectedCategoryId.value,
            type: selectedType.value
          };
          console.log(searchBody);
          const res = await proxy.$api.searchQ(searchBody);
          console.log(res.data.data)
          searchResults.value = res.data.data;
          console.log(searchResults.value)
          finished.value = true;
        } catch (error) {
          console.error('搜索失败:', error);
          proxy.$toast.fail('搜索失败');
        } finally {
          loading.value = false;
        }
      }
    };

    const onSearch = () => {
      searchResults.value = [];
      finished.value = false;
      onLoad();
    };

    const openPdf = (path) => {
      const url = `/upload-files/${path}`;
      window.open(url, '_blank');
    };

    // mounted
    onMounted(async () => {
      await loadCategories();
      await loadQuestionTypes();

      // 确保初始状态
      showCategoryPicker.value = false;
      showTypePicker.value = false;
      console.log('组件挂载完成，选择器状态:', {
        category: showCategoryPicker.value,
        type: showTypePicker.value
      });
    });

    return {
      searchKeyword,
      searchResults,
      loading,
      finished,
      categoryOptions,
      selectedCategoryId,
      selectedCategoryName,
      showCategoryPicker,
      typeOptions,
      selectedType,
      selectedTypeName,
      showTypePicker,
      categoryColumns,
      typeColumns,
      shownAnswers,
      toggleAnswer,
      goBack,
      handleCategoryClick,
      handleTypeClick,
      onCategoryConfirm,
      onCategoryCancel,
      onTypeConfirm,
      onTypeCancel,
      onSearch,
      onLoad,
      openPdf,
      onCategoryPickerUpdate,
      onTypePickerUpdate,
      forceClosePickers
    };
  }
};
</script>

<style scoped>
.search-container {
  padding: 16px;
}

.search-filters {
  margin-bottom: 16px;
}

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
  padding: 5px;
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