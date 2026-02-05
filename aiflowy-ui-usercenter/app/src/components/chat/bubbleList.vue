<script setup lang="ts">
import { IconifyIcon } from '@aiflowy/icons';
import { useUserStore } from '@aiflowy/stores';

import { CircleCheck } from '@element-plus/icons-vue';
import { ElAvatar, ElCollapse, ElCollapseItem, ElIcon } from 'element-plus';

import defaultAssistantAvatar from '#/assets/defaultAssistantAvatar.svg';
import defaultUserAvatar from '#/assets/defaultUserAvatar.png';
import ShowJson from '#/components/json/ShowJson.vue';

interface Props {
  bot: any;
  messages: any[];
}
const props = defineProps<Props>();
const store = useUserStore();

function getAssistantAvatar() {
  return props.bot.icon || defaultAssistantAvatar;
}
function getUserAvatar() {
  return store.userInfo?.avatar || defaultUserAvatar;
}
</script>

<template>
  <ElBubbleList :list="messages" max-height="calc(100vh - 345px)">
    <!-- 自定义头像 -->
    <template #avatar="{ item }">
      <ElAvatar
        :src="
          item.role === 'assistant' ? getAssistantAvatar() : getUserAvatar()
        "
        :size="40"
      />
    </template>

    <!-- 自定义头部 -->
    <template #header="{ item }">
      <div class="flex flex-col">
        <span class="text-foreground/50 text-xs">
          {{ item.created }}
        </span>

        <template v-if="item.chains">
          <template
            v-for="(chain, index) in item.chains"
            :key="chain.id || index"
          >
            <ElThinking
              v-if="!('id' in chain)"
              v-model="chain.thinlCollapse"
              :content="chain.reasoning_content"
              :status="chain.thinkingStatus"
            />
            <ElCollapse v-else class="mb-2">
              <ElCollapseItem :title="chain.name" :name="chain.id">
                <template #title>
                  <div class="flex items-center gap-2 pl-5">
                    <ElIcon size="16">
                      <IconifyIcon icon="svg:wrench" />
                    </ElIcon>
                    <span>{{ chain.name }}</span>
                    <template v-if="chain.status === 'TOOL_CALL'">
                      <div
                        class="bg-secondary flex items-center gap-1 rounded-full px-2 py-0.5 leading-none"
                      >
                        <ElIcon size="16">
                          <IconifyIcon icon="mdi:clock-time-five-outline" />
                        </ElIcon>
                        <span>工具调用中...</span>
                      </div>
                    </template>
                    <template v-else>
                      <div
                        class="bg-secondary flex items-center gap-1 rounded-full px-2 py-0.5 leading-none"
                      >
                        <ElIcon size="16" color="var(--el-color-success)">
                          <CircleCheck />
                        </ElIcon>
                        <span>调用成功</span>
                      </div>
                    </template>
                  </div>
                </template>
                <ShowJson :value="chain.result" />
              </ElCollapseItem>
            </ElCollapse>
          </template>
        </template>

        <!-- <ElThinking
          v-if="item.reasoning_content"
          v-model="item.thinlCollapse"
          :content="item.reasoning_content"
          :status="item.thinkingStatus"
        /> -->
        <!-- <ElCollapse v-if="item.tools" class="mb-2">
          <ElCollapseItem
            class="mb-2"
            v-for="tool in item.tools"
            :key="tool.id"
            :title="tool.name"
            :name="tool.id"
          >
            <template #title>
              <div class="flex items-center gap-2 pl-5">
                <ElIcon size="16">
                  <IconifyIcon icon="svg:wrench" />
                </ElIcon>
                <span>{{ tool.name }}</span>
                <template v-if="tool.status === 'TOOL_CALL'">
                  <ElIcon size="16">
                    <IconifyIcon icon="svg:spinner" />
                  </ElIcon>
                </template>
                <template v-else>
                  <ElIcon size="16" color="var(--el-color-success)">
                    <CircleCheck />
                  </ElIcon>
                </template>
              </div>
            </template>
            <ShowJson :value="tool.result" />
          </ElCollapseItem>
        </ElCollapse> -->
      </div>
    </template>

    <!-- 自定义气泡内容 -->
    <template #content="{ item }">
      <ElXMarkdown :markdown="item.content" />
    </template>

    <!-- 自定义底部 -->
    <!--<template #footer="{ item }">
      <div class="flex items-center">
        <template v-if="item.role === 'assistant'">
          <ElButton :icon="RefreshRight" link />
          <ElButton :icon="CopyDocument" link />
        </template>
        <template v-else>
          <ElButton :icon="CopyDocument" link />
          <ElButton :icon="EditPen" link />
        </template>
      </div>
    </template>-->
  </ElBubbleList>
</template>

<style lang="css" scoped>
:deep(.el-bubble-header) {
  width: 100%;
}

:deep(.el-bubble-end .el-bubble-header) {
  width: fit-content;
}

:deep(.el-bubble-content-wrapper .el-bubble-content) {
  --bubble-content-max-width: 100%;
}

:deep(.el-thinking) {
  margin: 0;
}

:deep(.el-thinking .content-wrapper) {
  --el-thinking-content-wrapper-width: 100%;

  margin-bottom: 8px;
}

:deep(.el-collapse) {
  overflow: hidden;
  border: 1px solid var(--el-collapse-border-color);
  border-radius: 8px;
}

:deep(.el-collapse-item__content) {
  padding-bottom: 0;
}
</style>
