declare module '#/components/page/PageData.vue' {
  export { default } from '@/components/page/PageData.vue';
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue';

  const component: DefineComponent<object, object, any>;
  export default component;
}
