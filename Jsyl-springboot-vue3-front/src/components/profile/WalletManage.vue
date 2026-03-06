<template>
  <div class="wallet-manage">
    <el-card shadow="hover" class="balance-card">
      <div class="balance-content">
        <div class="balance-label">账户余额</div>
        <div class="balance-amount">¥{{ balance.toFixed(2) }}</div>
        <div class="balance-actions">
          <el-button type="primary" @click="showRechargeDialog">
            <el-icon><Wallet /></el-icon>
            充值
          </el-button>
          <el-button
            type="success"
            @click="showWithdrawDialog"
            :disabled="balance <= 0"
          >
            <el-icon><Money /></el-icon>
            提现
          </el-button>
        </div>
      </div>
    </el-card>

    <el-card shadow="hover" class="transactions-card">
      <template #header>
        <div class="card-header">
          <span class="title">收支明细</span>
          <el-radio-group
            v-model="transactionType"
            size="small"
            @change="loadTransactions"
          >
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="income">收入</el-radio-button>
            <el-radio-button label="expense">支出</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <div class="transaction-list">
        <div
          v-for="item in filteredTransactions"
          :key="item.id"
          class="transaction-item"
        >
          <div class="transaction-icon" :class="item.type">
            <el-icon v-if="item.type === 'income'"><BottomRight /></el-icon>
            <el-icon v-else><TopLeft /></el-icon>
          </div>

          <div class="transaction-info">
            <div class="transaction-title">{{ item.title }}</div>
            <div class="transaction-time">{{ item.time }}</div>
          </div>

          <div class="transaction-amount" :class="item.type">
            {{ item.type === "income" ? "+" : "-" }}¥{{
              item.amount.toFixed(2)
            }}
          </div>
        </div>

        <el-empty
          v-if="filteredTransactions.length === 0"
          description="暂无收支记录"
        />
      </div>
    </el-card>

    <el-dialog v-model="rechargeVisible" title="充值" width="400px">
      <el-form :model="rechargeForm" label-width="80px">
        <el-form-item label="充值金额">
          <el-input-number
            v-model="rechargeForm.amount"
            :min="1"
            :max="10000"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="rechargeForm.paymentMethod">
            <el-radio label="alipay">支付宝</el-radio>
            <el-radio label="wechat">微信支付</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="rechargeVisible = false">取消</el-button>
        <el-button type="primary" :loading="recharging" @click="handleRecharge"
          >确认充值</el-button
        >
      </template>
    </el-dialog>

    <el-dialog v-model="withdrawVisible" title="提现" width="400px">
      <el-form :model="withdrawForm" label-width="80px">
        <el-form-item label="提现金额">
          <el-input-number
            v-model="withdrawForm.amount"
            :min="1"
            :max="balance"
            :precision="2"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="到账账户">
          <el-input
            v-model="withdrawForm.account"
            placeholder="请输入支付宝账号"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="withdrawVisible = false">取消</el-button>
        <el-button type="success" :loading="withdrawing" @click="handleWithdraw"
          >确认提现</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";
import { Wallet, Money, BottomRight, TopLeft } from "@element-plus/icons-vue";
import { getTransactionHistory, type Order } from "@/api/order";

interface Transaction {
  id: number;
  title: string;
  amount: number;
  type: "income" | "expense";
  time: string;
}

interface Props {
  balance?: number;
}

const props = withDefaults(defineProps<Props>(), {
  balance: 0,
});

const balance = ref(props.balance);
const transactionType = ref("");
const transactions = ref<Transaction[]>([
  {
    id: 1,
    title: "订单收入 #1001",
    amount: 50.0,
    type: "income",
    time: "2024-01-15 14:30",
  },
  {
    id: 2,
    title: "充值",
    amount: 100.0,
    type: "income",
    time: "2024-01-14 10:00",
  },
  {
    id: 3,
    title: "订单支出 #1002",
    amount: 30.0,
    type: "expense",
    time: "2024-01-13 16:20",
  },
  {
    id: 4,
    title: "订单收入 #1003",
    amount: 80.0,
    type: "income",
    time: "2024-01-12 09:15",
  },
]);

const rechargeVisible = ref(false);
const rechargeForm = reactive({
  amount: 100,
  paymentMethod: "alipay",
});
const recharging = ref(false);

const withdrawVisible = ref(false);
const withdrawForm = reactive({
  amount: 0,
  account: "",
});
const withdrawing = ref(false);

const filteredTransactions = computed(() => {
  if (!transactionType.value) return transactions.value;
  return transactions.value.filter((t) => t.type === transactionType.value);
});

