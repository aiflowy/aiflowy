<script setup>
import { computed } from 'vue';

import { ArrowDown } from '@element-plus/icons-vue';
import {
  ElAvatar,
  ElButton,
  ElCard,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElEmpty,
  ElIcon,
} from 'element-plus';

const props = defineProps({
  titleKey: {
    type: String,
    default: 'title',
  },
  avatarKey: {
    type: String,
    default: 'avatar',
  },
  descriptionKey: {
    type: String,
    default: 'description',
  },
  data: {
    type: Array,
    default: () => [],
  },
  // 操作按钮配置
  actions: {
    type: Array,
    default: () => [],
  },
});

// 定义组件事件
const emit = defineEmits(['actionClick']);

// 可见的操作按钮（最多3个）
const visibleActions = computed(() => {
  return props.actions.slice(0, 3);
});

// 下拉菜单中的操作按钮
const dropdownActions = computed(() => {
  return props.actions.slice(3);
});

// 处理操作按钮点击
const handleActionClick = (action, item) => {
  emit('actionClick', { action, item });
};
</script>

<template>
  <div class="card-list-container">
    <div class="card-list">
      <ElCard v-for="item in data" :key="item.id" class="card-item">
        <div class="card-content">
          <!-- 卡片头部：头像和基本信息 -->
          <div class="card-header">
            <ElAvatar :src="item[avatarKey]" />
            <div class="card-info">
              <h3 class="card-title">{{ item[titleKey] }}</h3>
              <p class="card-desc">{{ item[descriptionKey] }}</p>
            </div>
          </div>
          <!-- 操作按钮区域 -->
          <div class="card-actions">
            <!-- 最多显示3个操作按钮 -->
            <ElButton
              v-for="(action, index) in visibleActions"
              :key="index"
              :type="action.type || 'primary'"
              :icon="action.icon"
              size="small"
              @click="handleActionClick(action, item)"
            >
              {{ action.label }}
            </ElButton>

            <!-- 更多操作下拉菜单 -->
            <ElDropdown
              v-if="dropdownActions.length > 0"
              @command="(command) => handleActionClick(command, item)"
            >
              <ElButton size="small" style="margin-left: 8px">
                更多
                <ElIcon class="el-icon--right">
                  <ArrowDown />
                </ElIcon>
              </ElButton>
              <template #dropdown>
                <ElDropdownMenu>
                  <ElDropdownItem
                    v-for="action in dropdownActions"
                    :key="action.name"
                    :command="action"
                    :icon="action.icon"
                  >
                    {{ action.label }}
                  </ElDropdownItem>
                </ElDropdownMenu>
              </template>
            </ElDropdown>
          </div>
        </div>
      </ElCard>
    </div>

    <!-- 空状态（保留） -->
    <div v-if="data.length === 0" class="empty-state">
      <ElEmpty description="暂无数据" />
    </div>
  </div>
</template>

<style scoped>
.card-list-container {
  width: 100%;
  height: 100%;
}

.card-list {
  display: flex;
  min-width: 300px;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
}

.card-item {
  transition: all 0.3s ease;
  border-radius: 8px;
  width: 330px;
  flex-shrink: 0;
}

.card-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.card-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: var(--el-bg-color);
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  margin-bottom: 15px;
  flex: 1;
}

.card-info {
  flex: 1;
  min-width: 0;
  max-width: 100%;
}

.card-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  line-height: 1.4;
}

.card-desc {
  margin: 0;
  width: 100%;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  height: 42px;
  min-height: 42px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

/* 响应式设计（移除分页相关样式，保留卡片适配） */
@media (max-width: 768px) {
  .card-list {
    justify-content: center; /* 小屏幕下卡片居中 */
  }
  .card-item {
    width: 100%;
    max-width: 330px;
  }
  .card-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
}
</style>
