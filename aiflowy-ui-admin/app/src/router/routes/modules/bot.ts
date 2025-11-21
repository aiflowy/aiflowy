import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    name: 'BotRun',
    path: '/ai/bots/run/:id',
    component: () => import('#/views/ai/bots/Run.vue'),
    meta: {
      title: 'bot',
      noBasicLayout: true,
      openInNewWindow: true,
      hideInMenu: true,
      hideInBreadcrumb: true,
      hideInTab: true,
    },
  },
];

export default routes;