const loadTransactions = async () => {
  try {
    const res = await getTransactionHistory();
    if (res?.code === 200 && res.data) {
      transactions.value = res.data.map((order: any) => {
        const isIncome = order.orderNo?.startsWith("收入");
        return {
          id: order.id,
          title: isIncome
            ? `订单收入 ${order.orderNo?.replace("收入-", "")}`
            : `订单支出 ${order.orderNo?.replace("支出-", "")}`,
          amount: order.orderAmount || 0,
          type: isIncome ? "income" : "expense",
          time: formatOrderTime(order.createTime),
        };
      });
    }
  } catch (error) {
    console.error("加载交易记录失败:", error);
  }
};

const formatOrderTime = (time: any): string => {
  if (!time) return "-";
  if (typeof time === "string") return time;
  if (Array.isArray(time)) {
    const [year, month, day, hour, minute] = time;
    return `${year}-${String(month).padStart(2, "0")}-${String(day).padStart(
      2,
      "0"
    )} ${String(hour).padStart(2, "0")}:${String(minute).padStart(2, "0")}`;
  }
  return "-";
};

const showRechargeDialog = () => {
  rechargeForm.amount = 100;
  rechargeVisible.value = true;
};

const showWithdrawDialog = () => {
  withdrawForm.amount = 0;
  withdrawForm.account = "";
  withdrawVisible.value = true;
};

const handleRecharge = async () => {
  if (rechargeForm.amount <= 0) {
    ElMessage.warning("请输入正确的充值金额");
    return;
  }

  recharging.value = true;
  setTimeout(() => {
    balance.value += rechargeForm.amount;
    transactions.value.unshift({
      id: Date.now(),
      title: "充值",
      amount: rechargeForm.amount,
      type: "income",
      time: new Date().toLocaleString("zh-CN"),
    });
    ElMessage.success("充值成功");
    rechargeVisible.value = false;
    recharging.value = false;
  }, 1000);
};

const handleWithdraw = async () => {
  if (withdrawForm.amount <= 0) {
    ElMessage.warning("请输入正确的提现金额");
    return;
  }
  if (withdrawForm.amount > balance.value) {
    ElMessage.warning("余额不足");
    return;
  }
  if (!withdrawForm.account) {
    ElMessage.warning("请输入到账账户");
    return;
  }

  withdrawing.value = true;
  setTimeout(() => {
    balance.value -= withdrawForm.amount;
    transactions.value.unshift({
      id: Date.now(),
      title: "提现到 " + withdrawForm.account,
      amount: withdrawForm.amount,
      type: "expense",
      time: new Date().toLocaleString("zh-CN"),
    });
    ElMessage.success("提现成功");
    withdrawVisible.value = false;
    withdrawing.value = false;
  }, 1000);
};

onMounted(() => {
  loadTransactions();
  watch(
    () => props.balance,
    (newVal) => {
      balance.value = newVal || 0;
    },
    { immediate: true }
  );
});
</script>

<style lang="scss" scoped>
.wallet-manage {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.balance-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;

  :deep(.el-card__body) {
    padding: 40px;
  }

  .balance-content {
    text-align: center;
    color: white;

    .balance-label {
      font-size: 16px;
      opacity: 0.9;
      margin-bottom: 8px;
    }

    .balance-amount {
      font-size: 48px;
      font-weight: 700;
      margin-bottom: 24px;
    }

    .balance-actions {
      display: flex;
      justify-content: center;
      gap: 16px;

      .el-button {
        min-width: 120px;

        &.el-button--primary {
          background: white;
          color: #667eea;
          border-color: white;
        }

        &.el-button--success {
          background: rgba(255, 255, 255, 0.2);
          color: white;
          border-color: rgba(255, 255, 255, 0.5);
        }
      }
    }
  }
}

.transactions-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 18px;
      font-weight: 600;
    }
  }
}

.transaction-list {
  .transaction-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    border-bottom: 1px solid #ebeef5;

    &:last-child {
      border-bottom: none;
    }
  }

  .transaction-icon {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;

    &.income {
      background: rgba(103, 194, 58, 0.1);
      color: #67c23a;
    }

    &.expense {
      background: rgba(245, 108, 108, 0.1);
      color: #f56c6c;
    }
  }

  .transaction-info {
    flex: 1;

    .transaction-title {
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 4px;
    }

    .transaction-time {
      font-size: 12px;
      color: #909399;
    }
  }

  .transaction-amount {
    font-size: 18px;
    font-weight: 600;

    &.income {
      color: #67c23a;
    }

    &.expense {
      color: #f56c6c;
    }
  }
}
</style>
