<script setup>
import {
  computed,
  defineEmits,
  defineProps,
  isVNode,
  onMounted,
  ref,
} from 'vue';

import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue';
import { ElIcon } from 'element-plus';

// 定义组件属性
const props = defineProps({
  // 分类数据，格式示例：[{ name: '分类1', icon: SomeIcon }, { name: '分类2' }]
  categories: {
    type: Array,
    default: () => [],
    required: true,
  },
  valueKey: {
    type: String,
    default: undefined,
  },
  titleKey: {
    type: String,
    default: 'name',
  },
  needHideCollapse: {
    type: Boolean,
    default: false,
  },
  iconKey: {
    type: String,
    default: 'icon',
  },
  // 自定义展开状态宽度（默认300px）
  expandWidth: {
    type: Number,
    default: 120,
  },
  // 自定义收缩状态宽度（默认48px）
  collapseWidth: {
    type: Number,
    default: 48,
  },
  // 默认选中的分类（用于初始化） 指定key
  defaultSelectedCategory: {
    type: String,
    default: null,
  },
  iconSize: { type: [Number, String], default: 18 },
  iconColor: { type: String, default: 'var(--el-text-color-primary)' },
  // 新增：是否用 img 标签渲染 SVG 字符串（默认 false）
  useImgForSvg: { type: Boolean, default: false },
});

// 定义事件
const emit = defineEmits([
  'click', // 分类项点击事件
  'panelToggle', // 面板收缩状态改变事件
]);

const finalValueKey = computed(() => {
  // 父组件传递了 valueKey 就用 valueKey，否则用 titleKey
  return props.valueKey ?? props.titleKey;
});

// -------------------------- 核心工具函数 --------------------------
/**
 * SVG 字符串转 Data URL（供 img 标签使用）
 * @param {string} svgString - 清理后的 SVG 字符串
 * @returns {string} Data URL
 */
const svgToDataUrl = (svgString) => {
  // 1. 去除 SVG 中的换行和多余空格（优化编码后体积）
  const cleanedSvg = svgString
    .replaceAll('\n', '')
    .replaceAll(/\s+/g, ' ')
    .trim();
  // 2. URL 编码 + 拼接 Data URL 格式
  return `data:image/svg+xml;utf8,${encodeURIComponent(cleanedSvg)}`;
};

/**
 * 判断是否为组件（Element Plus 图标 / 自定义 SVG 组件）
 */
const isComponent = (icon) => {
  return (
    typeof icon === 'object' && (typeof icon === 'object' || isVNode(icon))
  );
};

/**
 * 判断是否为 SVG 字符串
 */
const isSvgString = (icon) => {
  return typeof icon === 'string' && icon.trim().startsWith('<svg');
};

/**
 * 判断是否为图片 URL
 */
const isImageUrl = (icon) => {
  return (
    typeof icon === 'string' &&
    (icon.endsWith('.svg') ||
      icon.endsWith('.png') ||
      icon.endsWith('.jpg') ||
      icon.startsWith('http://') ||
      icon.startsWith('https://'))
  );
};

// 面板收缩状态
const isCollapsed = ref(false);

// 检查是否有分类包含图标
const hasIcons = computed(() => {
  return props.categories.some((item) => item[props.iconKey]);
});

// 动态计算面板宽度
const panelWidth = computed(() => {
  if (isCollapsed.value) {
    // 收缩状态：有图标用自定义收缩宽度，无图标保持最小适配宽度
    return hasIcons.value ? props.collapseWidth : 120;
  } else {
    // 展开状态：使用自定义展开宽度
    return props.expandWidth;
  }
});

// 切换面板收缩状态
const togglePanel = () => {
  isCollapsed.value = !isCollapsed.value;
  emit('panelToggle', {
    collapsed: isCollapsed.value,
    currentWidth: panelWidth.value,
  });
};
const selectedCategory = ref(null);
// 处理分类项点击
const handleCategoryClick = (category) => {
  // 选中值：用 finalValueKey（父组件指定的字段）
  selectedCategory.value = category[finalValueKey.value];
  emit('click', {
    item: category,
    value: category[finalValueKey.value], // 父组件指定字段的值
    label: category[props.titleKey], // 分类名称
  });
};

