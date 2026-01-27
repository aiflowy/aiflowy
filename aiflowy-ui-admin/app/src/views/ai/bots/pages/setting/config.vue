<script setup lang="ts">
import type { AiLlm, BotInfo } from '@aiflowy/types';

import { onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';

import { $t } from '@aiflowy/locales';
import { useBotStore } from '@aiflowy/stores';

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
  ElSwitch,
} from 'element-plus';
import { tryit } from 'radash';

import {
  getPerQuestions,
  updateBotOptions,
  updateLlmId,
  updateLlmOptions,
} from '#/api';
import { api } from '#/api/request';
import ProblemPresupposition from '#/components/chat/ProblemPresupposition.vue';
import PublishWxOfficalAccount from '#/components/chat/PublishWxOfficalAccount.vue';
import CollapseViewItem from '#/components/collapseViewItem/CollapseViewItem.vue';
import CommonSelectDataModal from '#/components/commonSelectModal/CommonSelectDataModal.vue';

interface SelectedMcpTool {
  name: string;
  description: string;
}
const props = defineProps<{
  bot?: BotInfo;
  hasSavePermission?: boolean;
}>();
const botStore = useBotStore();
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
});
const dialogueSettings = ref({
  welcomeMessage: '',
  enableDeepThinking: false,
});
watch(
  props,
  (newValue) => {
    if (!newValue.hasSavePermission) {
      selectedId.value = $t('bot.noPermission');
    } else if (newValue.bot?.modelId) {
      selectedId.value = newValue.bot.modelId;
    }

    if (newValue.bot) {
      llmConfig.value = newValue.bot.modelOptions;
    }
  },
  { immediate: true },
);
const pluginToolIdsData = ref<any>([]);
const knowledgeIdsData = ref<any>([]);
const workflowIdsData = ref<any>([]);
const mcpToolData = ref<any>([]);

const workflowData = ref<any[]>([]);
const knowledgeData = ref<any[]>([]);
const pluginToolData = ref<any[]>([]);
const getAiBotPluginToolList = async () => {
  api
    .post('/api/v1/pluginItem/tool/list', { botId: botId.value })
    .then((res) => {
      pluginToolData.value = res.data;
      pluginToolIdsData.value = res.data.map((item: any) => item.id);
    });
};
const getAiBotMcpToolList = async () => {
  api.get(`/api/v1/botMcp/list?botId=${botId.value}`).then((res) => {
    mcpToolData.value = res.data;
  });
};
const getAiBotKnowledgeList = async () => {
  api
    .get('/api/v1/botKnowledge/list', {
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
      knowledgeIdsData.value = res.data.map(
        (item: any) => item.documentCollectionId,
      );
    });
};

