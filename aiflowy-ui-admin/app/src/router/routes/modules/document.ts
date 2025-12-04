import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      title: $t('aiKnowledge.documentManagement'),
      hideInMenu: true,
      hideInTab: true,
      hideInBreadcrumb: true,
      fullPathKey: true,
      activePath: '/ai/knowledge',
    },
    name: 'Document',
    path: '/ai/knowledge/document',
    component: () => import('#/views/ai/knowledge/Document.vue'),
  },
];

export default routes;
