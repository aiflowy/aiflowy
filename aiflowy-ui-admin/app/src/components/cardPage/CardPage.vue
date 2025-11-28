<script setup>
import { computed } from 'vue';

import { MoreFilled } from '@element-plus/icons-vue';
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

import { hasPermission } from '#/api/common/hasPermission.ts';

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

const filteredActions = computed(() => {
  return dropdownActions.value.filter((action) => {
    return hasPermission([action.permission]);
  });
});
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
              link
              class="action-btn"
              :icon="action.icon"
              v-access:code="action.permission"
              @click="handleActionClick(action, item)"
            >
              {{ action.label }}
              <span
                v-if="
                  index < visibleActions.length - 1 ||
                  (index === visibleActions.length - 1 &&
                    filteredActions.length > 0)
                "
                class="divider"
              ></span>
            </ElButton>

            <!-- 更多操作下拉菜单 -->
            <ElDropdown
              v-if="filteredActions.length > 0"
              class="action-btn"
              @command="(command) => handleActionClick(command, item)"
            >
              <ElIcon class="el-icon--right">
                <MoreFilled />
              </ElIcon>
              <template #dropdown>
                <ElDropdownMenu>
                  <ElDropdownItem
                    v-for="action in filteredActions"
                    :key="action.name"
                    :command="action"
                    :icon="action.icon"
                    :class="{
                      'delete-dropdown-item': action.name === 'delete',
                    }"
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

    <div v-if="data.length === 0" class="empty-state">
      <ElEmpty />
    </div>
  </div>
</template>

<style scoped>
.card-list-container {
  width: 100%;
  height: 100%;
}

.card-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

:deep(.el-card__body) {
  padding: 20px 0 0 0;
}
.card-item {
  transition: all 0.3s ease;
  border-radius: 8px;
  flex-shrink: 0;
  height: 165px;
  min-width: 0;
}
:deep(.delete-dropdown-item) {
  color: #ff4d4f !important;
}
:deep(.delete-dropdown-item:hover) {
  color: #ff7875 !important;
}
:deep(.delete-dropdown-item .el-icon) {
  color: inherit !important;
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
  padding: 0 20px 0 20px;
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
  align-items: center;
  height: 48px;
  background:
    linear-gradient(
      180deg,
      var(--el-color-primary-light-9),
      var(--el-bg-color)
    ),
    var(--el-bg-color);
}

.action-btn {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  min-width: 0;
  color: #acb7c6;
  position: relative;
  &:hover {
    color: var(--el-color-primary);
  }
}

.more-btn {
  width: 100%;
  justify-content: center;
}

.divider {
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 1px;
  height: 20px;
  background-color: #e0e0e0;
}

/* 确保按钮内容不换行 */
.action-btn .el-button {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  justify-content: center;
}
.empty-state {
  padding: 40px 0;
  text-align: center;
}

/* 响应式设计 */
/* 超大屏幕 */
@media (min-width: 1920px) {
  .card-list {
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 24px;
  }
}

/* 大屏幕 */
@media (min-width: 1200px) and (max-width: 1919px) {
  .card-list {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 20px;
  }
}

/* 中等屏幕 */
@media (min-width: 992px) and (max-width: 1199px) {
  .card-list {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 16px;
  }

  .card-header {
    padding: 0 16px 0 16px;
    gap: 12px;
  }

  .card-title {
    font-size: 15px;
  }

  .card-desc {
    font-size: 13px;
    height: 36px;
    min-height: 36px;
  }
}

/* 小屏幕 - 平板 */
@media (min-width: 768px) and (max-width: 991px) {
  .card-list {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 16px;
  }
  .card-header {
    padding: 0 12px 0 12px;
    gap: 10px;
  }

  .card-title {
    font-size: 14px;
    margin-bottom: 6px;
  }

  .card-desc {
    font-size: 12px;
    height: 32px;
    min-height: 32px;
    -webkit-line-clamp: 2;
  }

  .card-actions {
    height: 40px;
  }

  .action-btn .el-button {
    font-size: 11px;
  }
}
</style>