onMounted(() => {
  // 初始化时，检查是否有默认选中的分类
  if (props.defaultSelectedCategory) {
    selectedCategory.value = props.defaultSelectedCategory;
  }
});
</script>

<template>
  <div class="category-panel" :style="{ width: `${panelWidth}px` }">
    <!-- 右上角收缩/展开按钮 -->
    <div class="toggle-panel-btn" @click="togglePanel" v-if="!needHideCollapse">
      <ElIcon>
        <ArrowLeft v-if="!isCollapsed" />
        <ArrowRight v-else />
      </ElIcon>
    </div>
    <div style="margin-bottom: 48px" v-if="!needHideCollapse"></div>
    <!-- 分类列表容器 -->
    <div class="category-list" :class="{ collapsed: isCollapsed }">
      <!-- 遍历一级分类数据 -->
      <div
        v-for="(category, index) in categories"
        :key="index"
        class="category-item"
      >
        <div
          class="category-item-content"
          :class="{ selected: selectedCategory === category[finalValueKey] }"
          @click="handleCategoryClick(category)"
        >
          <!-- 图标 -->
          <div v-if="category[iconKey]" class="category-icon">
            <!-- 1. 组件类型图标（Element Plus / 自定义 SVG 组件） -->
            <ElIcon v-if="isComponent(category[iconKey])">
              <component :is="category[iconKey]" />
            </ElIcon>
            <!-- 2. SVG 字符串：支持 v-html 或 img 两种渲染方式 -->
            <template v-else-if="isSvgString(category[iconKey])">
              <div
                v-if="!useImgForSvg"
                v-html="category[iconKey]"
                class="custom-svg"
              ></div>
              <img
                v-else
                :src="svgToDataUrl(category[iconKey])"
                :alt="category[titleKey]"
                class="svg-image"
              />
            </template>
            <!-- 3. 图片 URL（本地/网络 SVG/PNG） -->
            <img
              v-else-if="isImageUrl(category[iconKey])"
              :src="category[iconKey]"
              :alt="category[titleKey]"
              class="svg-image"
            />
          </div>

          <!-- 分类名称（收缩状态且有图标时隐藏文字） -->
          <span
            class="category-name"
            :class="{ hidden: isCollapsed && category[iconKey] }"
          >
            {{ category[titleKey] }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.category-panel {
  position: relative; /* 相对定位，用于按钮绝对定位 */
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
  height: 100%;
  transition: width 0.3s cubic-bezier(0.4, 0, 0.2, 1); /* 平滑宽度过渡 */
  box-sizing: border-box;
  background-color: var(--el-bg-color);
}

/* 右上角收缩/展开按钮 */
.toggle-panel-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 10; /* 确保按钮在最上层 */
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: var(--el-color-white);
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.2s;
  /* 按钮不随面板收缩移动 */
  transform: translateX(0);
}

.toggle-panel-btn:hover {
  background-color: #f3f4f6;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.toggle-panel-btn .el-icon {
  width: 16px;
  height: 16px;
  color: #666;
}

/* 分类列表容器 */
.category-list {
  overflow: hidden;
  height: 100%;
  box-sizing: border-box;
}

.category-item {
}

.category-item:last-child {
  border-bottom: none;
}

.category-item-content {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  transition: background-color 0.2s;
  gap: 12px;
}

.category-item-content:hover {
  background-color: #f9fafb;
}

.category-icon {
  width: v-bind(iconSize);
  height: v-bind(iconSize);
  color: v-bind(iconColor);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
}

.category-icon .el-icon {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.category-item-content {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
  cursor: pointer;
  transition: background-color 0.2s;
  gap: 12px;
}

.category-name {
  transition:
    opacity 0.2s,
    transform 0.2s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: var(--el-text-color-primary);
}

/* 收缩状态样式 */
.hidden {
  display: none;
}

.collapsed .category-item-content {
  justify-content: center;
  padding: 12px 0;
}

/* 收缩状态下文字强制隐藏（避免无图标时文字溢出） */
.collapsed .category-name {
  display: none;
}

/* 新增：选中态样式 */
.category-item-content.selected {
  background-color: var(--el-color-primary-light-9);
  color: var(--el-text-color-primary);
  font-weight: 600;
}

.category-item-content.selected:hover {
  background-color: var(--el-color-primary-light-9);
}
</style>
