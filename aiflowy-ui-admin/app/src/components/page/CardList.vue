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
  ElText,
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
      :style="{
        '--el-box-shadow-light': '0px 2px 12px 0px rgb(100 121 153 10%)',
      }"
    >
      <div class="flex flex-col gap-3">
        <div class="flex items-center gap-3">
          <ElAvatar
            class="shrink-0"
            :src="item[iconField] || defaultIcon"
            :size="36"
          />
          <ElText truncated size="large" class="font-medium">
            {{ item[titleField] }}
          </ElText>
        </div>
        <ElText line-clamp="2" class="item-desc w-full">
          {{ item[titleField] }}
        </ElText>
      </div>
      <template #footer>
        <div :class="visibleActions.length > 2 ? 'footer-div' : ''">
          <template v-for="(action, idx) in visibleActions" :key="idx">
            <ElButton
              :icon="typeof action.icon === 'string' ? undefined : action.icon"
              size="small"
              :style="{
                '--el-button-text-color': 'hsl(220deg 9.68% 63.53%)',
                '--el-button-font-weight': 400,
              }"
              link
              @click="action.onClick(item)"
            >
              <template v-if="typeof action.icon === 'string'" #icon>
                <IconifyIcon :icon="action.icon" />
              </template>
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
            <ElButton
              :style="{
                '--el-button-text-color': 'hsl(220deg 9.68% 63.53%)',
                '--el-button-font-weight': 400,
              }"
              :icon="MoreFilled"
              link
            />
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

:deep(.el-card__footer) {
  border-top: none;
}

.footer-div {
  display: flex;
  justify-content: space-between;
  padding: 8px 20px;
  background-color: hsl(var(--background-deep));
  border-radius: 8px;
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
  height: 40px;
  font-size: clamp(8px, 1vw, 14px);
  line-height: 20px;
  color: #75808d;
}

.item-danger {
  color: var(--el-color-danger);
}
</style>
