<template>
  <div class="image-upload">
    <el-upload
      v-model:file-list="fileList"
      :action="uploadUrl"
      :headers="uploadHeaders"
      list-type="picture-card"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-remove="handleRemove"
      :limit="limit"
      :multiple="multiple"
      accept="image/*"
    >
      <el-icon><Plus /></el-icon>
    </el-upload>
    <div v-if="showTip" class="upload-tip">
      {{ tip }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import type { UploadProps, UploadUserFile } from 'element-plus';

interface Props {
  modelValue?: string[];
  limit?: number;
  maxSize?: number; // MB
  multiple?: boolean;
  showTip?: boolean;
  tip?: string;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => [],
  limit: 9,
  maxSize: 5,
  multiple: true,
  showTip: true,
  tip: '支持 JPG、PNG 格式，单张不超过 5MB，最多 9 张',
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: string[]): void;
  (e: 'change', urls: string[]): void;
}>();

const fileList = ref<UploadUserFile[]>([]);
const imageUrls = ref<string[]>([...props.modelValue]);

// 监听外部传入的值变化
watch(
  () => props.modelValue,
  (newVal) => {
    if (JSON.stringify(newVal) !== JSON.stringify(imageUrls.value)) {
      imageUrls.value = [...newVal];
      fileList.value = newVal.map((url, index) => ({
        name: `image-${index}`,
        url: url,
        uid: Date.now() + index,
      }));
    }
  },
  { immediate: true }
);

const uploadUrl = import.meta.env.VITE_API_BASE_URL + '/jsyl/common/upload/post/image';

const uploadHeaders = {
  authentication: localStorage.getItem('token') || '',
};

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

const handleSuccess = (response: any, file: UploadUserFile) => {
  if (response.code === 1) {
    const url = response.data.url;
    imageUrls.value.push(url);
    emit('update:modelValue', imageUrls.value);
    emit('change', imageUrls.value);
  } else {
    ElMessage.error(response.msg || '上传失败');
    // 移除失败的文件
    const index = fileList.value.findIndex((f) => f.uid === file.uid);
    if (index > -1) {
      fileList.value.splice(index, 1);
    }
  }
};

const handleError = () => {
  ElMessage.error('上传失败，请重试');
};

const handleRemove = (file: UploadUserFile) => {
  const index = fileList.value.findIndex((f) => f.uid === file.uid);
  if (index > -1) {
    imageUrls.value.splice(index, 1);
    emit('update:modelValue', imageUrls.value);
    emit('change', imageUrls.value);
  }
};
</script>

<style lang="scss" scoped>
.image-upload {
  .upload-tip {
    margin-top: var(--spacing-sm);
    font-size: var(--font-size-xs);
    color: var(--text-tertiary);
  }

  :deep(.el-upload-list__item) {
    transition: all var(--transition-fast);
  }

  :deep(.el-upload--picture-card) {
    width: 120px;
    height: 120px;
    line-height: 120px;
  }

  :deep(.el-upload-list__item) {
    width: 120px;
    height: 120px;
  }
}
</style>
