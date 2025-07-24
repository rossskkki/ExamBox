<template>
  <div class="upload-question">
    <van-nav-bar 
      title="上传题目" 
      left-text="返回"
      left-arrow
      @click-left="goBack"
    />

    <el-form
      :model="questionForm"
      :rules="rules"
      ref="questionFormRef"
      label-width="120px"
      class="question-form"
    >
      <!-- 题目内容 -->
      <el-form-item label="题目内容" prop="content">
        <el-input
          v-model="questionForm.content"
          type="textarea"
          :rows="4"
          placeholder="请输入题目内容"
        />
      </el-form-item>

      <!-- 题目分类 -->
      <el-form-item label="题目分类" prop="categoryId" class="category">
        <div class="category-selector">
          <el-select v-model="questionForm.categoryId" placeholder="请选择分类" class="category-select">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
          <el-button type="primary" @click="showAddCategoryDialog">
            <el-icon><CirclePlus /></el-icon>
          </el-button>
        </div>
      </el-form-item>

      <!-- 题目类型 -->
      <el-form-item label="题目类型" prop="type">
        <el-select v-model="questionForm.type" placeholder="请选择题目类型">
          <el-option label="单选题" value="CHOICE" />
          <el-option label="判断题" value="TRUE_FALSE" />
          <el-option label="填空题" value="FILL_IN" />
          <el-option label="简答题" value="ESSAY" />
        </el-select>
      </el-form-item>

      <!-- 选项列表 (仅选择题显示) -->
      <el-form-item
        v-if="questionForm.type === 'CHOICE'"
        label="选项列表"
        prop="options"
      >
        <div class="options-container">
          <div
            v-for="(option, index) in questionForm.options"
            :key="index"
            class="option-item"
          >
            <el-input
              v-model="questionForm.options[index]"
              :placeholder="`选项 ${String.fromCharCode(65 + index)}`"
              class="option-input"
            />
            <el-button
              type="danger"
              size="small"
              @click="removeOption(index)"
              :disabled="questionForm.options.length <= 2"
            >
              删除
            </el-button>
          </div>
          <el-button type="primary" size="small" @click="addOption">
            添加选项
          </el-button>
        </div>
      </el-form-item>

      <!-- 答案 -->
      <el-form-item label="答案" prop="answer">
        <!-- 单选题答案 -->
        <el-select
          v-if="questionForm.type === 'CHOICE'"
          v-model="questionForm.answer"
          placeholder="请选择正确答案"
        >
          <el-option
            v-for="(option, index) in questionForm.options"
            :key="index"
            :label="`${String.fromCharCode(65 + index)}. ${option}`"
            :value="String.fromCharCode(65 + index)"
          />
        </el-select>

        <!-- 判断题答案 -->
        <el-radio-group v-else-if="questionForm.type === 'TRUE_FALSE'" v-model="questionForm.answer">
          <el-radio label="正确">正确</el-radio>
          <el-radio label="错误">错误</el-radio>
        </el-radio-group>

        <!-- 其他题型答案 -->
        <el-input
          v-else
          v-model="questionForm.answer"
          type="textarea"
          :rows="3"
          placeholder="请输入答案"
        />
      </el-form-item>

      <!-- 提交按钮 -->
      <el-form-item>
        <el-button type="primary" @click="submitForm" :loading="loading">
          上传题目
        </el-button>
        <el-button @click="resetForm">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 添加分类对话框 -->
    <el-dialog
      v-model="addCategoryDialogVisible"
      title="添加分类"
      width="400px"
      :before-close="handleCloseAddDialog"
    >
      <el-form
        :model="categoryForm"
        :rules="categoryRules"
        ref="categoryFormRef"
        label-width="80px"
      >
        <el-form-item label="分类名称" prop="name">
          <el-input
            v-model="categoryForm.name"
            placeholder="请输入分类名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCloseAddDialog">取消</el-button>
          <el-button type="primary" @click="submitAddCategory" :loading="addCategoryLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
  <!-- <van-button type="primary" size="small" block @click="goToUpload">上传试卷</van-button> -->
  <el-button id="upload-btn" type="success" @click="goToUpload" size="large" round>上传试卷</el-button>
</template>

<script>
import { getCategoryList, uploadQuestion, addCategory } from '@/api/exambase'
import { ElMessage } from 'element-plus'
import { CirclePlus } from '@element-plus/icons-vue'

