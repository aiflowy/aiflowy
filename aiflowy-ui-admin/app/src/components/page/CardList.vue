<script setup lang="ts">
import { computed } from 'vue';

import { useAccess } from '@aiflowy/access';

import { MoreFilled } from '@element-plus/icons-vue';
import {
  ElAvatar,
  ElButton,
  ElCard,
  ElDivider,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElIcon,
  ElTooltip,
} from 'element-plus';

export interface ActionButton {
  icon: any;
  text: string;
  className: string;
  permission: string;
  onClick: (row: any) => void;
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
</script>

<template>
  <div class="card-grid">
    <ElCard
      v-for="(item, index) in props.data"
      :key="index"
      shadow="hover"
      footer-class="foot-c"
    >
      <div class="flex flex-col gap-3">
        <div class="flex items-center gap-3">
          <ElAvatar :src="item[iconField] || defaultIcon" :size="36" />
          <ElTooltip :content="item[titleField]" placement="top">
            <span class="text-base font-medium">{{ item[titleField] }}</span>
          </ElTooltip>
        </div>
        <ElTooltip :content="item[descField]" placement="top">
          <div class="item-desc">
            {{ item[descField] }}
          </div>
        </ElTooltip>
      </div>
      <template #footer>
        <div :class="visibleActions.length > 2 ? 'footer-div' : ''">
          <template v-for="(action, idx) in visibleActions" :key="idx">
            <ElButton
              :icon="action.icon"
              style="color: var(--el-color-info-light-3)"
              link
              @click="action.onClick(item)"
            >
              {{ action.text }}
            </ElButton>
            <ElDivider
              v-if="
                filterActions.length <= 3
                  ? idx < filterActions.length - 1
                  : true
              "
              direction="vertical"
            />
          </template>

          <ElDropdown v-if="hiddenActions.length > 0" trigger="click">
            <ElButton :icon="MoreFilled" link />
            <template #dropdown>
              <ElDropdownMenu>
                <ElDropdownItem
                  v-for="(action, idx) in hiddenActions"
                  :key="idx"
                  @click="action.onClick(item)"
                >
                  <template #default>
                    <div :class="`${action.className} handle-div`">
                      <ElIcon v-if="action.icon">
                        <component :is="action.icon" />
                      </ElIcon>
                      {{ action.text }}
                    </div>
                  </template>
                </ElDropdownItem>
              </ElDropdownMenu>
            </template>
          </ElDropdown>
        </div>
      </template>
    </ElCard>
  </div>
</template>

<style scoped>
:deep(.el-card__footer) {
  border-top: none;
}
.footer-div {
  display: flex;
  justify-content: space-between;
  padding: 8px 20px;
  border-radius: 8px;
  background-color: hsl(var(--background-deep));
}
.handle-div {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 0;
}
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  min-width: max(100%, 600px); /* 确保至少显示2个卡片 */
}

.item-desc {
  font-size: clamp(8px, 1vw, 12px);
  height: 20px;
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
