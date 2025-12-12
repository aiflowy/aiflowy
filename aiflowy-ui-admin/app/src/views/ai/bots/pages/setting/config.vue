<script setup lang="ts">
import type { AiLlm, BotInfo } from '@aiflowy/types';

import { onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

import { $t } from '@aiflowy/locales';
import { tryit } from '@aiflowy/utils';

import { Delete, Plus, Setting } from '@element-plus/icons-vue';
import { useDebounceFn } from '@vueuse/core';
import {
  ElButton,
  ElCol,
  ElCollapse,
  ElCollapseItem,
  ElIcon,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElRow,
  ElSelect,
  ElSlider,
} from 'element-plus';

import { getAiLlmList, updateLlmId, updateLlmOptions } from '#/api';
import { api } from '#/api/request';
import ProblemPresupposition from '#/components/chat/ProblemPresupposition.vue';
import PublishWxOfficalAccount from '#/components/chat/PublishWxOfficalAccount.vue';
import CollapseViewItem from '#/components/collapseViewItem/CollapseViewItem.vue';
import CommonSelectDataModal from '#/components/commonSelectModal/CommonSelectDataModal.vue';

const props = defineProps<{
  bot?: BotInfo;
  hasSavePermission?: boolean;
}>();
const route = useRoute();
const botId = ref<string>((route.params.id as string) || '');
const options = ref<AiLlm[]>([]);
const selectedId = ref<string>('');
const llmConfig = ref({
  maxMessageCount: 0,
  maxReplyLength: 0,
  temperature: 0,
  topK: 0,
  topP: 0,
  welcomeMessage: '',
});

watch(
  props,
  (newValue) => {
    if (!newValue.hasSavePermission) {
      selectedId.value = '没有权限';
    } else if (newValue.bot?.llmId) {
      selectedId.value = newValue.bot.llmId;
    }

    if (newValue.bot) {
      llmConfig.value = newValue.bot.llmOptions;
    }
  },
  { immediate: true },
);
const pluginToolIdsData = ref<any>([]);
const knowledgeIdsData = ref<any>([]);
const workflowIdsData = ref<any>([]);

const workflowData = ref<any[]>([]);
const knowledgeData = ref<any[]>([]);
const pluginToolData = ref<any[]>([]);
const getAiBotPluginToolList = async () => {
  api
    .post('/api/v1/aiPluginTool/tool/list', { botId: botId.value })
    .then((res) => {
      pluginToolData.value = res.data;
      pluginToolIdsData.value = res.data.map((item: any) => item.id);
    });
};
const getAiBotKnowledgeList = async () => {
  api
    .get('/api/v1/aiBotKnowledge/list', {
      params: {
        botId: botId.value,
      },
    })
    .then((res) => {
      knowledgeData.value = res.data.map((item: any) => {
        return {
          recordId: item.id,
          ...item.knowledge,
        };
      });
      knowledgeIdsData.value = res.data.map((item: any) => item.knowledgeId);
    });
};

const getAiBotWorkflowList = async () => {
  api
    .get('/api/v1/aiBotWorkflow/list', {
      params: {
        botId: botId.value,
      },
    })
    .then((res) => {
      workflowData.value = res.data.map((item: any) => {
        return {
          recordId: item.id,
          ...item.workflow,
        };
      });
      workflowIdsData.value = res.data.map((item: any) => item.workflowId);
    });
};
const botInfo = ref<BotInfo>();
const getBotDetail = async () => {
  api
    .get('/api/v1/aiBot/detail', {
      params: {
        id: botId.value,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        botInfo.value = res.data;
      }
    });
};
onMounted(async () => {
  const [, res] = await tryit(getAiLlmList({ supportFunctionCalling: true }));
  getAiBotPluginToolList();
  getAiBotKnowledgeList();
  getAiBotWorkflowList();
  getBotDetail();
  if (res?.errorCode === 0) {
    options.value = res.data;
  }
});

const handleLlmChange = async (value: string) => {
  if (!props.bot) return;

  const [, res] = await tryit(
    updateLlmId({
      id: props.bot?.id || '',
      llmId: value,
    }),
  );

  if (res?.errorCode === 0) {
    ElMessage.success('保存成功');
  } else {
    ElMessage.error(res?.message || '保存失败');
  }
};
const handleLlmOptionsChange = useDebounceFn(
  (key: keyof typeof llmConfig.value, value: number) => {
    const _value = handleInvalidNumber(value);

    llmConfig.value.temperature = _value;
    updateLlmOptions({
      id: props.bot?.id || '',
      llmOptions: {
        [key]: _value,
      },
    });
  },
  300,
);

const handleLlmOptionsStrChange = useDebounceFn(
  (key: keyof typeof llmConfig.value, value: string) => {
    updateLlmOptions({
      id: props.bot?.id || '',
      llmOptions: {
        [key]: value,
      },
    });
  },
  300,
);

/** 处理不合规数值 */
const handleInvalidNumber = (
  value: number,
  min = 0.1,
  max = 1,
  decimal = 1,
) => {
  if (Number.isNaN(value) || value < min) {
    return min;
  } else if (value > max) {
    return max;
  }

  if (value.toString().includes('.')) {
    const factor = 10 ** decimal;
    return Math.floor(value * factor) / factor;
  }

  return value;
};
const pluginToolDataRef = ref();
const knowledgeDataRef = ref();
const workflowDataRef = ref();
const publishWxRef = ref();

const handleAddPlugin = () => {
  pluginToolDataRef.value.openDialog(pluginToolIdsData.value);
};
const handleAddKnowledge = () => {
  knowledgeDataRef.value.openDialog(knowledgeIdsData.value);
};
const handleAddWorkflow = () => {
  workflowDataRef.value.openDialog(workflowIdsData.value);
};
const confirmUpdateAiBotPlugin = (data: any) => {
  api
    .post('/api/v1/aiBotPlugins/updateBotPluginToolIds', {
      botId: botId.value,
      pluginToolIds: data,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.updateOkMessage'));
        getAiBotPluginToolList();
      } else {
        ElMessage.error(res.message);
      }
    });
};

