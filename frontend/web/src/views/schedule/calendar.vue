<template>
  <div class="page-container">
    <div class="page-header">
      <h3 class="page-title">排片日历</h3>
    </div>

    <el-card>
      <div class="calendar-header">
        <el-button @click="prevWeek"><el-icon><ArrowLeft /></el-icon> 上一周</el-button>
        <span class="date-range">{{ dateRangeText }}</span>
        <el-button @click="nextWeek">下一周 <el-icon><ArrowRight /></el-icon></el-button>
      </div>

      <div class="calendar-grid">
        <div class="calendar-header-row">
          <div class="time-column">时间</div>
          <div v-for="day in weekDays" :key="day.date" class="day-column" :class="{ 'is-today': day.isToday }">
            {{ day.weekDay }}<br>{{ day.date }}
          </div>
        </div>

        <div class="calendar-body">
          <div class="time-column">
            <div v-for="hour in hours" :key="hour" class="time-slot">{{ hour }}</div>
          </div>
          <div v-for="day in weekDays" :key="day.date" class="day-column">
            <div v-for="hour in hours" :key="hour" class="time-slot" @click="handleAdd(day.date, hour)">
              <div v-for="schedule in getSchedules(day.date, hour)" :key="schedule.id" class="schedule-item">
                <div class="movie-title">{{ schedule.movieTitle }}</div>
                <div class="hall-name">{{ schedule.hallName }}</div>
                <div class="price">¥{{ schedule.price }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getScheduleCalendar } from '@/api/schedule'

const router = useRouter()
const currentDate = ref(new Date())
const scheduleData = ref({})

const hours = ['08:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00']

const weekDays = computed(() => {
  const days = []
  const weekDayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const startOfWeek = new Date(currentDate.value)
  startOfWeek.setDate(startOfWeek.getDate() - startOfWeek.getDay())

  for (let i = 0; i < 7; i++) {
    const date = new Date(startOfWeek)
    date.setDate(date.getDate() + i)
    const dateStr = formatDate(date)
    const today = new Date()
    days.push({
      date: dateStr,
      weekDay: weekDayNames[date.getDay()],
      isToday: formatDate(today) === dateStr
    })
  }
  return days
})

const dateRangeText = computed(() => {
  const start = weekDays.value[0]?.date || ''
  const end = weekDays.value[6]?.date || ''
  return `${start} ~ ${end}`
})

const formatDate = (date) => {
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${m}-${d}`
}

const prevWeek = () => {
  const newDate = new Date(currentDate.value)
  newDate.setDate(newDate.getDate() - 7)
  currentDate.value = newDate
  fetchScheduleData()
}

const nextWeek = () => {
  const newDate = new Date(currentDate.value)
  newDate.setDate(newDate.getDate() + 7)
  currentDate.value = newDate
  fetchScheduleData()
}

const fetchScheduleData = async () => {
  try {
    const startDate = weekDays.value[0]?.date
    const endDate = weekDays.value[6]?.date
    const res = await getScheduleCalendar({ startDate, endDate })
    scheduleData.value = res.data
  } catch (error) {
    console.error('获取排片数据失败:', error)
  }
}

const getSchedules = (date, hour) => {
  const key = `${date}-${hour}`
  return scheduleData.value[key] || []
}

const handleAdd = (date, hour) => {
  router.push({ path: '/schedule/list', query: { showDate: date, showTime: hour } })
}

onMounted(() => fetchScheduleData())
</script>

<style lang="scss" scoped>
.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;

  .date-range {
    font-size: 16px;
    font-weight: 600;
  }
}

.calendar-grid {
  border: 1px solid #EBEEF5;

  .calendar-header-row {
    display: flex;
    background: #F5F7FA;
    border-bottom: 1px solid #EBEEF5;

    .time-column, .day-column {
      padding: 10px;
      text-align: center;
    }

    .time-column { width: 80px; flex-shrink: 0; }
    .day-column { flex: 1; border-left: 1px solid #EBEEF5; }
    .day-column.is-today { background: #ECF5FF; }
  }

  .calendar-body {
    display: flex;

    .time-column {
      width: 80px;
      flex-shrink: 0;

      .time-slot {
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-bottom: 1px solid #EBEEF5;
        color: #909399;
        font-size: 12px;
      }
    }

    .day-column {
      flex: 1;
      border-left: 1px solid #EBEEF5;

      .time-slot {
        height: 60px;
        border-bottom: 1px solid #EBEEF5;
        padding: 5px;
        cursor: pointer;
        position: relative;

        &:hover {
          background: #F5F7FA;
        }

        .schedule-item {
          background: #409EFF;
          color: #fff;
          padding: 4px 8px;
          border-radius: 4px;
          font-size: 12px;
          margin-bottom: 4px;

          .movie-title { font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
          .hall-name { opacity: 0.8; }
          .price { opacity: 0.9; }
        }
      }
    }
  }
}
</style>
