// import type { RouteRecordStringComponent } from '@aiflowy/types';

// import { requestClient } from '#/api/request';
import { h } from 'vue';

import BotPlazaIcon from '#/components/icons/IconBotPlaza.vue';
import CollectIcon from '#/components/icons/IconCollect.vue';
import HistoryIcon from '#/components/icons/IconHistory.vue';
import HomeIcon from '#/components/icons/IconHome.vue';
import UniversalAssistantIcon from '#/components/icons/IconUniversalAssistant.vue';

/**
 * 获取用户所有菜单
 */
export async function getAllMenusApi() {
  // return requestClient.get<RouteRecordStringComponent[]>(
  //   '/api/v1/sysMenu/treeV2',
  // );
  return [
    {
      component: '',
      meta: {
        icon: h(HomeIcon),
        order: 100,
        title: '首页',
      },
      name: 'Home',
      path: '/',
    },
    {
      component: '',
      meta: {
        icon: h(UniversalAssistantIcon),
        title: '通用助手',
      },
      name: 'UniversalAssistant',
      path: '/universalAssistant',
    },
    {
      component: '',
      meta: {
        icon: h(BotPlazaIcon),
        title: '智能体广场',
      },
      name: 'BotPlaza',
      path: '/botPlaza',
    },
    {
      component: '',
      meta: {
        icon: h(CollectIcon),
        title: '我的收藏',
      },
      name: 'MyCollection',
      path: '/myCollection',
    },
    {
      component: '',
      meta: {
        icon: h(HistoryIcon),
        title: '历史记录',
      },
      name: 'History',
      path: '/history',
    },
  ];
}
