import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      title: $t('plugin.toolsManagement'),
      hideInMenu: true,
      hideInTab: true,
      hideInBreadcrumb: true,
      fullPathKey: true,
    },
    name: 'PluginTools',
    path: '/ai/plugin/tools',
    component: () => import('#/views/ai/plugin/PluginTools.vue'),
  },
  {
    meta: {
      title: $t('plugin.toolsManagement'),
      hideInMenu: true,
      hideInTab: true,
      hideInBreadcrumb: true,
      fullPathKey: true,
    },
    name: 'PluginToolEdit',
    path: '/ai/plugin/tool/edit',
    component: () => import('#/views/ai/plugin/PluginToolEdit.vue'),
  },
];

export default routes;
