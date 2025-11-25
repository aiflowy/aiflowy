import type {
  AIFlowyFormSchema as FormSchema,
  AIFlowyFormProps,
} from '@aiflowy/common-ui';

import type { ComponentType } from './component';

import { setupAIFlowyForm, useAIFlowyForm as useForm, z } from '@aiflowy/common-ui';
import { $t } from '@aiflowy/locales';

async function initSetupAIFlowyForm() {
  setupAIFlowyForm<ComponentType>({
    config: {
      modelPropNameMap: {
        Upload: 'fileList',
        CheckboxGroup: 'model-value',
      },
    },
    defineRules: {
      required: (value, _params, ctx) => {
        if (value === undefined || value === null || value.length === 0) {
          return $t('ui.formRules.required', [ctx.label]);
        }
        return true;
      },
      selectRequired: (value, _params, ctx) => {
        if (value === undefined || value === null) {
          return $t('ui.formRules.selectRequired', [ctx.label]);
        }
        return true;
      },
    },
  });
}

const useAIFlowyForm = useForm<ComponentType>;

export { initSetupAIFlowyForm, useAIFlowyForm, z };

export type AIFlowyFormSchema = FormSchema<ComponentType>;
export type { AIFlowyFormProps };
