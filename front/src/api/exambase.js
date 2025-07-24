import axios from 'axios'

const api = axios.create({
  timeout: 60000
})

// 获取试卷分类列表
export function getCategoryList() {
  return api.get('/exambase/categories')
}

// 获取指定分类的试卷列表
export function getExamListByCategory(categoryId) {
  return api.get(`/exambase/exam/${categoryId}`)
}

// 获取指定分类的题目
export function getQuestionListByCategory(categoryId) {
  return api.get(`/exambase/questions/${categoryId}`)
}

// 上传试卷
export function uploadPaper(formData) {
  return api.post('/exambase/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function addCategory(category) {
  return api.post('/exambase/addCategory', category)
}

// 修复uploadQuestion方法
export function uploadQuestion(dto) {
  return api.post('/exambase/uploadQuestion', dto)
}