import { createRouter, createWebHistory } from 'vue-router'
// import Home from '../views/Home.vue';
import ExamBank from '../views/ExamBank.vue';
import QuestionBank from '@/views/QuestionBank.vue';

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue')
  },
  {
    path: '/examSearch',
    name: 'ExamSearch',
    component: () => import('@/views/ExamSearch.vue')
  },
  {
    path: '/searchQ',
    name: 'QuestionSearch',
    component: () => import('@/views/QuestionSearch.vue')
  },
  {
    path: '/exam-bank',
    name: 'ExamBank',
    component: ExamBank
  },
  {
    path: '/question-bank',
    name: 'QuestionBank',
    component: QuestionBank
  },
  {
    path: '/category/:id',
    name: 'ExamList',
    component: () => import('../views/ExamList.vue'),
    props: true
  },
  {
    path: '/upload',
    name: 'Upload',
    component: () => import('../views/Upload.vue')
  },
  {
    path: '/uploadQ',
    name: 'UploadQ',
    component: () => import('../views/UploadQ.vue')
  },
  {
    path: '/add-category',
    name: 'AddCategory',
    component: () => import('../views/AddCategory.vue')
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router