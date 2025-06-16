<template>
  <div class="p-4 bg-white rounded shadow">
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center">
        <!-- <h2 class="text-xl font-bold">技术支持</h2> -->
        <el-button
          type="primary"
          class="ml-2"
          @click="isEditing = !isEditing"
        >
          {{ isEditing ? '取消' : '编辑' }}
        </el-button>
        <el-button
          v-if="isEditing"
          type="success"
          class="ml-2"
          @click="saveContent"
        >
          保存
        </el-button>
      </div>
    </div>

    <!-- 内容展示/编辑区域 -->
    <div v-if="!isEditing" v-html="content" class="prose max-w-none p-4 bg-gray-50 rounded"></div>
    <div v-else class="min-h-[500px]">
      <QuillEditor
        v-model:content="editingContent"
        :options="editorOptions"
        contentType="html"
        class="h-[500px]"
        toolbar="full"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as supportApi from '@/api/support'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import { uploadFile } from '@/api/upload'
import Quill from 'quill'
import ImageResize from 'quill-image-resize-vue'

// 注册图片调整模块
Quill.register('modules/imageResize', ImageResize)

const isEditing = ref(false)
const content = ref('技术支持内容')
const editingContent = ref(content.value)
const loading = ref(false)
const supportId = ref(null)

// 添加基础URL配置
const BASE_URL = 'http://localhost:8090' // 根据您的实际后端地址修改

// 处理图片URL
const processImageUrl = (url) => {
  if (!url) return ''
  // 如果URL已经是完整的，直接返回
  if (url.startsWith('http')) return url
  // 确保URL以/api开头
  if (!url.startsWith('/api')) {
    url = `/api${url}`
  }
  return url
}

