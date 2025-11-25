<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus';

import { defineEmits, nextTick, onMounted, reactive, ref, watch } from 'vue';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElOption,
  ElSelect,
  ElSwitch,
  ElTag,
} from 'element-plus';

import { getLlmBrandList, saveLlm, updateLlm } from '#/api/ai/llm.js';
import UploadAvatar from '#/components/upload/UploadAvatar.vue';
import { $t } from '#/locales';

interface LlmFormType {
  title: string;
  brand: string;
  llmModel: string;
  icon: string;
  llmApiKey: string;
  description: string | undefined;
  supportChat: boolean;
  supportFunctionCalling: boolean;
  supportEmbed: boolean;
  supportReranker: boolean;
  supportTextToImage: boolean;
  supportImageToImage: boolean;
  supportTextToAudio: boolean;
  supportAudioToAudio: boolean;
  supportTextToVideo: boolean;
  supportImageToVideo: boolean;
  isCustomInput: boolean;
  options: {
    chatPath: string | undefined;
    embedPath: string | undefined;
    isCustomInput: boolean | undefined;
    llmEndpoint: string | undefined;
    multimodal: boolean | undefined;
  };
}
interface brandDataType {
  icon: string;
  key: string;
  title: string;
  options: {
    chatPath: string;
    embedPath: string;
    llmEndpoint: string;
    modelList: {
      description: string;
      llmModel: string;
      multimodal: boolean;
      supportChat: boolean;
      supportEmbed: boolean;
      supportFunctionCalling: boolean;
      title: string;
    }[];
  };
}

interface modalListType {
  llmModel?: string;
  title?: string;
  supportChat?: boolean;
  supportFunctionCalling?: boolean;
  multimodal?: boolean;
  supportEmbed?: boolean;
  description?: string;
  chatPath?: string;
  embedPath?: string;
  llmEndpoint?: string;
}

const emit = defineEmits(['success']);
const dialogTitle = ref($t('button.add'));
const LlmAddOrUpdateDialog = ref(false);
const llmFormRef = ref<FormInstance>();
const isAdd = ref(false);
type brandDataListType = brandDataType[];
const brandListData = ref<brandDataListType>([]);
// 是否自定义输入
const isCustomInput = ref(false);

onMounted(() => {
  getLlmBrandList().then((res) => {
    brandListData.value = res.data;
  });
});

const rules = reactive<FormRules<LlmFormType>>({
  title: [
    {
      required: true,
      message: $t('llm.llmModal.TitleRequired'),
      trigger: 'blur',
    },
  ],
  brand: [
    {
      required: true,
      message: $t('llm.llmModal.BrandRequired'),
      trigger: 'blur',
    },
  ],
  llmModel: [
    {
      required: true,
      message: $t('llm.llmModal.ModelRequired'),
      trigger: 'blur',
    },
  ],
  llmApiKey: [
    {
      required: true,
      message: $t('llm.llmModal.ApiKeyRequired'),
      trigger: 'blur',
    },
  ],
});
// 定义表单初始值（所有字段默认值，与 LlmFormType 结构对齐）
const INIT_LLM_FORM: LlmFormType = {
  title: '',
  brand: '',
  llmModel: '',
  icon: '',
  llmApiKey: '',
  description: '',
  supportChat: false,
  supportFunctionCalling: false,
  supportEmbed: false,
  supportReranker: false,
  supportTextToImage: false,
  supportImageToImage: false,
  supportTextToAudio: false,
  supportAudioToAudio: false,
  supportTextToVideo: false,
  supportImageToVideo: false,
  isCustomInput: false,
  options: {
    chatPath: undefined,
    embedPath: undefined,
    isCustomInput: false,
    llmEndpoint: undefined,
    multimodal: false,
  },
};

const llmForm = reactive<LlmFormType>({ ...INIT_LLM_FORM });

// 选择品牌时，触发的联动逻辑
const modalList = ref<modalListType[]>([]);

defineExpose({
  openAddDialog: () => {
    isAdd.value = true;
    Object.assign(llmForm, { ...INIT_LLM_FORM });
    nextTick(() => {
      llmFormRef.value?.resetFields();
    });
    dialogTitle.value = $t('button.add');
    LlmAddOrUpdateDialog.value = true;
  },
  openUpdateDialog: (editRecord: any) => {
    isAdd.value = false;
    const mergedRecord = {
      ...INIT_LLM_FORM,
      ...editRecord,
      options: {
        ...INIT_LLM_FORM.options,
        ...editRecord.options,
      },
    };
    Object.assign(llmForm, mergedRecord);
    nextTick(() => {
      llmFormRef.value?.clearValidate();
    });
    dialogTitle.value = $t('button.edit');
    LlmAddOrUpdateDialog.value = true;
    Object.assign(llmForm, editRecord);
  },
});

