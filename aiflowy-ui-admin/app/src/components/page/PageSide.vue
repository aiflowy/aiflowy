<script setup lang="ts" generic="T extends { icon?: any; [key: string]: any }">
import { ref } from 'vue';

import { IconifyIcon } from '@aiflowy/icons';

import { MoreFilled } from '@element-plus/icons-vue';
import {
  ElButton,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElIcon,
} from 'element-plus';

interface Props {
  title?: string;
  menus: T[];
  labelKey: string;
  valueKey: string;
  controlBtns?: {
    icon?: any;
    label: string;
    onClick: (_: T) => void;
    type?: any;
  }[];
  footerButton?: {
    icon?: any;
    label: string;
    onClick: () => void;
  };
  defaultSelected?: number | string;
}

const props = defineProps<Props>();
const emits = defineEmits<{
  (e: 'change', item: T): void;
}>();
const panelWidth = ref(225);
const selected = ref<string>('');

const handleChange = (item: T) => {
  selected.value = item[props.valueKey];
  emits('change', item);
};
</script>

<template>
  <div
    class="flex w-[225px] flex-col rounded-lg border border-[var(--el-border-color)] bg-[var(--el-bg-color)] p-2"
    :style="{ width: `${panelWidth}px` }"
  >
    <div class="flex flex-1 flex-col gap-5">
      <h3 v-if="title && title.length > 0" class="text-base font-medium">
        {{ title }}
      </h3>

      <div class="flex-1 overflow-scroll">
        <div
          v-for="item in menus"
          :key="item[valueKey]"
          class="list-item"
          :class="{
            selected: (selected ?? defaultSelected) === item[valueKey],
          }"
          @click="handleChange(item)"
        >
          <div class="flex items-center gap-1">
            <div
              v-if="item.icon"
              class="ml-[-3px] flex items-center justify-center"
            >
              <IconifyIcon :icon="item.icon" />
            </div>
            <div>
              {{ item[labelKey] }}
            </div>
          </div>
          <ElDropdown
            v-if="controlBtns && !['', '0'].includes(item[valueKey])"
            @click.stop
          >
            <ElIcon>
              <MoreFilled />
            </ElIcon>
            <template #dropdown>
              <ElDropdownMenu>
                <ElDropdownItem
                  v-for="btn in controlBtns"
                  :key="btn.label"
                  @click="btn.onClick(item)"
                >
                  <ElButton :type="btn.type" :icon="btn.icon" link>
                    {{ btn.label }}
                  </ElButton>
                </ElDropdownItem>
              </ElDropdownMenu>
            </template>
          </ElDropdown>
        </div>
      </div>
    </div>

    <ElButton
      v-if="footerButton"
      @click="footerButton.onClick"
      :icon="footerButton.icon"
      plain
    >
      {{ footerButton.label }}
    </ElButton>
  </div>
</template>

<style scoped>
.list-item {
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: space-between;
  padding: 10px;
  margin-bottom: 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 14px;
}
.list-item:hover {
  background-color: var(--el-color-primary-light-9);
}
.list-item.selected {
  background-color: var(--el-color-primary-light-8);
  color: var(--el-color-primary);
}
</style>
