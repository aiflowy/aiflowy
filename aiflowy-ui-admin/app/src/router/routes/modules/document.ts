import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      title: $t('documentCollection.documentManagement'),
      hideInMenu: true,
      hideInTab: true,
      hideInBreadcrumb: true,
      fullPathKey: true,
      activePath: '/ai/documentCollection',
    },
    name: 'Document',
    path: '/ai/documentCollection/document',
    component: () => import('#/views/ai/documentCollection/Document.vue'),
  },
];

export default routes;
