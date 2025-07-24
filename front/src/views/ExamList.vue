<template>
  <div>
    <van-nav-bar 
      title="试卷列表" 
      left-text="返回"
      left-arrow
      fixed
      @click-left="goBack"
    />
    
    <div class="page-container" style="padding-top: 56px;">
      <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
        <van-list
          v-model:loading="loading"
          :finished="finished"
          finished-text="没有更多了"
        >
          <div v-if="exams.length > 0" class="exam-list">
            <van-cell 
              v-for="exam in exams" 
              :key="exam.id"
              :title="exam.name"
              :label="'文件路径: ' + exam.path"
            />
          </div>
          <div v-else class="empty-state">
            <p>暂无试卷，请先上传</p>
          </div>
        </van-list>
      </van-pull-refresh>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Toast } from 'vant'
import { getExamListByCategory } from '../api/exambase'

export default {
  name: 'ExamListPage',
  props: {
    id: {
      type: [String, Number],
      required: true
    }
  },
  setup(props) {
    const router = useRouter()
    const route = useRoute()
    const exams = ref([])
    const loading = ref(false)
    const finished = ref(true)
    const refreshing = ref(false)
    
    // 获取试卷列表
    const fetchExams = async () => {
      try {
        loading.value = true
        const categoryId = props.id || route.params.id
        const res = await getExamListByCategory(categoryId)
        exams.value = res.data.data || []
        loading.value = false
        finished.value = true
      } catch (error) {
        console.error('获取试卷列表失败:', error)
        Toast.fail('获取试卷列表失败')
        loading.value = false
      }
    }
    
    // 下拉刷新
    const onRefresh = () => {
      fetchExams().then(() => {
        refreshing.value = false
        Toast.success('刷新成功')
      })
    }
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    onMounted(() => {
      fetchExams()
    })
    
    return {
      exams,
      loading,
      finished,
      refreshing,
      onRefresh,
      goBack
    }
  }
}
</script>

<style scoped>
.exam-list {
  margin-top: 16px;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
  color: #969799;
}
</style>