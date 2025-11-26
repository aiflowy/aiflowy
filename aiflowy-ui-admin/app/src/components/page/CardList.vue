<script setup lang="ts">
import { computed } from 'vue';

import { useAccess } from '@aiflowy/access';

import { MoreFilled } from '@element-plus/icons-vue';
import {
  ElAvatar,
  ElButton,
  ElCard,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElTooltip,
} from 'element-plus';

export interface ActionButton {
  icon: any;
  text: string;
  action: string;
  className: string;
  permission: string;
}

export interface CardListProps {
  iconField?: string;
  titleField?: string;
  descField?: string;
  actions?: ActionButton[];
  defaultIcon: any;
  data: any[];
}
const props = withDefaults(defineProps<CardListProps>(), {
  iconField: 'icon',
  titleField: 'title',
  descField: 'description',
  actions: () => [],
});
const emit = defineEmits(['onAction']);
const { hasAccessByCodes } = useAccess();
const filterActions = computed(() => {
  return props.actions.filter((action) => {
    return hasAccessByCodes([action.permission]);
  });
});
const visibleActions = computed(() => {
  return filterActions.value.length <= 3
    ? filterActions.value
    : filterActions.value.slice(0, 3);
});
const hiddenActions = computed(() => {
  return filterActions.value.length > 3 ? filterActions.value.slice(3) : [];
});
function handleAction(row: any, action: any) {
  emit('onAction', row, action);
}
</script>

<template>
  <div class="card-list-container">
    <div class="card-grid">
      <ElCard
        v-for="(item, index) in props.data"
        :key="index"
        shadow="hover"
        footer-class="foot-c"
      >
        <div class="card-content">
          <div>
            <ElAvatar :src="item[iconField] || defaultIcon" />
          </div>
          <div style="width: 80%">
            <ElTooltip :content="item[titleField]" placement="top">
              <div class="item-title">
                {{ item[titleField] }}
              </div>
            </ElTooltip>
            <ElTooltip :content="item[descField]" placement="top">
              <div class="item-desc">
                {{ item[descField] }}
              </div>
            </ElTooltip>
          </div>
        </div>
        <template #footer>
          <div :class="visibleActions.length > 2 ? 'footer-div' : ''">
            <ElButton
              v-for="(action, idx) in visibleActions"
              :key="idx"
              :icon="action.icon"
              link
              @click="handleAction(item, action.action)"
            >
              {{ action.text }}
            </ElButton>
            <ElDropdown v-if="hiddenActions.length > 0" trigger="click">
              <ElButton :icon="MoreFilled" link />
              <template #dropdown>
                <ElDropdownMenu>
                  <ElDropdownItem
                    v-for="(action, idx) in hiddenActions"
                    :key="idx"
                    @click="handleAction(item, action.action)"
                  >
                    <div :class="action.className">{{ action.text }}</div>
                  </ElDropdownItem>
                </ElDropdownMenu>
              </template>
            </ElDropdown>
          </div>
        </template>
      </ElCard>
    </div>
  </div>
</template>

<style scoped>
:deep(.el-card__footer) {
  background:
    linear-gradient(
      180deg,
      var(--el-color-primary-light-9),
      var(--el-bg-color)
    ),
    var(--el-bg-color);
}
.footer-div {
  display: flex;
  justify-content: space-between;
}
.card-list-container {
  width: 100%;
  overflow-x: auto;
  padding: 10px 0;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  min-width: min(100%, 900px); /* 确保至少显示3个卡片 */
}

.card-content {
  display: flex;
  gap: 1vw;
}

.item-title {
  font-size: clamp(12px, 1.2vw, 16px);
  font-weight: 600;
  margin-bottom: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-desc {
  font-size: clamp(8px, 1vw, 12px);
  margin-bottom: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 响应式调整 */
@media (max-width: 1024px) {
  .card-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
}

@media (max-width: 768px) {
  .card-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  }
}

@media (max-width: 480px) {
  .card-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
.item-danger {
  color: var(--el-color-danger);
}
</style>
