<template>
  <div style="width: 300px; height: 100%">
    <CategoryPanel
      icon-key="icon1"
      title-key="name"
      :categories="categoryData"
      @click="handleCategoryClick"
      @panel-toggle="handlePanelToggle"
    />
  </div>

</template>

<script setup>
import CategoryPanel from "#/components/categoryPanel/CategoryPanel.vue";
import { User, Message, Setting } from '@element-plus/icons-vue'

// 示例分类数据
const categoryData = [
  { name: '用户管理', icon1: User },
  { name: '消息中心', icon1: Message },
  { name: '系统设置', icon1: Setting },
]

// 处理分类点击
const handleCategoryClick = (category) => {
  console.log('点击分类:', category)
}

// 处理面板收缩状态改变
const handlePanelToggle = (collapsed) => {
  console.log('面板状态:', collapsed ? '已收缩' : '已展开')
}
</script>