export default {
  name: 'UploadQuestion',
  components: {
    // ArrowLeft,
    CirclePlus
  },
  data() {
    return {
      loading: false,
      addCategoryLoading: false,
      addCategoryDialogVisible: false,
      categories: [],
      multipleAnswers: [],
      questionForm: {
        content: '',
        categoryId: null,
        options: ['', ''],
        type: '',
        answer: ''
      },
      categoryForm: {
        name: ''
      },
      rules: {
        content: [
          { required: true, message: '请输入题目内容', trigger: 'blur' }
        ],
        categoryId: [
          { required: true, message: '请选择题目分类', trigger: 'change' }
        ],
        type: [
          { required: true, message: '请选择题目类型', trigger: 'change' }
        ],
        options: [
          {
            validator: (rule, value, callback) => {
              if (this.questionForm.type === 'CHOICE') {
                if (!value || value.length < 2) {
                  callback(new Error('至少需要2个选项'))
                } else if (value.some(option => !option.trim())) {
                  callback(new Error('选项内容不能为空'))
                } else {
                  callback()
                }
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ],
        answer: [
          { required: true, message: '请输入或选择答案', trigger: 'blur' }
        ]
      },
      categoryRules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' },
          { min: 1, max: 50, message: '分类名称长度在 1 到 50 个字符', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    'questionForm.type'(newType) {
      if (newType === 'CHOICE') {
        if (this.questionForm.options.length === 0) {
          this.questionForm.options = ['', '']
        }
      } else {
        this.questionForm.options = []
      }
      this.questionForm.answer = ''
      this.multipleAnswers = []
    }
  },
  created() {
    this.loadCategories()
  },
  methods: {
    goToUpload() {
      this.$router.push('/upload');
    },

    async loadCategories() {
      try {
        const response = await getCategoryList()
        this.categories = response.data.data
      } catch (error) {
        ElMessage.error('加载分类列表失败')
        console.error(error)
      }
    },

    showAddCategoryDialog() {
      this.addCategoryDialogVisible = true
      this.categoryForm.name = ''
    },

    handleCloseAddDialog() {
      this.addCategoryDialogVisible = false
      this.$refs.categoryFormRef?.resetFields()
    },

    async submitAddCategory() {
      try {
        const valid = await this.$refs.categoryFormRef.validate()
        if (!valid) return

        this.addCategoryLoading = true

        await addCategory(this.categoryForm)

        ElMessage.success('分类添加成功')
        this.handleCloseAddDialog()

        // 重新加载分类列表
        await this.loadCategories()

        // 自动选择新添加的分类
        const newCategory = this.categories.find(cat => cat.name === this.categoryForm.name)
        if (newCategory) {
          this.questionForm.categoryId = newCategory.id
        }

      } catch (error) {
        ElMessage.error('分类添加失败')
        console.error(error)
      } finally {
        this.addCategoryLoading = false
      }
    },

    addOption() {
      this.questionForm.options.push('')
    },

    removeOption(index) {
      if (this.questionForm.options.length > 2) {
        this.questionForm.options.splice(index, 1)
      }
    },

    async submitForm() {
      try {
        const valid = await this.$refs.questionFormRef.validate()
        if (!valid) return

        this.loading = true

        const submitData = {
          ...this.questionForm,
          options: this.questionForm.type === 'CHOICE'
            ? this.questionForm.options.filter(option => option.trim())
            : []
        }

        await uploadQuestion(submitData)
        ElMessage.success('题目上传成功')
        this.resetForm()
      } catch (error) {
        ElMessage.error('题目上传失败')
        console.error(error)
      } finally {
        this.loading = false
      }
    },

    resetForm() {
      this.$refs.questionFormRef.resetFields()
      this.questionForm = {
        content: '',
        categoryId: null,
        options: ['', ''],
        type: '',
        answer: ''
      }
      this.multipleAnswers = []
    },

    goBack() {
      // 使用路由返回上一页
      if (this.$route.query.from) {
        this.$router.push(this.$route.query.from)
      } else {
        this.$router.go(-1)
      }
    }
  }
}
</script>

<style scoped>
.upload-question {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  position: relative;
}

.back-button {
  margin-right: 20px;
}

.question-form {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-top: 50px;
}

.category-selector {
  display: flex;
  gap: 10px;
  align-items: center;
}

.category-select {
  flex: 1;
}

.options-container {
  width: 100%;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.option-input {
  flex: 1;
  margin-right: 10px;
}

h2 {
  flex: 1;
  text-align: center;
  margin: 0;
  color: #333;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 使用 :deep() 深度选择器来覆盖 Element Plus 的样式 */
:deep(.el-form-item__content) {
  display: block !important;
}

/* 或者更具体的选择器 */
.question-form :deep(.el-form-item__content) {
  display: block !important;
}

/* 如果只想对特定的表单项生效，可以添加类名 */
.category :deep(.el-form-item__content) {
  display: block !important;
}

#upload-btn{
  margin-left:60%
}
</style>
