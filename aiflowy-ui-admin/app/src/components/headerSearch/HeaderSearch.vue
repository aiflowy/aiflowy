<template>
  <div class="custom-header">
    <!-- 左侧搜索区域 -->
    <div class="header-left">
      <div class="search-container">
        <el-input
          v-model="searchValue"
          placeholder="请输入搜索内容"
          class="search-input"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 右侧按钮区域 -->
    <div class="header-right">
      <!-- 显示的按钮（最多3个） -->
      <template v-for="(button, index) in visibleButtons" :key="button.key || index">
        <el-button
          :type="button.type || 'default'"
          :icon="button.icon"
          :disabled="button.disabled"
          @click="handleButtonClick(button)"
        >
          {{ button.text }}
        </el-button>
      </template>

      <!-- 下拉菜单（隐藏的按钮） -->
      <el-dropdown v-if="dropdownButtons.length > 0" @command="handleDropdownClick">
        <el-button>
          更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
        </el-button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="button in dropdownButtons"
              :key="button.key"
              :command="button"
              :disabled="button.disabled"
            >
              <el-icon v-if="button.icon"><component :is="button.icon" /></el-icon>
              <span style="margin-left: 8px;">{{ button.text }}</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElInput, ElButton, ElIcon, ElDropdown, ElDropdownMenu, ElDropdownItem } from 'element-plus'
import { Search, ArrowDown } from '@element-plus/icons-vue'

// 定义组件属性
const props = defineProps({
  // 按钮配置数组
  buttons: {
    type: Array,
    default: () => [],
    validator: (value) => {
      return value.every(button => {
        return typeof button.text === 'string' &&
          (button.key || typeof button.key === 'string')
      })
    }
  },
  // 最大显示按钮数量（不包括下拉菜单）
  maxVisibleButtons: {
    type: Number,
    default: 3
  },
  // 搜索框占位符
  searchPlaceholder: {
    type: String,
    default: '请输入搜索内容'
  }
})

const emit = defineEmits(['search', 'button-click'])

// 搜索值
const searchValue = ref('')

// 计算显示的按钮
const visibleButtons = computed(() => {
  return props.buttons.slice(0, props.maxVisibleButtons)
})

// 计算下拉菜单中的按钮
const dropdownButtons = computed(() => {
  return props.buttons.slice(props.maxVisibleButtons)
})

// 处理搜索
const handleSearch = () => {
  emit('search', searchValue.value)
}

// 处理按钮点击
const handleButtonClick = (button) => {
  emit('button-click', {
    type: 'button',
    key: button.key,
    button: button,
    data: button.data
  })
}

// 处理下拉菜单点击
const handleDropdownClick = (button) => {
  emit('button-click', {
    type: 'dropdown',
    key: button.key,
    button: button,
    data: button.data
  })
}
</script>

<style scoped>
.custom-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e4e7ed;
}

.header-left {
  display: flex;
  align-items: center;
}

.search-container {
  width: 300px;
}

.search-input {
  border-radius: 4px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .custom-header {
    flex-direction: column;
    gap: 16px;
    padding: 12px 16px;
  }

  .header-left,
  .header-right {
    width: 100%;
    justify-content: center;
  }

  .search-container {
    width: 100%;
    max-width: 400px;
  }

  .header-right {
    flex-wrap: wrap;
  }
}
</style>