const confirmUpdateAiBotKnowledge = (data: any) => {
  api
    .post('/api/v1/aiBotKnowledge/updateBotKnowledgeIds', {
      botId: botId.value,
      knowledgeIds: data,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.updateOkMessage'));
        getAiBotKnowledgeList();
      } else {
        ElMessage.error(res.message);
      }
    });
};

const confirmUpdateAiBotWorkflow = (data: any) => {
  api
    .post('/api/v1/aiBotWorkflow/updateBotWorkflowIds', {
      botId: botId.value,
      workflowIds: data,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.updateOkMessage'));
        getAiBotWorkflowList();
      } else {
        ElMessage.error(res.message);
      }
    });
};
const deletePluginTool = (item: any) => {
  api
    .post('/api/v1/aiBotPlugins/doRemove', {
      botId: botId.value,
      pluginToolId: item.id,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.deleteOkMessage'));
        getAiBotPluginToolList();
      } else {
        ElMessage.error(res.message);
      }
    });
};

const deleteKnowledge = (item: any) => {
  api
    .post('/api/v1/aiBotKnowledge/remove', {
      id: item.recordId,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.deleteOkMessage'));
        getAiBotKnowledgeList();
      } else {
        ElMessage.error(res.message);
      }
    });
};

const deleteWorkflow = (item: any) => {
  api
    .post('/api/v1/aiBotWorkflow/remove', {
      id: item.recordId,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.deleteOkMessage'));
        getAiBotWorkflowList();
      } else {
        ElMessage.error(res.message);
      }
    });
};

const problemPresuppositionRef = ref();
const handleAddPresetQuestion = () => {
  problemPresuppositionRef.value.openDialog(
    botInfo.value?.options.presetQuestions,
  );
};

