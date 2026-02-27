<template>
  <div class="help-feedback">
    <el-card shadow="hover" class="faq-card">
      <template #header>
        <span class="title">常见问题</span>
      </template>
      
      <el-collapse v-model="activeFaq">
        <el-collapse-item title="如何发布订单？" name="1">
          <div>在首页点击"发布订单"按钮，填写订单详情后即可发布。</div>
        </el-collapse-item>
        <el-collapse-item title="如何接取订单？" name="2">
          <div>在订单列表中点击感兴趣的订单，查看详情后点击"接单"按钮即可接取。</div>
        </el-collapse-item>
        <el-collapse-item title="如何充值/提现？" name="3">
          <div>进入个人中心 → 我的钱包，可以进行充值和提现操作。</div>
        </el-collapse-item>
        <el-collapse-item title="订单完成后如何确认？" name="4">
          <div>订单完成后，发布者和接单者都可以点击"完成订单"按钮确认。</div>
        </el-collapse-item>
        <el-collapse-item title="如何修改个人信息？" name="5">
          <div>进入个人中心 → 个人资料，点击编辑按钮即可修改。</div>
        </el-collapse-item>
      </el-collapse>
    </el-card>
    
    <el-card shadow="hover" class="feedback-card">
      <template #header>
        <span class="title">意见反馈</span>
      </template>
      
      <el-form ref="formRef" :model="feedbackForm" :rules="rules" label-width="80px">
        <el-form-item label="反馈类型" prop="type">
          <el-radio-group v-model="feedbackForm.type">
            <el-radio label="bug">功能问题</el-radio>
            <el-radio label="suggest">建议反馈</el-radio>
            <el-radio label="complaint">投诉举报</el-radio>
            <el-radio label="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="反馈内容" prop="content">
          <el-input
            v-model="feedbackForm.content"
            type="textarea"
            :rows="6"
            placeholder="请详细描述您遇到的问题或建议..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="feedbackForm.contact" placeholder="便于我们回复您（选填）" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">提交反馈</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-card shadow="hover" class="contact-card">
      <template #header>
        <span class="title">联系我们</span>
      </template>
      
      <div class="contact-list">
        <div class="contact-item">
          <div class="contact-icon">
            <el-icon><Message /></el-icon>
          </div>
          <div class="contact-info">
            <div class="contact-title">官方邮箱</div>
            <div class="contact-text">support@campus-service.com</div>
          </div>
        </div>
        
        <div class="contact-item">
          <div class="contact-icon">
            <el-icon><Phone /></el-icon>
          </div>
          <div class="contact-info">
            <div class="contact-title">客服热线</div>
            <div class="contact-text">400-123-4567 (工作日 9:00-18:00)</div>
          </div>
        </div>
        
        <div class="contact-item">
          <div class="contact-icon">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="contact-info">
            <div class="contact-title">在线客服</div>
            <div class="contact-text">点击右侧在线按钮咨询</div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage, FormInstance } from 'element-plus';
import { Message, Phone, ChatDotRound } from '@element-plus/icons-vue';

const activeFaq = ref(['1']);
const formRef = ref<FormInstance>();
const submitting = ref(false);

const feedbackForm = reactive({
  type: 'suggest',
  content: '',
  contact: '',
});

const rules = {
  type: [{ required: true, message: '请选择反馈类型', trigger: 'change' }],
  content: [
    { required: true, message: '请输入反馈内容', trigger: 'blur' },
    { min: 10, message: '内容至少10个字符', trigger: 'blur' },
  ],
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    submitting.value = true;
    
    setTimeout(() => {
      ElMessage.success('反馈提交成功，感谢您的建议！');
      handleReset();
      submitting.value = false;
    }, 1000);
  } catch (error: any) {
    if (error.name !== 'Error') {
      ElMessage.error('提交失败，请重试');
    }
    submitting.value = false;
  }
};

const handleReset = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  feedbackForm.contact = '';
};
</script>

<style lang="scss" scoped>
.help-feedback {
  display: flex;
  flex-direction: column;
  gap: 20px;
  
  .title {
    font-size: 18px;
    font-weight: 600;
  }
  
  .faq-card {
    :deep(.el-collapse-item__header) {
      font-weight: 500;
    }
    
    :deep(.el-collapse-item__content) {
      color: #606266;
      line-height: 2;
    }
  }
  
  .feedback-card {
    .el-form {
      padding: 20px 0;
    }
  }
  
  .contact-card {
    .contact-list {
      .contact-item {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 16px 0;
        border-bottom: 1px solid #ebeef5;
        
        &:last-child {
          border-bottom: none;
        }
      }
      
      .contact-icon {
        width: 50px;
        height: 50px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
      }
      
      .contact-info {
        .contact-title {
          font-size: 14px;
          font-weight: 500;
          margin-bottom: 4px;
        }
        
        .contact-text {
          font-size: 12px;
          color: #909399;
        }
      }
    }
  }
}
</style>
