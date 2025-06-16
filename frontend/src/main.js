import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@wangeditor/editor/dist/css/style.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import App from './App.vue'
import router from './router'
import store from './store'

const app = createApp(App)
const pinia = createPinia()

// 注册 wangEditor 组件
app.component('el-editor', Editor)
app.component('el-toolbar', Toolbar)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(store)
app.use(ElementPlus)
app.use(router)
app.mount('#app') 