const handleProblemPresuppositionSuccess = (data: any) => {
  api
    .post('/api/v1/aiBot/updateOptions', {
      id: botId.value,
      options: {
        presetQuestions: data,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.updateOkMessage'));
        getBotDetail();
      } else {
        ElMessage.error(res.message);
      }
    });
};
const handleDeletePresetQuestion = (item: any) => {
  const tempData = botInfo.value?.options.presetQuestions.filter(
    (i: any) => i.key !== item,
  );
  api
    .post('/api/v1/aiBot/updateOptions', {
      id: botId.value,
      options: {
        presetQuestions: tempData,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        getBotDetail();
      }
    });
};
const handlePublishWx = () => {
  publishWxRef.value.openDialog(botId.value, botInfo.value?.options);
};
const handleUpdatePublishWx = () => {
  api
    .post('/api/v1/aiBot/updateOptions', {
      id: botId.value,
      options: {
        weChatMpAppId: '',
        weChatMpSecret: '',
        weChatMpToken: '',
        EncodingAESKey: '',
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.updateOkMessage'));
        getBotDetail();
      } else {
        ElMessage.error(res.message);
      }
    });
};
</script>

<template>
  <div class="config-container flex flex-col gap-3">
    <!-- 大模型 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">大模型</h1>
      <div
        class="llm-back-container flex w-full flex-col justify-between gap-1 rounded-lg p-3"
      >
        <ElSelect
          v-model="selectedId"
          :options="options"
          :props="{ value: 'id', label: 'title' }"
          :disabled="!hasSavePermission"
          @change="handleLlmChange"
        />
        <!-- 温度 -->
        <ElRow :gutter="12" align="middle" type="flex">
          <ElCol class="options-config-item-left">
            <span class="config-font-style">温度</span>
          </ElCol>
          <ElCol class="options-config-item-middle">
            <ElSlider
              :min="0.1"
              :max="1"
              :step="0.1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.temperature"
              @change="
                (value) =>
                  handleLlmOptionsChange('temperature', value as number)
              "
            />
          </ElCol>
          <ElCol class="options-config-item-right">
            <ElInputNumber
              :min="0.1"
              :max="1"
              :step="0.1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.temperature"
              @change="
                (value) =>
                  handleLlmOptionsChange('temperature', value as number)
              "
            />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle" type="flex">
          <ElCol class="options-config-item-left">
            <span class="config-font-style">TopK</span>
          </ElCol>
          <ElCol class="options-config-item-middle">
            <ElSlider
              :min="1"
              :max="10"
              :step="1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.topK"
              @change="
                (value) => handleLlmOptionsChange('topK', value as number)
              "
            />
          </ElCol>
          <ElCol class="options-config-item-right">
            <ElInputNumber
              :min="1"
              :max="10"
              :step="1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.topK"
              @change="
                (value) => handleLlmOptionsChange('topK', value as number)
              "
            />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle" type="flex">
          <ElCol class="options-config-item-left">
            <span class="config-font-style">TopP</span>
          </ElCol>
          <ElCol class="options-config-item-middle">
            <ElSlider
              :min="0.1"
              :max="1"
              :step="0.1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.topP"
              @change="
                (value) => handleLlmOptionsChange('topP', value as number)
              "
            />
          </ElCol>
          <ElCol class="options-config-item-right">
            <ElInputNumber
              :min="0.1"
              :max="1"
              :step="0.1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.topP"
              @change="
                (value) => handleLlmOptionsChange('topP', value as number)
              "
            />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle" type="flex">
          <ElCol class="options-config-item-left">
            <span class="config-font-style">最大回复长度</span>
          </ElCol>
          <ElCol class="options-config-item-middle">
            <ElSlider
              :min="1024"
              :max="20000"
              :step="1000"
              :disabled="!hasSavePermission"
              v-model="llmConfig.maxReplyLength"
              @change="
                (value) =>
                  handleLlmOptionsChange('maxReplyLength', value as number)
              "
            />
          </ElCol>
          <ElCol class="options-config-item-right">
            <ElInputNumber
              :min="1024"
              :max="20000"
              :step="1000"
              :disabled="!hasSavePermission"
              v-model="llmConfig.maxReplyLength"
              @change="
                (value) =>
                  handleLlmOptionsChange('maxReplyLength', value as number)
              "
            />
          </ElCol>
        </ElRow>
        <ElRow :gutter="12" align="middle" type="flex">
          <ElCol class="options-config-item-left">
            <span class="config-font-style">携带历史条数</span>
          </ElCol>
          <ElCol class="options-config-item-middle">
            <ElSlider
              :min="1"
              :max="100"
              :step="10"
              :disabled="!hasSavePermission"
              v-model="llmConfig.maxMessageCount"
              @change="
                (value) =>
                  handleLlmOptionsChange('maxMessageCount', value as number)
              "
            />
          </ElCol>
          <ElCol class="options-config-item-right">
            <ElInputNumber
              :min="1"
              :max="100"
              :step="1"
              :disabled="!hasSavePermission"
              v-model="llmConfig.maxMessageCount"
              @change="
                (value) =>
                  handleLlmOptionsChange('maxMessageCount', value as number)
              "
            />
          </ElCol>
        </ElRow>
      </div>
    </div>

    <!-- 技能 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">技能</h1>
      <div class="flex w-full flex-col justify-between">
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="工作流">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>工作流</span>
                <div class="collapse-right-container">
                  <span class="badge-circle">
                    {{ workflowData.length }}
                  </span>
                  <span @click="handleAddWorkflow()">
                    <ElIcon>
                      <Plus />
                    </ElIcon>
                  </span>
                </div>
              </div>
            </template>
            <CollapseViewItem :data="workflowData" @delete="deleteWorkflow" />
          </ElCollapseItem>
          <ElCollapseItem title="知识库">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>知识库</span>
                <div class="collapse-right-container">
                  <span class="badge-circle">
                    {{ knowledgeData.length }}
                  </span>
                  <span @click="handleAddKnowledge()">
                    <ElIcon>
                      <Plus />
                    </ElIcon>
                  </span>
                </div>
              </div>
            </template>
            <CollapseViewItem :data="knowledgeData" @delete="deleteKnowledge" />
          </ElCollapseItem>
          <ElCollapseItem title="插件">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>插件</span>
                <div class="collapse-right-container">
                  <span class="badge-circle">
                    {{ pluginToolData.length }}
                  </span>
                  <span @click="handleAddPlugin()">
                    <ElIcon>
                      <Plus />
                    </ElIcon>
                  </span>
                </div>
              </div>
            </template>
            <CollapseViewItem
              :data="pluginToolData"
              title-key="name"
              @delete="deletePluginTool"
            />
          </ElCollapseItem>
        </ElCollapse>
      </div>
    </div>

    <!-- 对话设置 -->
    <div class="flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">对话设置</h1>
      <div class="flex w-full flex-col justify-between rounded-lg">
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="问题预设">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>问题预设</span>
                <span @click="handleAddPresetQuestion()">
                  <ElIcon>
                    <Plus />
                  </ElIcon>
                </span>
              </div>
            </template>
            <div class="question-container">
              <div
                v-for="item in botInfo?.options?.presetQuestions"
                :key="item.key"
              >
                <div class="presetQues-container" v-if="item.description">
                  <span>{{ item.description }}</span>
                  <ElIcon
                    color="var(--el-color-danger)"
                    size="20px"
                    @click="handleDeletePresetQuestion(item.key)"
                    class="el-list-item-delete-container"
                  >
                    <Delete />
                  </ElIcon>
                </div>
              </div>
            </div>
          </ElCollapseItem>
          <ElCollapseItem title="欢迎语">
            <div>
              <ElInput
                v-model="llmConfig.welcomeMessage"
                placeholder="请输入欢迎语"
                type="textarea"
                @change="
                  (value) => handleLlmOptionsStrChange('welcomeMessage', value)
                "
              />
            </div>
          </ElCollapseItem>
        </ElCollapse>
      </div>
    </div>

    <!-- 发布 -->
    <div class="publish-container flex flex-col gap-3 rounded-lg bg-white p-3">
      <h1 class="text-base font-medium text-[#1A1A1A]">发布</h1>
      <div class="flex w-full flex-col justify-between rounded-lg">
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem title="发布到微信公众号">
            <div class="publish-wx">
              <span v-if="botInfo?.options.weChatMpAppId">已配置</span>
              <span v-else>未配置</span>
              <div class="publish-config-operation">
                <div
                  class="publish-wx-right-container"
                  @click="handlePublishWx"
                >
                  <ElButton link type="primary">
                    <ElIcon class="mr-1">
                      <Setting />
                    </ElIcon>
                    {{ $t('button.edit') }}
                  </ElButton>
                </div>
                <div
                  v-if="botInfo?.options.weChatMpAppId"
                  class="publish-wx-right-container"
                  @click="handleUpdatePublishWx"
                >
                  <ElButton link type="danger">
                    <ElIcon class="mr-1">
                      <Delete />
                    </ElIcon>
                    {{ $t('button.delete') }}
                  </ElButton>
                </div>
              </div>
            </div>
          </ElCollapseItem>
        </ElCollapse>
      </div>
    </div>

    <!-- 选择插件-->
    <CommonSelectDataModal
      width="730"
      ref="pluginToolDataRef"
      page-url="/api/v1/aiPlugin/pageByCategory"
      :is-select-plugin="true"
      @get-data="confirmUpdateAiBotPlugin"
      :extra-query-params="{
        category: 0,
      }"
    />

    <!-- 选择知识库-->
    <CommonSelectDataModal
      width="730"
      ref="knowledgeDataRef"
      page-url="/api/v1/aiKnowledge/page"
      @get-data="confirmUpdateAiBotKnowledge"
    />

    <!-- 选择工作流-->
    <CommonSelectDataModal
      width="730"
      ref="workflowDataRef"
      page-url="/api/v1/aiWorkflow/page"
      @get-data="confirmUpdateAiBotWorkflow"
    />

    <!--预设问题-->
    <ProblemPresupposition
      ref="problemPresuppositionRef"
      @success="handleProblemPresuppositionSuccess"
    />
    <PublishWxOfficalAccount ref="publishWxRef" />
  </div>
