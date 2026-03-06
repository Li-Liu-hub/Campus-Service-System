<template>
  <div class="seckill-page">
    <div class="page-header">
      <div class="header-title">
        <h2>秒杀商品管理</h2>
        <p class="header-desc">管理秒杀活动商品</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          添加秒杀商品
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon :size="28"><ShoppingCart /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.totalGoods || 0 }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon active">
              <el-icon :size="28"><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.activeGoods || 0 }}</div>
              <div class="stat-label">活动中</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon stock">
              <el-icon :size="28"><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.totalStock || 0 }}</div>
              <div class="stat-label">总库存</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon sold">
              <el-icon :size="28"><Sell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ overview.totalSold || 0 }}</div>
              <div class="stat-label">已售出</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="商品标题">
          <el-input v-model="queryForm.title" placeholder="请输入商品标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 商品列表 -->
    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column label="商品信息" min-width="200">
          <template #default="{ row }">
            <div class="goods-info">
              <el-image
                v-if="row.imageUrl"
                :src="row.imageUrl"
                fit="cover"
                class="goods-image"
              />
              <div v-else class="goods-image-placeholder">
                <el-icon><Picture /></el-icon>
              </div>
              <div class="goods-detail">
                <div class="goods-title">{{ row.title }}</div>
                <div class="goods-desc">{{ row.description }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="价格" width="150" align="center">
          <template #default="{ row }">
            <div class="price-info">
              <div class="original-price">¥{{ row.originalPrice }}</div>
              <div class="seckill-price">秒杀价: ¥{{ row.seckillPrice }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="100" align="center">
          <template #default="{ row }">
            <span :class="{ 'low-stock': row.stock < 10 }">{{ row.stock }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="soldCount" label="已售" width="80" align="center" />
        <el-table-column label="秒杀时间" width="180">
          <template #default="{ row }">
            <div class="time-info">
              <div>开始: {{ formatTime(row.startTime) }}</div>
              <div>结束: {{ formatTime(row.endTime) }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="queryForm.page"
          v-model:page-size="queryForm.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑秒杀商品' : '添加秒杀商品'"
      width="600px"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="商品标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入商品标题" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="商品图片" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原价" prop="originalPrice">
              <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="秒杀价" prop="seckillPrice">
              <el-input-number v-model="form.seckillPrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="限购数量" prop="limitPerUser">
              <el-input-number v-model="form.limitPerUser" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="form.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="form.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox, type FormInstance } from "element-plus";
import { Plus, ShoppingCart, Timer, Box, Sell, Picture } from "@element-plus/icons-vue";
import {
  getAdminSeckillGoodsList,
  addSeckillGoods,
  updateSeckillGoods,
  deleteSeckillGoods,
  updateSeckillGoodsStatus,
  getSeckillOverview,
} from "@/api/seckill";

const queryForm = reactive({
  page: 1,
  pageSize: 10,
  title: "",
  status: undefined as number | undefined,
});

const tableData = ref<any[]>([]);
const total = ref(0);
const loading = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const formRef = ref<FormInstance>();

const overview = ref<any>({});

const form = reactive({
  id: undefined as number | undefined,
  title: "",
  description: "",
  imageUrl: "",
  originalPrice: 0,
  seckillPrice: 0,
  stock: 0,
  limitPerUser: 1,
  startTime: "",
  endTime: "",
});

const rules = {
  title: [{ required: true, message: "请输入商品标题", trigger: "blur" }],
  originalPrice: [{ required: true, message: "请输入原价", trigger: "blur" }],
  seckillPrice: [{ required: true, message: "请输入秒杀价", trigger: "blur" }],
  stock: [{ required: true, message: "请输入库存", trigger: "blur" }],
  startTime: [{ required: true, message: "请选择开始时间", trigger: "change" }],
  endTime: [{ required: true, message: "请选择结束时间", trigger: "change" }],
};

const loadData = async () => {
  loading.value = true;
  try {
    const res = await getAdminSeckillGoodsList(queryForm);
    if (res.code === 200) {
      tableData.value = res.data.records || [];
      total.value = res.data.total || 0;
    }
  } catch (error) {
    console.error("加载失败:", error);
  } finally {
    loading.value = false;
  }
};

const loadOverview = async () => {
  try {
    const res = await getSeckillOverview();
    if (res.code === 200) {
      overview.value = res.data || {};
    }
  } catch (error) {
    console.error("加载统计失败:", error);
  }
};

const handleSearch = () => {
  queryForm.page = 1;
  loadData();
};

const handleReset = () => {
  queryForm.title = "";
  queryForm.status = undefined;
  queryForm.page = 1;
  loadData();
};

const handleAdd = () => {
  isEdit.value = false;
  Object.assign(form, {
    id: undefined,
    title: "",
    description: "",
    imageUrl: "",
    originalPrice: 0,
    seckillPrice: 0,
    stock: 0,
    limitPerUser: 1,
    startTime: "",
    endTime: "",
  });
  dialogVisible.value = true;
};

const handleEdit = (row: any) => {
  isEdit.value = true;
  Object.assign(form, row);
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  await formRef.value.validate();

  submitLoading.value = true;
  try {
    if (isEdit.value && form.id) {
      await updateSeckillGoods(form.id, form);
      ElMessage.success("更新成功");
    } else {
      await addSeckillGoods(form);
      ElMessage.success("添加成功");
    }
    dialogVisible.value = false;
    loadData();
    loadOverview();
  } catch (error: any) {
    ElMessage.error(error.msg || "操作失败");
  } finally {
    submitLoading.value = false;
  }
};

const handleToggleStatus = async (row: any) => {
  const newStatus = row.status === 1 ? 0 : 1;
  const action = newStatus === 1 ? "上架" : "下架";

  try {
    await ElMessageBox.confirm(`确定要${action}该商品吗？`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await updateSeckillGoodsStatus(row.id, newStatus);
    ElMessage.success(`${action}成功`);
    loadData();
    loadOverview();
  } catch (error: any) {
    if (error !== "cancel") {
      ElMessage.error("操作失败");
    }
  }
};

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm("确定要删除该秒杀商品吗？", "删除确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteSeckillGoods(row.id);
    ElMessage.success("删除成功");
    loadData();
    loadOverview();
  } catch (error: any) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
    }
  }
};

const formatTime = (time: string) => {
  if (!time) return "-";
  return time.substring(0, 16).replace("T", " ");
};

onMounted(() => {
  loadData();
  loadOverview();
});
</script>

<style lang="scss" scoped>
.seckill-page {
  padding: var(--spacing-xl);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);

  .header-title h2 {
    margin: 0 0 8px;
    font-size: 24px;
    font-weight: 600;
    color: var(--text-primary);
  }

  .header-desc {
    margin: 0;
    color: var(--text-secondary);
    font-size: 14px;
  }
}

