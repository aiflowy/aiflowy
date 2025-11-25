import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'clarity:time-line',
      title: $t('sysJobLog.title'),
      hideInMenu: true,
      hideInTab: true,
      hideInBreadcrumb: true,
    },
    name: 'SysJobLog',
    path: '/sysJobLog',
    component: () => import('#/views/system/sysJob/SysJobLogList.vue'),
  },
];

export default routes;
