// src/main.js
import { createApp } from 'vue'
import App from './App.vue'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import router from './router'
import { Button, NavBar, Cell, CellGroup, Form, Field, Uploader, Toast, Dialog, List, PullRefresh, Icon, Tabbar, TabbarItem, Sidebar, SidebarItem, Search, Empty, Picker, Popup } from 'vant'
import 'vant/lib/index.css'
import * as api from './api/exam' // 导入 api

const app = createApp(App)

// 注册Vant组件
app.use(Button)
app.use(NavBar)
app.use(Cell)
app.use(CellGroup)
app.use(Form)
app.use(Field)
app.use(Uploader)
app.use(Toast)
app.use(Dialog)
app.use(List)
app.use(PullRefresh)
app.use(Icon)
app.use(Tabbar)
app.use(TabbarItem)
app.use(Sidebar)
app.use(SidebarItem)
app.use(Search)
app.use(Empty)
app.use(router)
app.use(Picker)
app.use(Popup)
app.config.globalProperties.$api = api; // 全局挂载 api
app.use(ElementPlus)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.mount('#app')