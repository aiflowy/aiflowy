import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'clarity:database',
      title: $t('datacenterTable.title'),
      hideInMenu: true,
      hideInTab: true,
      hideInBreadcrumb: true,
    },
    name: 'TableDetail',
    path: '/tableDetail',
    component: () => import('#/views/datacenter/DatacenterTableDetail.vue'),
  },
];

export default routes;
