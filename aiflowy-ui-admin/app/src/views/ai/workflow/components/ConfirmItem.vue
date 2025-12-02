<script setup lang="ts">
import { defineEmits, defineProps } from 'vue';

import { Download } from '@element-plus/icons-vue';
import { ElIcon, ElText } from 'element-plus';

import confirmFile from '#/assets/ai/workflow/confirm-file.png';
// 导入你的图片资源
// 请确保路径正确，或者将图片放在 public 目录下引用
import confirmOther from '#/assets/ai/workflow/confirm-other.png';

// 定义 Props
const props = defineProps({
  // v-model 绑定值
  modelValue: {
    type: [String, Number, Object],
    default: null,
  },
  // 数据类型: text, image, video, audio, other, file
  selectionDataType: {
    type: String,
    default: 'text',
  },
  // 数据列表
  selectionData: {
    type: Array as () => any[],
    default: () => [],
  },
});

// 定义 Emits
const emit = defineEmits(['update:modelValue', 'change']);

// 判断是否选中
const isSelected = (item: any) => {
  return props.modelValue === item;
};

// 切换选中状态
const changeValue = (item: any) => {
  if (props.modelValue === item) {
    // 如果点击已选中的，则取消选中
    emit('update:modelValue', null);
    emit('change', null); // 触发 Element Plus 表单验证
  } else {
    emit('update:modelValue', item);
    emit('change', item); // 触发 Element Plus 表单验证
  }
};

// 获取图标
const getIcon = (type: string) => {
  return type === 'other' ? confirmOther : confirmFile;
};

// 下载处理
const handleDownload = (url: string) => {
  window.open(url, '_blank');
};
</script>

<template>
  <div class="custom-radio-group">
    <template v-for="(item, index) in selectionData" :key="index">
      <!-- 类型: Text -->
      <div
        v-if="selectionDataType === 'text'"
        class="custom-radio-option"
        :class="{ selected: isSelected(item) }"
        style="width: 100%; flex-shrink: 0"
        @click="changeValue(item)"
      >
        {{ item }}
      </div>

      <!-- 类型: Image -->
      <div
        v-else-if="selectionDataType === 'image'"
        class="custom-radio-option"
        :class="{ selected: isSelected(item) }"
        style="padding: 0"
        @click="changeValue(item)"
      >
        <img
          :src="item"
          alt=""
          style="width: 80px; height: 80px; border-radius: 8px; display: block"
        />
      </div>

      <!-- 类型: Video -->
      <div
        v-else-if="selectionDataType === 'video'"
        class="custom-radio-option"
        :class="{ selected: isSelected(item) }"
        @click="changeValue(item)"
      >
        <video controls :src="item" style="width: 162px; height: 141px"></video>
      </div>

      <!-- 类型: Audio -->
      <div
        v-else-if="selectionDataType === 'audio'"
        class="custom-radio-option"
        :class="{ selected: isSelected(item) }"
        style="width: 100%; flex-shrink: 0"
        @click="changeValue(item)"
      >
        <audio
          controls
          :src="item"
          style="width: 100%; height: 44px; margin-top: 8px"
        ></audio>
      </div>

      <!-- 类型: File 或 Other -->
      <div
        v-else-if="
          selectionDataType === 'other' || selectionDataType === 'file'
        "
        class="custom-radio-option"
        :class="{ selected: isSelected(item) }"
        style="width: 100%; flex-shrink: 0"
        @click="changeValue(item)"
      >
        <div
          style="
            display: flex;
            justify-content: space-between;
            align-items: center;
          "
        >
          <div style="width: 92%; display: flex; align-items: center">
            <img
              style="width: 20px; height: 20px; margin-right: 8px"
              alt=""
              :src="getIcon(selectionDataType)"
            />
            <!-- 使用 Element Plus 的 Text 组件处理省略号，如果没有安装 Element Plus，可以用普通的 span + css -->
            <ElText truncated>
              {{ item }}
            </ElText>
          </div>
          <div class="download-icon-btn" @click.stop="handleDownload(item)">
            <ElIcon><Download /></ElIcon>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.custom-radio-group {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.custom-radio-option {
  background-color: var(--el-bg-color);
  padding: 8px;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  box-shadow: 0 0 0 1px var(--el-border-color);
  transition: all 0.2s;
  box-sizing: border-box; /* 确保 padding 不会撑大宽度 */
}

.custom-radio-option:hover {
  box-shadow: 0 0 0 1px var(--el-color-primary-light-5);
}

.custom-radio-option.selected {
  box-shadow: 0 0 0 1px var(--el-color-primary-light-3);
  padding: 8px;
  background: var(--el-color-primary-light-9);
}

.custom-radio-option.selected::after {
  content: '';
  /* 这里保留了原来的 base64 图片 */
  background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACoAAAAoCAYAAACIC2hQAAAAAXNSR0IArs4c6QAAAnpJREFUWEfN2FuITHEcwPHvb5Jcsm/IJSVJ8YDkkrQKMSUraZUHRZzZ8aDWA7UPSimKWvGiWVrywJPQrry6PIyQywMPitrUUopYW5Nmz09nZsoxjTn//7k55/l3+cz/nN//f+YIttcR7aBCHtgOLEKYC8wBptiWsoh/KcbBji4jxym0BpxsnBdPoAG0qLNwOQPsB3Lx9LWuEgB1dAXCXWCBdel4E9pAHe1CuAlMi7dnqGr/gBZ1DS4PEx4QG3ELaFHn4fKsMck2xZKMbQEt6HBj60mysW3tJqijGxEe2FZJIb4JWtAnwNoUGtu28EF7dCnKG9sKKcX7oI72IZxOqbFtm79WtIyyzrZClPiTO+DQBjh+C248bVvJBy3od6AjSmOb3PN7oHdzPePOK9h1yQS6T6czlZ82jaLE+pFjFdjUD89HTKA9uhjlXZTmprnNyG0XoPwhMLtx64u6EpcXgeERA0Iiva7pQSMg04NGRKYDjQFpB719GJbMhoPXjR7+2tMcE9IcOnMGfD4HOQFvOzGZ1BiR5lAv8uxuOLa1PvZB2JiRdlDTW5kA0h4ahE0IGQ7aCpu/CN2r/pzdQY9GiHMj/IbvX72qC5Ma//gTQHq/61H9S0nII9SPNRmyECtZT1GuRYJ6Nfq74egW+FGBvNkLhr1XORAZ6nVdPh8+foOv4/YGo4wcC2OBGjULH1RmQNZnH+rSyRV5nHXoEAPS5d2MLENHqbKaQRnNMrSC0sll8b6B1a4srug4wl5KMuSfv6xBRxB2UpLXzZtEVqAT3ulDlT6uypdWO9n/hv4ChhFOUJK37bbaNKEV4BOKN8XvgXtMcJ9BGTM5C34DIlpE2qbZk5wAAAAASUVORK5CYII=);
  background-size: cover;
  width: 16px;
  height: 16px;
  position: absolute;
  right: 0;
  bottom: 0;
}
.download-icon-btn {
  font-size: 18px;
  cursor: pointer;
  margin-right: 10px;
  display: flex; /* 为了对齐图标 */
  align-items: center;
}
</style>