</template>

<style scoped>
.config-container {
  height: 100%;
  min-height: 100%;
  overflow-y: auto;
  width: 100%;
  border-radius: 8px;
}
.collapse-right-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 5px;
}
.badge-circle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  color: var(--el-color-white);
  background-color: var(--el-color-primary);
  font-size: 12px;
  font-weight: 500;
  line-height: 1;
  vertical-align: middle;
}
.presetQues-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--el-bg-color);
  padding: 12px 24px 12px 12px;
  border-radius: 8px;
}
.preset-delete {
  cursor: pointer;
}

.config-font-style {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  font-family:
    PingFangSC,
    PingFang SC;
  line-height: 22px;
}
.options-config-item-left {
  flex: 0 0 auto;
}

:deep(.el-collapse-item__title) {
  font-size: 14px;
  font-weight: 500;
  color: var(--el-text-color-primary);
  line-height: 22px;
}
.options-config-item-middle {
  flex: 1;
  margin: 0 12px;
}
.options-config-item-right {
  flex: 0 0 auto;
  width: auto;
}
:deep(.el-slider__runway) {
  height: 4px;
}
:deep(.el-slider__bar) {
  height: 4px;
}
:deep(.el-slider__button) {
  width: 14px;
  height: 14px;
}
.publish-wx {
  padding: 0 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.publish-wx-right-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}
.publish-config-operation {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 5px;
}
:deep(.el-collapse-item__header) {
  background-color: var(--bot-collapse-itme-back);
}
:deep(.el-collapse) {
  border-radius: 8px !important;
  overflow: hidden !important;
  box-sizing: border-box;
}

:deep(.el-collapse-item__header) {
  border-bottom: 1px solid #e4e7ed;
  border-radius: 0;
}

:deep(.el-collapse-item:last-child .el-collapse-item__header) {
  border-bottom: none;
}
:deep(.el-collapse-icon-position-left .el-collapse-item__header) {
  padding: 8px;
}
.llm-back-container {
  background-color: var(--bot-collapse-itme-back);
}
.publish-container {
  flex: 1;
}
.el-list-item-delete-container {
  cursor: pointer;
}
:deep(.el-collapse-item__content) {
  height: 100%;
  padding: 0;
}

.question-container {
  background-color: #f5f5f5 !important;
  width: 100%;
  min-height: 120px;
  height: auto;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

:deep(.el-collapse) {
  height: 100%;
}
</style>
