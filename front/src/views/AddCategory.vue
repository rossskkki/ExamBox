<template>
  <div>
    <van-nav-bar 
      title="添加分类" 
      left-text="返回"
      left-arrow
      fixed
      @click-left="goBack"
    />
    
    <div class="page-container" style="padding-top: 56px;">
      <van-form @submit="onSubmit">
        <van-cell-group inset>
          <van-field
            v-model="formData.name"
            name="name"
            label="分类名称"
            placeholder="请输入分类名称"
            :rules="[{ required: true, message: '请输入分类名称' }]"
          />
        </van-cell-group>
        
        <div style="margin: 16px;">
          <van-button round block type="primary" native-type="submit" :disabled="submitting">
            {{ submitting ? '提交中...' : '提交' }}
          </van-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Toast } from 'vant'
import { addCategory } from '../api/exambase'

export default {
  name: 'AddCategoryPage',
  setup() {
    const router = useRouter()
    const submitting = ref(false)
    
    const formData = reactive({
      name: ''
    })
    
    // 提交表单
    const onSubmit = async () => {
      try {
        submitting.value = true
        
        // 调用添加分类接口
        const res = await addCategory(formData)
        
        if (res.data.code === 200) {
          Toast.success('添加成功')
          router.push('/')
        } else {
          Toast.fail(res.data.msg || '添加失败')
        }
      } catch (error) {
        console.error('添加分类失败:', error)
        Toast.fail('添加分类失败')
      } finally {
        submitting.value = false
      }
    }
    
    // 返回上一页
    const goBack = () => {
      router.back()
    }
    
    return {
      formData,
      submitting,
      onSubmit,
      goBack
    }
  }
}
</script>