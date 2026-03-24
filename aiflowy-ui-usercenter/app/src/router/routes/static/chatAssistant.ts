import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    name: 'ChatAssistant',
    path: '/chatAssistant',
    component: () => import('#/views/chatAssistant/index.vue'),
    meta: {
      icon: 'svg:chat-assistant',
      order: 22,
      title: '我的智能体',
    },
  },
];

export default routes;