.stats-row {
  margin-bottom: var(--spacing-lg);

  .stat-card {
    border-radius: var(--border-radius-lg);
  }

  .stat-content {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;

    &.total { background: linear-gradient(135deg, #667eea, #764ba2); }
    &.active { background: linear-gradient(135deg, #f093fb, #f5576c); }
    &.stock { background: linear-gradient(135deg, #4facfe, #00f2fe); }
    &.sold { background: linear-gradient(135deg, #43e97b, #38f9d7); }
  }

  .stat-info {
    .stat-value {
      font-size: 28px;
      font-weight: 700;
      color: var(--text-primary);
    }

    .stat-label {
      font-size: 14px;
      color: var(--text-secondary);
    }
  }
}

.filter-card {
  margin-bottom: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
}

.table-card {
  border-radius: var(--border-radius-lg);
}

.goods-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .goods-image {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    object-fit: cover;
  }

  .goods-image-placeholder {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    background: #f5f7fa;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #c0c4cc;
  }

  .goods-detail {
    flex: 1;
    min-width: 0;

    .goods-title {
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: 4px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .goods-desc {
      font-size: 12px;
      color: var(--text-secondary);
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

.price-info {
  .original-price {
    font-size: 14px;
    color: var(--text-secondary);
    text-decoration: line-through;
  }

  .seckill-price {
    font-size: 16px;
    font-weight: 600;
    color: #f5576c;
  }
}

.low-stock {
  color: #f5576c;
  font-weight: 600;
}

.time-info {
  font-size: 12px;
  color: var(--text-secondary);
}

.pagination-wrapper {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
}
</style>
