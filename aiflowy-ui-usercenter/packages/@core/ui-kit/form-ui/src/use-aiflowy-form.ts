import type {
  BaseFormComponentType,
  ExtendedFormApi,
  AIFlowyFormProps,
} from './types';

import { defineComponent, h, isReactive, onBeforeUnmount, watch } from 'vue';

import { useStore } from '@aiflowy-core/shared/store';

import { FormApi } from './form-api';
import AIFlowyUseForm from './aiflowy-use-form.vue';

export function useAIFlowyForm<
  T extends BaseFormComponentType = BaseFormComponentType,
>(options: AIFlowyFormProps<T>) {
  const IS_REACTIVE = isReactive(options);
  const api = new FormApi(options);
  const extendedApi: ExtendedFormApi = api as never;
  extendedApi.useStore = (selector) => {
    return useStore(api.store, selector);
  };

  const Form = defineComponent(
    (props: AIFlowyFormProps, { attrs, slots }) => {
      onBeforeUnmount(() => {
        api.unmount();
      });
      api.setState({ ...props, ...attrs });
      return () =>
        h(AIFlowyUseForm, { ...props, ...attrs, formApi: extendedApi }, slots);
    },
    {
      name: 'AIFlowyUseForm',
      inheritAttrs: false,
    },
  );
  // Add reactivity support
  if (IS_REACTIVE) {
    watch(
      () => options.schema,
      () => {
        api.setState({ schema: options.schema });
      },
      { immediate: true },
    );
  }

  return [Form, extendedApi] as const;
}