// 修改 Quill 编辑器的配置
const editorOptions = {
  theme: 'snow',
  modules: {
    toolbar: {
      container: [
        ['bold', 'italic', 'underline', 'strike'],
        ['blockquote', 'code-block'],
        [{ 'header': 1 }, { 'header': 2 }],
        [{ 'list': 'ordered' }, { 'list': 'bullet' }],
        [{ 'script': 'sub' }, { 'script': 'super' }],
        [{ 'indent': '-1' }, { 'indent': '+1' }],
        [{ 'direction': 'rtl' }],
        [{ 'size': ['small', false, 'large', 'huge'] }],
        [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
        [{ 'color': [] }, { 'background': [] }],
        [{ 'font': [] }],
        [{ 'align': [] }],
        ['clean'],
        ['link', 'image']
      ],
      handlers: {
        image: function() {
          const input = document.createElement('input')
          input.setAttribute('type', 'file')
          input.setAttribute('accept', 'image/*')
          input.click()

          input.onchange = async () => {
            const file = input.files[0]
            if (file) {
              try {
                console.log('准备上传文件:', file)
                const response = await uploadFile(file)
                console.log('上传响应:', response)
                
                if (response.code === 0 && response.data && response.data.code === 0) {
                  const quill = this.quill
                  const range = quill.getSelection(true)
                  // 处理图片URL，使用相对路径
                  const imageUrl = processImageUrl(response.data.data)
                  console.log('处理后的图片URL:', imageUrl)
                  
                  // 插入图片
                  quill.insertEmbed(range.index, 'image', imageUrl)
                } else {
                  console.error('上传响应格式错误:', response)
                  throw new Error('上传失败')
                }
              } catch (error) {
                console.error('图片上传失败:', error)
                ElMessage.error('图片上传失败')
              }
            }
          }
        }
      }
    },
    imageResize: {
      displaySize: true,
      modules: ['Resize', 'DisplaySize', 'Toolbar']
    }
  }
}

// 添加处理 base64 图片的函数
const convertBase64ToFile = (base64String) => {
  const arr = base64String.split(',')
  const mime = arr[0].match(/:(.*?);/)[1]
  const bstr = atob(arr[1])
  let n = bstr.length
  const u8arr = new Uint8Array(n)
  while (n--) {
    u8arr[n] = bstr.charCodeAt(n)
  }
  return new File([u8arr], `image-${Date.now()}.${mime.split('/')[1]}`, { type: mime })
}

// 处理编辑器内容中的图片
const processContentImages = async (content) => {
  const tempDiv = document.createElement('div')
  tempDiv.innerHTML = content
  const images = tempDiv.getElementsByTagName('img')
  
  for (let img of images) {
    const src = img.getAttribute('src')
    if (src.startsWith('data:image')) {
      try {
        const file = convertBase64ToFile(src)
        console.log('准备上传文件:', file)
        const response = await uploadFile(file)
        console.log('上传响应:', response)
        
        if (response.code === 0 && response.data && response.data.code === 0) {
          // 处理图片URL，使用相对路径
          const imageUrl = processImageUrl(response.data.data)
          console.log('处理后的图片URL:', imageUrl)
          img.setAttribute('src', imageUrl)
        } else {
          console.error('上传响应格式错误:', response)
          throw new Error('上传失败')
        }
      } catch (error) {
        console.error('图片上传失败:', error)
        ElMessage.error('图片上传失败')
      }
    }
  }
  return tempDiv.innerHTML
}

// 添加图片调整功能
const initImageResize = () => {
  const editor = document.querySelector('.ql-editor')
  if (!editor) return

  editor.addEventListener('mouseover', (e) => {
    const img = e.target.closest('img')
    if (img) {
      img.style.cursor = 'move'
      img.setAttribute('contenteditable', 'true')
      img.setAttribute('draggable', 'true')
      
      // 添加拖拽事件
      img.addEventListener('mousedown', (e) => {
        if (e.target === img) {
          const startX = e.clientX
          const startY = e.clientY
          const startWidth = img.offsetWidth
          const startHeight = img.offsetHeight
          
          const handleMouseMove = (e) => {
            const deltaX = e.clientX - startX
            const deltaY = e.clientY - startY
            img.style.width = `${startWidth + deltaX}px`
            img.style.height = `${startHeight + deltaY}px`
          }
          
          const handleMouseUp = () => {
            document.removeEventListener('mousemove', handleMouseMove)
            document.removeEventListener('mouseup', handleMouseUp)
          }
          
          document.addEventListener('mousemove', handleMouseMove)
          document.addEventListener('mouseup', handleMouseUp)
        }
      })
    }
  })
}

onMounted(async () => {
  await fetchContent()
  // 初始化图片拖拽功能
  initImageResize()
})

const fetchContent = async () => {
  loading.value = true
  try {
    // 假设只取第一条技术支持内容
    const res = await supportApi.fetchSupportList()
    if (res.data && res.data.length > 0) {
      // 处理内容中的图片URL
      const processedContent = res.data[0].content.replace(
        /<img src="([^"]+)"/g,
        (match, src) => {
          const processedUrl = processImageUrl(src)
          console.log('处理图片URL:', src, '->', processedUrl)
          return `<img src="${processedUrl}"`
        }
      )
      console.log('处理后的内容:', processedContent)
      content.value = processedContent
      editingContent.value = processedContent
      supportId.value = res.data[0].id
    }
  } catch (error) {
    ElMessage.error('获取内容失败')
  } finally {
    loading.value = false
  }
}

const saveContent = async () => {
  loading.value = true
  try {
    // 处理内容中的图片
    const processedContent = await processContentImages(editingContent.value)
    console.log('保存的内容:', processedContent)
    
    if (supportId.value) {
      await supportApi.updateSupport({ 
        id: supportId.value, 
        content: processedContent 
      })
    } else {
      await supportApi.addSupport({ 
        content: processedContent, 
        title: '技术支持' 
      })
    }
    content.value = processedContent
    isEditing.value = false
    ElMessage.success('保存成功')
    await fetchContent()
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
:deep(.ql-container) {
  height: calc(100% - 42px);
  font-size: 16px;
}

:deep(.ql-editor) {
  min-height: 300px;
  height: 100%;
}

:deep(.ql-toolbar) {
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
}

:deep(.ql-container) {
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
}

/* 图片样式 */
:deep(.ql-editor img) {
  max-width: 100%;
  height: auto;
  max-height: 500px;
  display: block;
  margin: 10px auto;
}

/* 图片调整工具栏样式 */
:deep(.image-resizer) {
  border: 1px solid #409EFF;
  background-color: rgba(64, 158, 255, 0.1);
}

:deep(.image-resizer .resize-handle) {
  background-color: #409EFF;
  border: 1px solid #fff;
}

:deep(.image-resizer .toolbar) {
  background-color: #fff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>
