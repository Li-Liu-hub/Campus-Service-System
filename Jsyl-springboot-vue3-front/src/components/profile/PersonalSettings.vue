<template>
  <div class="personal-settings">
    <el-card shadow="hover" class="theme-card">
      <template #header>
        <span class="title">主题设置</span>
      </template>
      
      <div class="setting-item">
        <div class="setting-label">
          <div class="label-title">深色模式</div>
          <div class="label-desc">开启后切换到深色主题</div>
        </div>
        <el-switch v-model="settings.darkMode" @change="handleDarkModeChange" />
      </div>
      
      <div class="setting-item">
        <div class="setting-label">
          <div class="label-title">消息通知</div>
          <div class="label-desc">接收系统推送通知</div>
        </div>
        <el-switch v-model="settings.notification" />
      </div>
    </el-card>
    
    <el-card shadow="hover" class="background-card">
      <template #header>
        <span class="title">背景设置</span>
      </template>
      
      <div class="background-preview">
        <div class="preview-box" :style="{ background: currentBackground }">
          <span class="preview-text">预览效果</span>
        </div>
      </div>
      
      <div class="background-options">
        <div class="option-title">选择背景</div>
        <div class="preset-list">
          <div
            v-for="bg in presetBackgrounds"
            :key="bg.id"
            class="preset-item"
            :class="{ active: selectedBackground === bg.value }"
            :style="{ background: bg.value }"
            @click="selectBackground(bg.value)"
          >
            <el-icon v-if="selectedBackground === bg.value"><Check /></el-icon>
          </div>
        </div>
        
        <div class="upload-section">
          <el-upload
            class="upload-box"
            :show-file-list="false"
            :before-upload="handleBeforeUpload"
            :http-request="handleUpload"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              上传自定义背景
            </el-button>
          </el-upload>
        </div>
      </div>
    </el-card>
    
    <el-card shadow="hover" class="privacy-card">
      <template #header>
        <span class="title">隐私设置</span>
      </template>
      
      <div class="setting-item">
        <div class="setting-label">
          <div class="label-title">资料可见性</div>
          <div class="label-desc">控制其他用户查看你的资料</div>
        </div>
        <el-select v-model="settings.profileVisibility" style="width: 150px">
          <el-option label="完全公开" value="public" />
          <el-option label="仅好友可见" value="friends" />
          <el-option label="完全私密" value="private" />
        </el-select>
      </div>
      
      <div class="setting-item">
        <div class="setting-label">
          <div class="label-title">互动权限</div>
          <div class="label-desc">谁可以评论和回复你的帖子</div>
        </div>
        <el-select v-model="settings.interactionPermission" style="width: 150px">
          <el-option label="所有人" value="all" />
          <el-option label="仅好友" value="friends" />
          <el-option label="关闭" value="off" />
        </el-select>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Check, Upload } from '@element-plus/icons-vue';

const settings = reactive({
  darkMode: false,
  notification: true,
  profileVisibility: 'public',
  interactionPermission: 'all',
});

const presetBackgrounds = [
  { id: 1, value: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { id: 2, value: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { id: 3, value: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { id: 4, value: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
  { id: 5, value: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
  { id: 6, value: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)' },
  { id: 7, value: 'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)' },
  { id: 8, value: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)' },
];

const selectedBackground = ref(presetBackgrounds[0].value);

const currentBackground = computed(() => selectedBackground.value);

const handleDarkModeChange = (value: boolean) => {
  if (value) {
    document.documentElement.classList.add('dark');
  } else {
    document.documentElement.classList.remove('dark');
  }
  localStorage.setItem('darkMode', String(value));
};

const selectBackground = (value: string) => {
  selectedBackground.value = value;
  localStorage.setItem('profileBackground', value);
  ElMessage.success('背景设置已保存');
};

const handleBeforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/');
  const isLt5M = file.size / 1024 / 1024 < 5;
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件');
    return false;
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过5MB');
    return false;
  }
  return true;
};

const handleUpload = (options: any) => {
  const { file } = options;
  const reader = new FileReader();
  reader.onload = (e) => {
    const result = e.target?.result as string;
    selectedBackground.value = `url(${result})`;
    localStorage.setItem('profileBackground', `url(${result})`);
    ElMessage.success('自定义背景已应用');
  };
  reader.readAsDataURL(file);
};

onMounted(() => {
  const darkMode = localStorage.getItem('darkMode');
  if (darkMode === 'true') {
    settings.darkMode = true;
    document.documentElement.classList.add('dark');
  }
  
  const savedBackground = localStorage.getItem('profileBackground');
  if (savedBackground) {
    selectedBackground.value = savedBackground;
  }
});
</script>

<style lang="scss" scoped>
.personal-settings {
  display: flex;
  flex-direction: column;
  gap: 20px;
  
  .title {
    font-size: 18px;
    font-weight: 600;
  }
  
  .setting-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid #ebeef5;
    
    &:last-child {
      border-bottom: none;
    }
    
    .setting-label {
      .label-title {
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 4px;
      }
      
      .label-desc {
        font-size: 12px;
        color: #909399;
      }
    }
  }
  
  .background-card {
    .background-preview {
      margin-bottom: 20px;
      
      .preview-box {
        height: 150px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        
        .preview-text {
          color: white;
          font-size: 18px;
          font-weight: 600;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
        }
      }
    }
    
    .background-options {
      .option-title {
        font-size: 14px;
        font-weight: 500;
        margin-bottom: 12px;
      }
      
      .preset-list {
        display: grid;
        grid-template-columns: repeat(8, 1fr);
        gap: 12px;
        margin-bottom: 20px;
        
        @media (max-width: 768px) {
          grid-template-columns: repeat(4, 1fr);
        }
        
        .preset-item {
          height: 50px;
          border-radius: 8px;
          cursor: pointer;
          transition: all 0.3s ease;
          border: 2px solid transparent;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          
          &:hover {
            transform: scale(1.05);
          }
          
          &.active {
            border-color: #409eff;
          }
        }
      }
      
      .upload-section {
        text-align: center;
      }
    }
  }
}
</style>