// 监听ruleForm.brand的变化
watch(
  () => llmForm.brand,
  async (newValue, _oldValue) => {
    if (isAdd.value && newValue) {
      llmForm.llmModel = '';
      modalList.value = [];
      llmForm.options.multimodal = false;
      llmForm.options.embedPath = '';
      llmForm.options.chatPath = '';
      llmForm.options.llmEndpoint = '';
      await nextTick();

      // 筛选当前品牌下的模型
      brandListData.value.forEach((item) => {
        if (item.key === newValue) {
          item.options.modelList.forEach((item) => {
            const tempItem: modalListType = {
              llmModel: item.llmModel,
              title: item.title,
              supportChat: item.supportChat,
              supportFunctionCalling: item.supportFunctionCalling,
              multimodal: item.multimodal,
              supportEmbed: item.supportEmbed,
              description: item.description,
            };
            modalList.value.push(tempItem);
          });
        }
      });
    }
  },
  { immediate: false },
);

// 监听ruleForm.llmModel的变化
watch(
  () => llmForm.llmModel,
  (newModel) => {
    if (
      isAdd.value &&
      newModel &&
      modalList.value.length > 0 &&
      !isCustomInput.value
    ) {
      llmForm.description = newModel;
      const tempModel: modalListType | undefined = modalList.value.find(
        (item) => item.llmModel === newModel,
      );
      brandListData.value.forEach((brand) => {
        if (brand.key === llmForm.brand) {
          llmForm.options.chatPath = brand.options.chatPath;
          llmForm.options.embedPath = brand.options.embedPath;
          llmForm.options.llmEndpoint = brand.options.llmEndpoint;
        }
      });
      if (tempModel) {
        llmForm.description = tempModel.description;
      }
    }
    // 你的逻辑：比如根据选中的模型加载配置、校验等
  },
  { immediate: false },
);

const handleConfirm = () => {
  llmFormRef.value?.validate((valid) => {
    if (valid) {
      if (isAdd.value) {
        saveLlm(JSON.stringify(llmForm)).then((res) => {
          if (res.errorCode === 0) {
            ElMessage.success($t('message.saveOkMessage'));
            LlmAddOrUpdateDialog.value = false;
            emit('success');
          }
        });
      } else {
        updateLlm(JSON.stringify(llmForm)).then((res) => {
          if (res.errorCode === 0) {
            ElMessage.success($t('message.updateOkMessage'));
            LlmAddOrUpdateDialog.value = false;
            emit('success');
          }
        });
      }
    }
  });
};
</script>

