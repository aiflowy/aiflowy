<script setup>
import { computed, ref } from 'vue';

import { ArrowDown } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElIcon,
  ElInput,
} from 'element-plus';

// 定义组件属性
const props = defineProps({
  // 按钮配置数组
  buttons: {
    type: Array,
    default: () => [],
    validator: (value) => {
      return value.every((button) => {
        return (
          typeof button.text === 'string' &&
          (button.key || typeof button.key === 'string')
        );
      });
    },
  },
  // 最大显示按钮数量（不包括下拉菜单）
  maxVisibleButtons: {
    type: Number,
    default: 3,
  },
  // 搜索框占位符
  searchPlaceholder: {
    type: String,
    default: '请输入搜索内容',
  },
});

const emit = defineEmits(['search', 'button-click', 'buttonClick']);

// 搜索值
const searchValue = ref('');

// 计算显示的按钮
const visibleButtons = computed(() => {
  return props.buttons.slice(0, props.maxVisibleButtons);
});

// 计算下拉菜单中的按钮
const dropdownButtons = computed(() => {
  return props.buttons.slice(props.maxVisibleButtons);
});

// 处理搜索
const handleSearch = () => {
  emit('search', searchValue.value);
};

const handleReset = () => {
  searchValue.value = '';
};

// 处理按钮点击
const handleButtonClick = (button) => {
  emit('buttonClick', {
    type: 'button',
    key: button.key,
    button,
    data: button.data,
  });
};

// 处理下拉菜单点击
const handleDropdownClick = (button) => {
  emit('buttonClick', {
    type: 'dropdown',
    key: button.key,
    button,
    data: button.data,
  });
};
</script>

<template>
  <div class="custom-header">
    <!-- 左侧搜索区域 -->
    <div class="header-left">
      <div class="search-container">
        <div>
          <ElInput
            v-model="searchValue"
            :placeholder="$t('common.searchPlaceholder')"
            class="search-input"
            @keyup.enter="handleSearch"
            clearable
          />
        </div>
        <div>
          <ElButton type="primary" @click="handleSearch">
            {{ $t('button.query') }}
          </ElButton>
        </div>
        <div>
          <ElButton @click="handleReset">
            {{ $t('button.reset') }}
          </ElButton>
        </div>
      </div>
    </div>

    <!-- 右侧按钮区域 -->
    <div class="header-right">
      <!-- 显示的按钮（最多3个） -->
      <template
        v-for="(button, index) in visibleButtons"
        :key="button.key || index"
      >
        <ElButton
          :type="button.type || 'default'"
          :icon="button.icon"
          :disabled="button.disabled"
          @click="handleButtonClick(button)"
        >
          {{ button.text }}
        </ElButton>
      </template>

      <!-- 下拉菜单（隐藏的按钮） -->
      <ElDropdown
        v-if="dropdownButtons.length > 0"
        @command="handleDropdownClick"
      >
        <ElButton>
          更多<ElIcon class="el-icon--right"><ArrowDown /></ElIcon>
        </ElButton>
        <template #dropdown>
          <ElDropdownMenu>
            <ElDropdownItem
              v-for="button in dropdownButtons"
              :key="button.key"
              :command="button"
              :disabled="button.disabled"
            >
              <ElIcon v-if="button.icon">
                <component :is="button.icon" />
              </ElIcon>
              <span style="margin-left: 8px">{{ button.text }}</span>
            </ElDropdownItem>
          </ElDropdownMenu>
        </template>
      </ElDropdown>
    </div>
  </div>
</template>

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
  display: flex;
  gap: 12px;
}

.search-input {
  border-radius: 4px;
  width: 300px;
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
  }

  .header-right {
    flex-wrap: wrap;
  }
}
</style>
