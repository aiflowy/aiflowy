import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    name: 'BotRun',
    path: '/ai/bots/run/:botId/:sessionId?',
    component: () => import('#/views/ai/bots/pages/Run.vue'),
    meta: {
      title: $t('menus.ai.bots'),
      noBasicLayout: true,
      openInNewWindow: true,
      hideInMenu: true,
      hideInBreadcrumb: true,
      hideInTab: true,
    },
  },
  {
    name: 'BotSetting',
    path: '/ai/bots/setting/:id',
    component: () => import('#/views/ai/bots/pages/setting/index.vue'),
    meta: {
      title: $t('menus.ai.bots'),
      openInNewWindow: true,
      hideInMenu: true,
      hideInBreadcrumb: true,
      hideInTab: true,
      activePath: '/ai/bots',
    },
  },
];

export default routes;
