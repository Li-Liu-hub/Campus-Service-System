<template>
  <div class="avatar-upload">
    <el-upload
      class="avatar-uploader"
      :action="uploadUrl"
      :headers="uploadHeaders"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      accept="image/*"
    >
      <el-avatar
        v-if="avatarUrl"
        :src="avatarUrl"
        :size="size"
        class="avatar"
      />
      <div v-else class="avatar-placeholder" :style="{ width: size + 'px', height: size + 'px' }">
        <el-icon :size="size / 2">
          <Plus />
        </el-icon>
      </div>
    </el-upload>
    <div v-if="showTip" class="upload-tip">
      {{ tip }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import type { UploadProps } from 'element-plus';

interface Props {
  modelValue?: string;
  size?: number;
  maxSize?: number; // MB
  showTip?: boolean;
  tip?: string;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  size: 100,
  maxSize: 2,
  showTip: true,
  tip: '支持 JPG、PNG 格式，大小不超过 2MB',
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void;
  (e: 'success', url: string): void;
}>();

const avatarUrl = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
});

const uploadUrl = computed(() => {
  const baseURL = import.meta.env.VITE_API_BASE_URL || '';
  return `${baseURL}/jsyl/common/upload/avatar`;
});

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token');
  return {
    authentication: token || '',
  };
});

const beforeUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLtMaxSize = file.size / 1024 / 1024 < props.maxSize;

  if (!isImage) {
    ElMessage.error('只能上传图片文件');
    return false;
  }
  if (!isLtMaxSize) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB`);
    return false;
  }
  return true;
};

const handleSuccess = (response: any) => {
  if (response.code === 1) {
    avatarUrl.value = response.data.url;
    emit('success', response.data.url);
    ElMessage.success('上传成功');
  } else {
    ElMessage.error(response.msg || '上传失败');
  }
};

const handleError = () => {
  ElMessage.error('上传失败，请重试');
};
</script>

<style lang="scss" scoped>
.avatar-upload {
  display: inline-block;

  .avatar-uploader {
    :deep(.el-upload) {
      border: 1px dashed var(--border-color);
      border-radius: 50%;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: all var(--transition-fast);

      &:hover {
        border-color: var(--primary-color);
      }
    }
  }

  .avatar {
    display: block;
  }

  .avatar-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--bg-tertiary);
    color: var(--text-tertiary);
  }

  .upload-tip {
    margin-top: var(--spacing-sm);
    font-size: var(--font-size-xs);
    color: var(--text-tertiary);
    text-align: center;
  }
}
</style>
