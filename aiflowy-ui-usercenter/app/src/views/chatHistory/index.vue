<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import { Delete, MoreFilled, Search } from '@element-plus/icons-vue';
import {
  ElButton,
  ElContainer,
  ElDropdown,
  ElDropdownItem,
  ElDropdownMenu,
  ElHeader,
  ElInput,
  ElMain,
  ElMessage,
  ElSelect,
  ElSpace,
  ElText,
} from 'element-plus';
import { tryit } from 'radash';

import { api } from '#/api/request';
import PageData from '#/components/page/PageData.vue';

const listTitles = ['智能体名称', '话题', '创建时间', '操作'];

const router = useRouter();
const assistantList = ref<any[]>([]);
const queryParams = ref<any>({});
const pageRef = ref();

onMounted(() => {
  getAssistantList();
});

async function getAssistantList() {
  api
    .get('/userCenter/bot/list', {
      params: { ...queryParams.value, status: 1 },
    })
    .then((res) => {
      if (res.errorCode === 0) {
        assistantList.value = res.data.map((item: any) => ({
          label: item.title,
          value: item.id,
        }));
      }
    });
}
function search() {
  pageRef.value.setQuery({ ...queryParams.value, status: 1 });
}
function toDetail(record: any) {
  router.push({ path: `/chatHistory/${record.id}` });
}
async function handleDelete(id: string) {
  const [, res] = await tryit(api.post)('/userCenter/botConversation/remove', {
    id,
  });

  if (res && res.errorCode === 0) {
    search();
    ElMessage.success('删除成功');
  }
}
</script>

<template>
  <ElContainer class="bg-background-deep h-full">
    <ElHeader class="!h-auto !p-8 !pb-0">
      <ElSpace direction="vertical" :size="24" alignment="flex-start">
        <h1 class="text-2xl font-medium">聊天记录</h1>
        <div class="flex items-center gap-5">
          <div class="flex items-center gap-4">
            <span class="text-nowrap text-sm">智能体</span>
            <ElSelect
              clearable
              v-model="queryParams.botId"
              :options="assistantList"
              placeholder="请选择智能体"
              @change="search"
            />
          </div>
          <ElInput
            placeholder="搜索关键词"
            v-model="queryParams.title"
            @keyup.enter="search"
            @change="search"
            :prefix-icon="Search"
          />
        </div>
      </ElSpace>
    </ElHeader>
    <ElMain class="!px-8">
      <ElContainer class="bg-background rounded-lg p-5">
        <ElHeader
          class="dark:bg-accent grid grid-cols-[repeat(3,minmax(0,1fr))_120px] place-items-center rounded-lg bg-[#f7f9fd] !p-0"
          height="54px"
        >
          <span
            class="text-accent-foreground text-sm"
            v-for="title in listTitles"
            :key="title"
          >
            {{ title }}
          </span>
        </ElHeader>
        <ElMain class="!p-0">
          <div class="flex flex-col items-center gap-5">
            <div class="w-full">
              <PageData
                page-url="/userCenter/botConversation/pageList"
                ref="pageRef"
              >
                <template #default="{ pageList }">
                  <div
                    class="text-foreground/90 grid h-[60px] grid-cols-[repeat(3,minmax(0,1fr))_120px] place-items-center text-sm hover:bg-[var(--el-fill-color-light)]"
                    v-for="record in pageList"
                    :key="record.id"
                  >
                    <ElText truncated>{{ record.bot.title }}</ElText>
                    <ElText line-clamp="2">{{ record.title }}</ElText>
                    <span>{{ record.created }}</span>

                    <div class="flex items-center gap-3">
                      <ElButton
                        class="[--el-font-weight-primary:400]"
                        link
                        type="primary"
                        @click="toDetail(record)"
                      >
                        查看详情
                      </ElButton>

                      <ElDropdown>
                        <ElButton :icon="MoreFilled" link />

                        <template #dropdown>
                          <ElDropdownMenu>
                            <ElDropdownItem>
                              <ElButton
                                link
                                type="danger"
                                :icon="Delete"
                                @click="handleDelete(record.id)"
                              >
                                删除
                              </ElButton>
                            </ElDropdownItem>
                          </ElDropdownMenu>
                        </template>
                      </ElDropdown>
                    </div>
                  </div>
                </template>
              </PageData>
            </div>
          </div>
        </ElMain>
      </ElContainer>
    </ElMain>
  </ElContainer>
</template>

<style lang="css" scoped>
.el-select {
  --el-select-width: 165px;
}

.el-select.bot-select {
  --el-select-width: 343px;
}

.el-select :deep(.el-select__wrapper) {
  --el-border-radius-base: 8px;
}
</style>
