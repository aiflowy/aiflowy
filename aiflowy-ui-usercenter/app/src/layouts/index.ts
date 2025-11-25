const BasicLayout = () => import('./basic.vue');
const AuthPageLayout = () => import('./auth.vue');

const IFrameView = () => import('@aiflowy/layouts').then((m) => m.IFrameView);

export { AuthPageLayout, BasicLayout, IFrameView };
