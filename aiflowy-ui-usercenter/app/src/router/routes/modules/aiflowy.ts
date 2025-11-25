import type { RouteRecordRaw } from 'vue-router';

import { APP_DOC_URL, APP_GITHUB_URL, APP_LOGO_URL } from '@aiflowy/constants';

import { IFrameView } from '#/layouts';
import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      badgeType: 'dot',
      icon: APP_LOGO_URL,
      order: 9998,
      title: $t('demos.aiflowy.title'),
    },
    name: 'AIFlowyProject',
    path: '/aiflowy-usercenter',
    children: [
      {
        name: 'AIFlowyDocument',
        path: '/aiflowy-usercenter/document',
        component: IFrameView,
        meta: {
          icon: 'lucide:book-open-text',
          link: APP_DOC_URL,
          title: $t('demos.aiflowy.document'),
        },
      },
      {
        name: 'AIFlowyGithub',
        path: '/aiflowy-usercenter/github',
        component: IFrameView,
        meta: {
          icon: 'mdi:github',
          link: APP_GITHUB_URL,
          title: 'Github',
        },
      },
    ],
  },
  {
    name: 'AIFlowyAbout',
    path: '/aiflowy-usercenter/about',
    component: () => import('#/views/_core/about/index.vue'),
    meta: {
      icon: 'lucide:copyright',
      title: $t('demos.aiflowy.about'),
      order: 9999,
    },
  },
  {
    name: 'Profile',
    path: '/profile',
    component: () => import('#/views/_core/profile/index.vue'),
    meta: {
      icon: 'lucide:user',
      hideInMenu: true,
      title: $t('page.auth.profile'),
    },
  },
  {
    meta: {
      title: '知识库文档管理',
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
