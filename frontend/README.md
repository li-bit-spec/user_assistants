# user-helps-frontend

## 目录结构

```
frontend/
  src/
    api/         # API 请求相关（当前为空）
    layout/      # 布局组件（index.vue）
    router/      # 路由配置（当前为空）
    store/       # 状态管理（Pinia，含modules子目录，index.js为空）
      modules/   # 拓展模块（当前为空）
    utils/       # 工具函数（如request.js）
    views/       # 页面视图（当前为空）
    main.js      # 项目入口文件
```

## 技术架构

- **构建工具**：Vite
- **主框架**：Vue 3
- **UI 框架**：Element Plus
- **状态管理**：Pinia
- **路由管理**：Vue Router
- **HTTP 请求**：Axios
- **代码规范**：ESLint + Prettier
- **样式**：Sass

### 主要依赖
- `vue@^3.3.4`
- `vue-router@^4.2.4`
- `element-plus@^2.3.8`
- `@element-plus/icons-vue@^2.1.0`
- `pinia@^2.1.6`
- `axios@^1.4.0`

### 入口说明
- `main.js`：初始化 Vue 应用，注册 Element Plus、Pinia、路由和 Element Plus 图标。
- `utils/request.js`：基于 Axios 的请求封装，包含请求/响应拦截、全局错误处理。

### 目录说明
- `api/`：用于存放所有后端 API 请求方法。
- `layout/`：用于存放全局布局组件。
- `router/`：用于存放路由配置。
- `store/`：用于存放 Pinia 状态管理相关内容。
- `utils/`：用于存放工具函数，如请求封装等。
- `views/`：用于存放各页面视图组件。

---

如需启动开发环境：
```bash
npm install
npm run dev
```

如需打包构建：
```bash
npm run build
``` 