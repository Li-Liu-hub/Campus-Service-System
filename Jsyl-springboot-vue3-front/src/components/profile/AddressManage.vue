<template>
  <div class="address-manage">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="title">收货地址</span>
          <el-button type="primary" @click="showAddDialog">
            <el-icon><Plus /></el-icon>
            新增地址
          </el-button>
        </div>
      </template>
      
      <div class="address-list">
        <div
          v-for="address in addresses"
          :key="address.id"
          class="address-item"
        >
          <div class="address-content">
            <div class="address-header">
              <span class="contact-name">{{ address.contactName }}</span>
              <span class="contact-phone">{{ address.contactPhone }}</span>
              <el-tag v-if="address.isDefault === 1" type="success" size="small">默认</el-tag>
            </div>
            <div class="address-detail">{{ address.addressDetail }}</div>
          </div>
          
          <div class="address-actions">
            <el-button type="primary" link @click="handleEdit(address)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(address)">删除</el-button>
            <el-button v-if="address.isDefault !== 1" type="warning" link @click="handleSetDefault(address)">设为默认</el-button>
          </div>
        </div>
        
        <el-empty v-if="addresses.length === 0" description="暂无收货地址" />
      </div>
    </el-card>
    
    <el-dialog
      v-model="dialogVisible"
      :title="isEditing ? '编辑地址' : '新增地址'"
      width="500px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="收货人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入收货人姓名" maxlength="20" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="11" />
        </el-form-item>
        <el-form-item label="详细地址" prop="addressDetail">
          <el-input v-model="form.addressDetail" type="textarea" :rows="3" placeholder="请输入详细地址" maxlength="200" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { getUserAddressList, addUserAddress, updateUserAddress, deleteUserAddress, setDefaultUserAddress, type UserAddress } from '@/api/user-address';

interface Emits {
  (e: 'refresh'): void;
}

const emit = defineEmits<Emits>();

const addresses = ref<UserAddress[]>([]);
const dialogVisible = ref(false);
const isEditing = ref(false);
const saving = ref(false);
const formRef = ref<FormInstance>();

const form = reactive({
  id: null as number | null,
  contactName: '',
  contactPhone: '',
  addressDetail: '',
  isDefault: false,
});

const rules = {
  contactName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在2-20个字符', trigger: 'blur' },
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  addressDetail: [
    { required: true, message: '请输入详细地址', trigger: 'blur' },
  ],
};

const loadAddresses = async () => {
  try {
    const res = await getUserAddressList();
    if (res?.code === 200 && res.data) {
      addresses.value = res.data;
    }
  } catch (error) {
    console.error('获取地址列表失败:', error);
  }
};

const showAddDialog = () => {
  isEditing.value = false;
  Object.assign(form, {
    id: null,
    contactName: '',
    contactPhone: '',
    addressDetail: '',
    isDefault: false,
  });
  dialogVisible.value = true;
};

const handleEdit = (address: UserAddress) => {
  isEditing.value = true;
  Object.assign(form, {
    id: address.id,
    contactName: address.contactName,
    contactPhone: address.contactPhone,
    addressDetail: address.addressDetail,
    isDefault: address.isDefault === 1,
  });
  dialogVisible.value = true;
};

const handleSave = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    saving.value = true;
    
    const data = {
      contactName: form.contactName,
      contactPhone: form.contactPhone,
      addressDetail: form.addressDetail,
      isDefault: form.isDefault ? 1 : 0,
    };
    
    let res;
    if (isEditing.value && form.id) {
      res = await updateUserAddress(form.id, data);
    } else {
      res = await addUserAddress(data);
    }
    
    if (res?.code === 200) {
      ElMessage.success('保存成功');
      dialogVisible.value = false;
      loadAddresses();
      emit('refresh');
    } else {
      ElMessage.error(res?.msg || '保存失败');
    }
  } catch (error: any) {
    if (error.name !== 'Error') {
      ElMessage.error('保存失败，请重试');
    }
  } finally {
    saving.value = false;
  }
};

const handleDelete = async (address: UserAddress) => {
  try {
    await ElMessageBox.confirm('确定要删除该地址吗？', '提示', { type: 'warning' });
    
    const res = await deleteUserAddress(address.id!);
    if (res?.code === 200) {
      ElMessage.success('删除成功');
      loadAddresses();
      emit('refresh');
    } else {
      ElMessage.error(res?.msg || '删除失败');
    }
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
};

const handleSetDefault = async (address: UserAddress) => {
  try {
    const res = await setDefaultUserAddress(address.id!);
    if (res?.code === 200) {
      ElMessage.success('设置成功');
      loadAddresses();
    } else {
      ElMessage.error(res?.msg || '设置失败');
    }
  } catch (error) {
    ElMessage.error('设置失败');
  }
};

onMounted(() => {
  loadAddresses();
});
</script>

<style lang="scss" scoped>
.address-manage {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .title {
      font-size: 18px;
      font-weight: 600;
    }
  }
  
  .address-list {
    .address-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px;
      border: 1px solid #ebeef5;
      border-radius: 8px;
      margin-bottom: 12px;
      transition: all 0.3s ease;
      
      &:hover {
        border-color: #409eff;
        box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
      }
    }
    
    .address-content {
      flex: 1;
      
      .address-header {
        margin-bottom: 8px;
        
        .contact-name {
          font-weight: 600;
          margin-right: 12px;
        }
        
        .contact-phone {
          color: #909399;
          margin-right: 12px;
        }
      }
      
      .address-detail {
        color: #606266;
        font-size: 14px;
      }
    }
    
    .address-actions {
      display: flex;
      gap: 8px;
    }
  }
}
</style>
