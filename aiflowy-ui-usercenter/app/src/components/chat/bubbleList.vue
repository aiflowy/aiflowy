<script setup lang="ts">
import { ref, watch } from 'vue';
import { BubbleList } from 'vue-element-plus-x';

import { CopyDocument, EditPen, RefreshRight } from '@element-plus/icons-vue';
import { ElAvatar, ElButton } from 'element-plus';

// type listType = BubbleListItemProps & {
//   key: number;
//   role: 'assistant' | 'user';
// };
// const messageList: BubbleListProps<listType>['list'] = [
//   {
//     key: 0,
//     role: 'user',
//     placement: 'end',
//     content: '哈哈哈，让我试试',
//     typing: true,
//   },
//   {
//     key: 1,
//     role: 'assistant',
//     placement: 'start',
//     content: '💖 感谢使用 Element Plus X ! 你的支持，是我们开源的最强动力 ~',
//     typing: true,
//   },
//   {
//     key: 2,
//     role: 'user',
//     placement: 'end',
//     content: '哈哈哈，让我试试',
//     typing: true,
//   },
//   {
//     key: 3,
//     role: 'assistant',
//     placement: 'start',
//     content: '💖 感谢使用 Element Plus X ! 你的支持，是我们开源的最强动力 ~',
//     loading: true,
//   },
// ];
interface Props {
  messages: any[];
}
defineProps<Props>();
const avatar = ref('https://avatars.githubusercontent.com/u/76239030?v=4');
const avartAi = ref(
  'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
);
</script>

<template>
  <BubbleList :list="messages" max-height="calc(100vh - 345px)">
    <!-- 自定义头像 -->
    <template #avatar="{ item }">
      <ElAvatar
        :src="item.role === 'assistant' ? avartAi : avatar"
        :size="40"
      />
    </template>

    <!-- 自定义头部 -->
    <template #header="{ item }">
      <span class="text-foreground/50 text-xs">{{
        item.role === 'assistant' ? '下午 2:32' : '下午 2:33'
      }}</span>
    </template>

    <!-- 自定义气泡内容 -->
    <!-- <template #content="{ item }">
    </template> -->

    <!-- 自定义底部 -->
    <template #footer="{ item }">
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
    </template>
  </BubbleList>
</template>
