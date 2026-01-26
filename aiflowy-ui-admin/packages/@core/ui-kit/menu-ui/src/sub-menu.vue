<script setup lang="ts">
import type { MenuRecordRaw } from '@aiflowy-core/typings';

import { computed } from 'vue';

import { cn } from '@aiflowy-core/shared/utils';

import { MenuItem } from './components';
// eslint-disable-next-line import/no-self-import
import SubMenu from './sub-menu.vue';

interface Props {
  collapse?: boolean;
  /**
   * 菜单项
   */
  menu: MenuRecordRaw;
}

defineOptions({
  name: 'SubMenuUi',
});

const props = withDefaults(defineProps<Props>(), {
  collapse: false,
});

/**
 * 判断是否有子节点，动态渲染 menu-item/sub-menu-item
 */
const hasChildren = computed(() => {
  const { menu } = props;

  return (
    Reflect.has(menu, 'children') && !!menu.children && menu.children.length > 0
  );
});
</script>

<template>
  <MenuItem
    v-if="!hasChildren"
    :key="menu.path"
    :active-icon="menu.activeIcon"
    :badge="menu.badge"
    :badge-type="menu.badgeType"
    :badge-variants="menu.badgeVariants"
    :icon="menu.icon"
    :path="menu.path"
  >
    <template #title>
      <span>{{ menu.name }}</span>
    </template>
  </MenuItem>
  <!-- 扁平化菜单 -->
  <div v-else :class="cn('flex flex-col gap-3', collapse && 'm-0')">
    <span :class="cn('mt-4 pl-4 text-sm text-[#C7C7C7]', collapse && 'hidden')">
      {{ menu.name }}
    </span>
    <div>
      <template v-for="childItem in menu.children || []" :key="childItem.path">
        <SubMenu :menu="childItem" :collapse="collapse" />
      </template>
    </div>
  </div>
  <!-- 收缩菜单 -->
  <!-- <SubMenuComp
    v-else
    :key="`${menu.path}_sub`"
    :active-icon="menu.activeIcon"
    :icon="menu.icon"
    :path="menu.path"
  >
    <template #content>
      <MenuBadge
        :badge="menu.badge"
        :badge-type="menu.badgeType"
        :badge-variants="menu.badgeVariants"
        class="right-6"
      />
    </template>
    <template #title>
      <span>{{ menu.name }}</span>
    </template>
    <template v-for="childItem in menu.children || []" :key="childItem.path">
      <SubMenu :menu="childItem" />
    </template>
  </SubMenuComp> -->
</template>
