<template>
  <div class="signin-card">
    <div class="signin-header">
      <div class="signin-icon">
        <el-icon :size="32"><Calendar /></el-icon>
      </div>
      <div class="signin-title">每日签到</div>
    </div>

    <div class="signin-stats">
      <div class="stat-item">
        <div class="stat-value">{{ signInStatus?.continuousDays || 0 }}</div>
        <div class="stat-label">连续签到</div>
      </div>
      <div class="stat-divider"></div>
      <div class="stat-item">
        <div class="stat-value">{{ signInStatus?.totalDays || 0 }}</div>
        <div class="stat-label">累计签到</div>
      </div>
    </div>

    <el-button
      type="primary"
      class="signin-button"
      :disabled="signInStatus?.signedToday"
      @click="handleSignIn"
      :loading="loading"
    >
      {{ signInStatus?.signedToday ? '今日已签到' : '立即签到' }}
    </el-button>

    <div v-if="showCalendar" class="signin-calendar">
      <div class="calendar-header">
        <el-button link @click="prevMonth">
          <el-icon><ArrowLeft /></el-icon>
        </el-button>
        <span>{{ currentYear }}年{{ currentMonth }}月</span>
        <el-button link @click="nextMonth">
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="calendar-grid">
        <div v-for="day in calendarDays" :key="day.date" class="calendar-day" :class="{
          'is-signed': day.isSigned,
          'is-today': day.isToday,
          'is-other-month': day.isOtherMonth
        }">
          {{ day.day }}
        </div>
      </div>
    </div>

    <div class="signin-toggle" @click="showCalendar = !showCalendar">
      {{ showCalendar ? '收起日历' : '查看签到日历' }}
      <el-icon><ArrowDown v-if="!showCalendar" /><ArrowUp v-else /></el-icon>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Calendar, ArrowLeft, ArrowRight, ArrowDown, ArrowUp } from '@element-plus/icons-vue';
import { signIn, getSignInStatus, getMonthSignInDates, type SignInVO } from '@/api/signin';

const signInStatus = ref<SignInVO | null>(null);
const loading = ref(false);
const showCalendar = ref(false);
const currentYear = ref(new Date().getFullYear());
const currentMonth = ref(new Date().getMonth() + 1);
const signedDates = ref<string[]>([]);

const calendarDays = computed(() => {
  const year = currentYear.value;
  const month = currentMonth.value;
  const firstDay = new Date(year, month - 1, 1);
  const lastDay = new Date(year, month, 0);
  const daysInMonth = lastDay.getDate();
  const startWeekday = firstDay.getDay();

  const days = [];
  const prevMonthDays = new Date(year, month - 1, 0).getDate();

  for (let i = startWeekday - 1; i >= 0; i--) {
    days.push({
      day: prevMonthDays - i,
      date: `${year}-${String(month - 1).padStart(2, '0')}-${String(prevMonthDays - i).padStart(2, '0')}`,
      isSigned: false,
      isToday: false,
      isOtherMonth: true
    });
  }

  for (let i = 1; i <= daysInMonth; i++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(i).padStart(2, '0')}`;
    const today = new Date().toISOString().split('T')[0];
    days.push({
      day: i,
      date: dateStr,
      isSigned: signedDates.value.includes(dateStr),
      isToday: dateStr === today,
      isOtherMonth: false
    });
  }

  const remainingDays = 42 - days.length;
  for (let i = 1; i <= remainingDays; i++) {
    days.push({
      day: i,
      date: `${year}-${String(month + 1).padStart(2, '0')}-${String(i).padStart(2, '0')}`,
      isSigned: false,
      isToday: false,
      isOtherMonth: true
    });
  }

  return days;
});

const loadSignInStatus = async () => {
  try {
    const res = await getSignInStatus();
    if (res.code === 200) {
      signInStatus.value = res.data;
    }
  } catch (error) {
    console.error('获取签到状态失败', error);
  }
};

const loadMonthSignInDates = async () => {
  try {
    const res = await getMonthSignInDates(currentYear.value, currentMonth.value);
    if (res.code === 200) {
      signedDates.value = res.data;
    }
  } catch (error) {
    console.error('获取签到日历失败', error);
  }
};

const handleSignIn = async () => {
  loading.value = true;
  try {
    const res = await signIn();
    if (res.code === 200) {
      signInStatus.value = res.data;
      ElMessage.success(`签到成功！连续签到${res.data.continuousDays}天`);
      await loadMonthSignInDates();
    }
  } catch (error: any) {
    ElMessage.error(error.msg || '签到失败');
  } finally {
    loading.value = false;
  }
};

const prevMonth = () => {
  if (currentMonth.value === 1) {
    currentYear.value--;
    currentMonth.value = 12;
  } else {
    currentMonth.value--;
  }
  loadMonthSignInDates();
};

const nextMonth = () => {
  if (currentMonth.value === 12) {
    currentYear.value++;
    currentMonth.value = 1;
  } else {
    currentMonth.value++;
  }
  loadMonthSignInDates();
};

onMounted(() => {
  loadSignInStatus();
  loadMonthSignInDates();
});
</script>

<style lang="scss" scoped>
.signin-card {
  background: var(--bg-primary);
  border-radius: var(--border-radius-xl);
  padding: 24px;
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-light);
  height: 100%;
  min-height: 400px;
  display: flex;
  flex-direction: column;
}

.signin-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-light);
}

.signin-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-active));
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.signin-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.signin-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
  padding: 20px 16px;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
  border-radius: var(--border-radius-md);
  border: 1px solid rgba(102, 126, 234, 0.15);
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.stat-divider {
  width: 1px;
  background: var(--border-light);
}

.signin-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--border-radius-md);
  background: linear-gradient(135deg, var(--primary-color), var(--primary-active));
  border: none;
  transition: all 0.3s;

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  }

  &:disabled {
    background: linear-gradient(135deg, #10b981, #059669);
    opacity: 0.8;
  }
}

.signin-calendar {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid var(--border-light);
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius-sm);
  font-size: 12px;
  color: var(--text-primary);
  background: var(--bg-secondary);
  transition: all 0.2s;
  min-width: 28px;

  &.is-signed {
    background: var(--primary-color);
    color: white;
    font-weight: 600;
  }

  &.is-today {
    border: 2px solid var(--primary-color);
  }

  &.is-other-month {
    color: var(--text-tertiary);
    opacity: 0.5;
  }
}

.signin-toggle {
  margin-top: 16px;
  text-align: center;
  color: var(--primary-color);
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 8px;
  border-radius: var(--border-radius-sm);
  transition: background 0.2s;

  &:hover {
    background: var(--bg-secondary);
  }
}
</style>