const getAiBotWorkflowList = async () => {
  api
    .get('/api/v1/botWorkflow/list', {
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
    .get('/api/v1/bot/detail', {
      params: {
        id: botId.value,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        botInfo.value = res.data;
        if (res.data.options) {
          dialogueSettings.value = res.data.options;
        }
        if (res.data.options?.presetQuestions) {
          botStore.setPresetQuestions(res.data?.options?.presetQuestions);
        }
      }
    });
};
const getLlmListData = async () => {
  const url = `/api/v1/model/list?modelType=chatModel&added=true`;
  api.get(url, {}).then((res) => {
    if (res.errorCode === 0) {
      options.value = res.data;
    }
  });
};
onMounted(async () => {
  getAiBotPluginToolList();
  getAiBotKnowledgeList();
  getAiBotWorkflowList();
  getAiBotMcpToolList();
  getBotDetail();
  getLlmListData();
});

const handleLlmChange = async (value: string) => {
  if (!props.bot) return;

  const [, res] = await tryit(updateLlmId)({
    id: props.bot?.id || '',
    modelId: value,
  });

  if (res?.errorCode === 0) {
    ElMessage.success($t('message.saveOkMessage'));
  } else {
    ElMessage.error(res?.message || $t('message.saveFailMessage'));
  }
};
const handleLlmOptionsChange = useDebounceFn(
  (key: keyof typeof llmConfig.value, value: number) => {
    updateLlmOptions({
      id: props.bot?.id || '',
      llmOptions: {
        [key]: value,
      },
    });
  },
  300,
);

const handleDialogOptionsStrChange = useDebounceFn(
  (
    key: keyof typeof dialogueSettings.value,
    value: boolean | number | string,
  ) => {
    updateBotOptions({
      id: props.bot?.id || '',
      options: {
        [key]: value,
      },
    });
  },
  300,
);
const pluginToolDataRef = ref();
const knowledgeDataRef = ref();
const workflowDataRef = ref();
const publishWxRef = ref();
const mcpToolDataRef = ref();

const handleAddPlugin = () => {
  pluginToolDataRef.value.openDialog(pluginToolIdsData.value);
};
const handleAddMcpTool = () => {
  const formattedSelectedMcp = formatSelectedMcpData();
  mcpToolDataRef.value.openMcpDialog(formattedSelectedMcp);
};
const handleAddKnowledge = () => {
  knowledgeDataRef.value.openDialog(knowledgeIdsData.value);
};
const handleAddWorkflow = () => {
  workflowDataRef.value.openDialog(workflowIdsData.value);
};
const confirmUpdateAiBotPlugin = (data: any) => {
  api
    .post('/api/v1/botPlugins/updateBotPluginToolIds', {
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
    .post('/api/v1/botKnowledge/updateBotKnowledgeIds', {
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
    .post('/api/v1/botWorkflow/updateBotWorkflowIds', {
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
const confirmUpdateAiBotMcp = (data: any) => {
  api
    .post('/api/v1/botMcp/updateBotMcpToolIds', {
      botId: botId.value,
      mcpSelectedData: data,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.updateOkMessage'));
        getAiBotMcpToolList();
      } else {
        ElMessage.error(res.message);
      }
    });
};
const deletePluginTool = (item: any) => {
  api
    .post('/api/v1/botPlugins/doRemove', {
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

const deleteBotMcpTool = (item: any) => {
  api
    .post('/api/v1/botMcp/remove', {
      id: item.id,
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.deleteOkMessage'));
        getAiBotMcpToolList();
      } else {
        ElMessage.error(res.message);
      }
    });
};

const deleteKnowledge = (item: any) => {
  api
    .post('/api/v1/botKnowledge/remove', {
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
    .post('/api/v1/botWorkflow/remove', {
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
    botInfo.value?.options?.presetQuestions,
  );
};

const handleProblemPresuppositionSuccess = (data: any) => {
  api
    .post('/api/v1/bot/updateOptions', {
      id: botId.value,
      options: {
        presetQuestions: data,
      },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        ElMessage.success($t('message.updateOkMessage'));
        getBotDetail();
        botStore.setPresetQuestions(data);
      } else {
        ElMessage.error(res.message);
      }
    });
};
const handleDeletePresetQuestion = (item: any) => {
  const tempData = botInfo.value?.options?.presetQuestions?.filter(
    (i: any) => i.key !== item,
  );
  api
    .post('/api/v1/bot/updateOptions', {
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
    .post('/api/v1/bot/updateOptions', {
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
const formatSelectedMcpData = () => {
  // 定义格式化后的结果，与组件内selectedToolMap结构一致
  const formattedData: Record<number | string, SelectedMcpTool[]> = {};

  // 遍历已有的MCP工具列表（mcpToolData.value）
  mcpToolData.value.forEach((mcpItem: any) => {
    const mcpParentId = mcpItem.mcpId || mcpItem.id;

    const toolInfo: SelectedMcpTool = {
      name: mcpItem.mcpToolName,
      description: mcpItem.mcpToolDescription,
    };

    if (!formattedData[mcpParentId]) {
      formattedData[mcpParentId] = [];
    }
    formattedData[mcpParentId].push(toolInfo);
  });

  return formattedData;
};
</script>

<template>
  <div class="config-container flex flex-col gap-3">
    <!-- 大模型 -->
    <div
      class="bg-background dark:border-border flex flex-col gap-3 rounded-lg p-3 dark:border"
    >
      <h1 class="text-base font-medium">{{ $t('bot.llm') }}</h1>
      <div
        class="llm-back-container dark:border-border flex w-full flex-col justify-between gap-1 rounded-lg p-3 dark:border"
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
            <span class="config-font-style">{{ $t('bot.temperature') }}</span>
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
            <span class="config-font-style">{{
              $t('bot.maxReplyLength')
            }}</span>
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
            <span class="config-font-style">{{ $t('bot.historyCount') }}</span>
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
    <div
      class="bg-background dark:border-border flex flex-col gap-3 rounded-lg p-3 dark:border"
    >
      <h1 class="text-base font-medium">
        {{ $t('bot.skill') }}
      </h1>
      <div class="flex w-full flex-col justify-between">
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem>
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>{{ $t('menus.ai.workflow') }}</span>
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
          <ElCollapseItem>
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>{{ $t('menus.ai.documentCollection') }}</span>
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
          <ElCollapseItem>
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>{{ $t('menus.ai.plugin') }}</span>
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
          <ElCollapseItem title="MCP">
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>MCP</span>
                <div class="collapse-right-container">
                  <span class="badge-circle">
                    {{ mcpToolData.length }}
                  </span>
                  <span @click="handleAddMcpTool()">
                    <ElIcon>
                      <Plus />
                    </ElIcon>
                  </span>
                </div>
              </div>
            </template>
            <CollapseViewItem
              :data="mcpToolData"
              title-key="mcpToolName"
              description-key="mcpToolDescription"
              @delete="deleteBotMcpTool"
            />
          </ElCollapseItem>
        </ElCollapse>
      </div>
    </div>

    <!-- 对话设置 -->
    <div
      class="bg-background dark:border-border flex flex-col gap-3 rounded-lg p-3 dark:border"
    >
      <h1 class="text-base font-medium">
        {{ $t('bot.conversationSettings') }}
      </h1>
      <div class="flex w-full flex-col justify-between rounded-lg">
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem>
            <template #title>
              <div class="flex items-center justify-between pr-2">
                <span>{{ $t('bot.problemPresupposition') }}</span>
                <span @click="handleAddPresetQuestion()">
                  <ElIcon>
                    <Plus />
                  </ElIcon>
                </span>
              </div>
            </template>
            <div class="question-container">
              <div
                v-for="item in getPerQuestions(botStore?.presetQuestions)"
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
          <ElCollapseItem :title="$t('bot.welcomeMessage')">
            <div class="bg-[var(--bot-collapse-itme-back)] p-2.5">
              <ElInput
                v-model="dialogueSettings.welcomeMessage"
                :placeholder="$t('bot.placeholder.welcome')"
                type="textarea"
                @change="
                  (value) =>
                    handleDialogOptionsStrChange('welcomeMessage', value)
                "
              />
            </div>
          </ElCollapseItem>
          <ElCollapseItem :title="$t('bot.deepThinking')">
            <div class="enable-think-container">
              <div>{{ $t('bot.enableDeepThinking') }}</div>
              <ElSwitch
                v-model="dialogueSettings.enableDeepThinking"
                @change="
                  (value: string | number | boolean) =>
                    handleDialogOptionsStrChange('enableDeepThinking', value)
                "
              />
            </div>
          </ElCollapseItem>
        </ElCollapse>
      </div>
    </div>

    <!-- 发布 -->
    <div
      class="publish-container bg-background dark:border-border flex flex-col gap-3 rounded-lg p-3 dark:border"
    >
      <h1 class="text-base font-medium">
        {{ $t('bot.publish') }}
      </h1>
      <div class="flex w-full flex-col justify-between rounded-lg">
        <ElCollapse expand-icon-position="left">
          <ElCollapseItem :title="$t('bot.postToWeChatOfficialAccount')">
            <div class="publish-wx-container">
              <div class="publish-wx">
                <span v-if="botInfo?.options?.weChatMpAppId">{{
                  $t('bot.configured')
                }}</span>
                <span v-else>{{ $t('bot.notConfigured') }}</span>
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
                    v-if="botInfo?.options?.weChatMpAppId"
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
            </div>
          </ElCollapseItem>
        </ElCollapse>
      </div>
    </div>

    <!-- 选择插件-->
    <CommonSelectDataModal
      :title="$t('menus.ai.plugin')"
      width="730"
      ref="pluginToolDataRef"
      page-url="/api/v1/plugin/pageByCategory"
      :has-parent="true"
      @get-data="confirmUpdateAiBotPlugin"
      :extra-query-params="{
        category: 0,
      }"
    />

    <!-- 选择知识库-->
    <CommonSelectDataModal
      :title="$t('menus.ai.documentCollection')"
      width="730"
      ref="knowledgeDataRef"
      page-url="/api/v1/documentCollection/page"
      @get-data="confirmUpdateAiBotKnowledge"
    />

    <!-- 选择工作流-->
    <CommonSelectDataModal
      :title="$t('menus.ai.workflow')"
      width="730"
      ref="workflowDataRef"
      page-url="/api/v1/workflow/page"
      @get-data="confirmUpdateAiBotWorkflow"
    />

    <!-- 选择MCP-->
    <CommonSelectDataModal
      :title="$t('menus.ai.mcp')"
      width="730"
      ref="mcpToolDataRef"
      page-url="/api/v1/mcp/pageTools?status=1"
      title-key="title"
      :has-parent="true"
      :is-select-mcp="true"
      @get-data="confirmUpdateAiBotMcp"
    />

    <!--预设问题-->
    <ProblemPresupposition
      ref="problemPresuppositionRef"
      @success="handleProblemPresuppositionSuccess"
    />
    <PublishWxOfficalAccount ref="publishWxRef" @reload="getBotDetail()" />
  </div>
</template>

<style scoped>
.config-container {
  width: 100%;
  height: 100%;
  min-height: 100%;
  overflow-y: auto;
  border-radius: 8px;
}

.collapse-right-container {
  display: flex;
  flex-direction: row;
  gap: 5px;
  align-items: center;
}

.badge-circle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  font-size: 12px;
  font-weight: 500;
  line-height: 1;
  vertical-align: middle;
  color: var(--el-color-white);
  background-color: var(--el-color-primary);
  border-radius: 50%;
}

.presetQues-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 24px 12px 12px;
  background-color: var(--el-bg-color);
  border-radius: 8px;
}

.preset-delete {
  cursor: pointer;
}

.config-font-style {
  font-family: PingFangSC, 'PingFang SC';
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
  color: var(--el-text-color-primary);
}

.options-config-item-left {
  flex: 0 0 auto;
}

:deep(.el-collapse-item__title) {
  font-size: 14px;
  font-weight: 500;
  line-height: 22px;
  color: var(--el-text-color-primary);
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

.enable-think-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 12px 12px;
  background-color: var(--bot-collapse-itme-back);
}

.publish-wx-container {
  padding: 12px;
  background-color: var(--bot-collapse-itme-back);
}

.publish-wx {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 45px;
  padding: 0 8px;
  background-color: var(--el-bg-color);
  border-radius: 8px;
}

.publish-wx-right-container {
  display: flex;
  gap: 5px;
  align-items: center;
  justify-content: flex-end;
  cursor: pointer;
}

.publish-config-operation {
  display: flex;
  gap: 5px;
  align-items: center;
  justify-content: flex-end;
}

:deep(.el-collapse) {
  box-sizing: border-box;
  height: 100%;
  overflow: hidden !important;
  border-radius: 8px !important;
}

:deep(.el-collapse-item__header) {
  background-color: var(--bot-collapse-itme-back);
  border-bottom: 1px solid #e4e7ed;
  border-radius: 0;
}

.dark .el-collapse :deep(.el-collapse-item__header) {
  border-bottom: 1px solid hsl(var(--border));
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
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  height: auto;
  padding: 10px;
  background-color: var(--bot-collapse-itme-back);
}
</style>
