import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // 导入路径模块
import { fileURLToPath } from 'node:url'
const __dirname = path.dirname(fileURLToPath(import.meta.url))

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    // 可选：指定开发服务器端口（默认5173，避免端口冲突时修改）
    port: 5173,
    // 可选：启动时自动打开浏览器
    open: true,
    // 代理配置（解决跨域）
    proxy: {
      "/jsyl": {
        target: "http://localhost:8080",
        changeOrigin: true,
        ws: true,
      },
      "/admin": {
        target: "http://localhost:8080",
        changeOrigin: true,
        ws: true,
      },
    },
  },
  resolve: {
    // 配置路径别名
    alias: {
      // 核心配置：把 @ 映射为 src 目录
      '@': path.resolve(__dirname, 'src'), 
    }
  }
})