<template>
  <!--    新增大模型对话框-->
  <ElDialog
    v-model="LlmAddOrUpdateDialog"
    :title="dialogTitle"
    width="700"
    align-center
  >
    <div class="llm-dialog-container">
      <UploadAvatar v-model="llmForm.icon" />
      <ElForm
        ref="llmFormRef"
        style="width: 100%; margin-top: 20px"
        :model="llmForm"
        :rules="rules"
        label-width="auto"
      >
        <ElFormItem
          :label="$t('llm.filed.title')"
          prop="title"
          label-position="right"
        >
          <ElInput v-model="llmForm.title" />
        </ElFormItem>
        <ElFormItem
          :label="$t('llm.filed.brand')"
          prop="brand"
          label-position="right"
        >
          <ElSelect
            v-model="llmForm.brand"
            :placeholder="$t('llm.placeholder.brand')"
          >
            <ElOption
              v-for="item in brandListData"
              :key="item.key"
              :label="item.title"
              :value="item.key"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem
          :label="$t('llm.filed.llmModel')"
          prop="llmModel"
          label-position="right"
        >
          <ElSelect
            v-model="llmForm.llmModel"
            :placeholder="$t('llm.placeholder.llmModel')"
          >
            <ElOption
              v-for="item in modalList"
              :key="item.title"
              :label="item.title"
              :value="item.llmModel || ''"
            >
              <div class="model-select-container">
                <span>{{ item.title }}</span>
                <div class="tag-container">
                  <ElTag type="primary" v-if="item.supportChat">文本聊天</ElTag>
                  <ElTag type="success" v-if="item.multimodal">多模态</ElTag>
                  <ElTag type="warning" v-if="item.supportEmbed">向量化</ElTag>
                </div>
              </div>
            </ElOption>
          </ElSelect>
        </ElFormItem>
        <ElFormItem
          :label="$t('llm.filed.llmEndpoint')"
          prop="llmEndpoint"
          label-position="right"
        >
          <ElInput v-model="llmForm.options.llmEndpoint" />
        </ElFormItem>
        <ElFormItem
          :label="$t('llm.filed.llmApiKey')"
          prop="llmApiKey"
          label-position="right"
        >
          <ElInput v-model="llmForm.llmApiKey" />
        </ElFormItem>
        <ElFormItem
          :label="$t('llm.filed.chatPath')"
          prop="chatPath"
          label-position="right"
        >
          <ElInput v-model="llmForm.options.chatPath" />
        </ElFormItem>
        <ElFormItem
          :label="$t('llm.filed.embedPath')"
          prop="embedPath"
          label-position="right"
        >
          <ElInput v-model="llmForm.options.embedPath" />
        </ElFormItem>
        <ElFormItem
          :label="$t('llm.filed.description')"
          prop="description"
          label-position="right"
        >
          <ElInput
            v-model="llmForm.description"
            :rows="4"
            type="textarea"
            :placeholder="$t('llm.placeholder.description')"
          />
        </ElFormItem>
        <!--对话-->
        <ElFormItem
          :label="$t('llm.filed.supportChat')"
          prop="supportChat"
          label-position="right"
        >
          <ElSwitch v-model="llmForm.supportChat" />
        </ElFormItem>
        <!--方法调用-->
        <ElFormItem
          :label="$t('llm.filed.supportFunctionCalling')"
          prop="supportFunctionCalling"
          label-position="right"
        >
          <ElSwitch v-model="llmForm.supportFunctionCalling" />
        </ElFormItem>
        <!--向量化-->
        <ElFormItem
          :label="$t('llm.filed.supportEmbed')"
          prop="supportEmbed"
          label-position="right"
        >
          <ElSwitch v-model="llmForm.supportEmbed" />
        </ElFormItem>
        <!--重排 -->
        <ElFormItem
          :label="$t('llm.filed.supportReranker')"
          prop="supportReranker"
          label-position="right"
        >
          <ElSwitch v-model="llmForm.supportReranker" />
        </ElFormItem>
        <!--文生图-->
        <ElFormItem
          :label="$t('llm.filed.supportTextToImage')"
          prop="supportTextToImage"
          label-position="right"
        >
          <ElSwitch v-model="llmForm.supportTextToImage" />
        </ElFormItem>
        <!--图生图-->
        <ElFormItem
          :label="$t('llm.filed.supportImageToImage')"
          prop="supportImageToImage"
          label-position="right"
        >
          <ElSwitch v-model="llmForm.supportImageToImage" />
        </ElFormItem>
        <!--文生音频-->
        <ElFormItem
          :label="$t('llm.filed.supportTextToAudio')"
          prop="supportTextToAudio"
          label-position="right"
        >
          <ElSwitch v-model="llmForm.supportTextToAudio" />
        </ElFormItem>
        <!--音频转音频-->
        <ElFormItem
          :label="$t('llm.filed.supportAudioToAudio')"
          prop="supportAudioToAudio"
          label-position="right"
        >
          <ElSwitch v-model="llmForm.supportAudioToAudio" />
        </ElFormItem>
        <!--文生视频-->
        <ElFormItem
          :label="$t('llm.filed.supportTextToVideo')"
          prop="supportTextToVideo"
          label-position="right"
        >
          <ElSwitch v-model="llmForm.supportTextToVideo" />
        </ElFormItem>
        <!--图片生视频-->
        <ElFormItem
          :label="$t('llm.filed.supportImageToVideo')"
          prop="supportImageToVideo"
          label-position="right"
        >
          <ElSwitch v-model="llmForm.supportImageToVideo" />
        </ElFormItem>
        <!--多模态-->
        <ElFormItem
          :label="$t('llm.filed.multimodal')"
          prop="multimodal"
          label-position="right"
        >
          <ElSwitch v-model="llmForm.options.multimodal" />
        </ElFormItem>
      </ElForm>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <ElButton @click="LlmAddOrUpdateDialog = false">
          {{ $t('button.cancel') }}
        </ElButton>
        <ElButton type="primary" @click="handleConfirm">
          {{ $t('button.confirm') }}
        </ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<style scoped>
.llm-dialog-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.model-select-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.tag-container {
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
</style>
