declare module '#/components/page/PageData.vue' {
  import type { DefineComponent } from 'vue';

  interface PageDataSlots {
    default: (props: { pageList: any[] }) => any;
  }

  const component: DefineComponent<object, object, PageDataSlots>;
  export default component;
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue';

  const component: DefineComponent<object, object, any>;
  export default component;
}
