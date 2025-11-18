<script setup>

import CategoryPanel from "#/components/categoryPanel/CategoryPanel.vue";
import {onMounted, ref} from "vue";
import {ElTable, ElTableColumn} from 'element-plus'
import {getLlmBrandList} from "#/api/ai/llm.js";
import HeaderSearch from "#/components/headerSearch/HeaderSearch.vue";
import { Plus, Edit, Delete, Download, Upload, Refresh } from '@element-plus/icons-vue'
import PageData from "#/components/page/PageData.vue";

const brandListData = ref([])
onMounted(() =>{
  getLlmBrandList().then(res => {
    console.log('res')
    console.log(res)
    brandListData.value = res.data
  })
})

const handleCategoryClick = (category) => {
  console.log('category')
  console.log(category.key)
}

// 按钮配置
const headerButtons = ref([
  {
    key: 'add',
    text: '新增大模型',
    icon: Plus,
    type: 'primary',
    data: { action: 'addLlm' }
  },
  {
    key: 'edit',
    text: '一键添加',
    type: 'primary',
    icon: Plus,
    data: { action: 'oneClickAdd' }
  }
])

const addLlm = () => {
  console.log('新增大模型')
}

const oneClickAdd = () => {
  console.log('一键添加')
}
// 处理搜索事件
const handleSearch = (searchValue) => {
  console.log('搜索内容:', searchValue)
  // 执行搜索逻辑
}
// 处理按钮点击事件
const handleButtonClick = (event) => {
  console.log('按钮点击事件:', event)

  // 根据按钮 key 执行不同操作
  switch (event.key) {
    case 'add':
      addLlm()
      break
    case 'edit':
      oneClickAdd()
      break
  }
}
</script>

<template>
  <div class="llm-container">
    <div class="llm-header">
      <HeaderSearch
        :buttons="headerButtons"
        search-placeholder="搜索用户"
        @search="handleSearch"
        @button-click="handleButtonClick"
      />
    </div>

    <div class="llm-content">
      <div>
        <CategoryPanel :categories="brandListData" title-key="title" :use-img-for-svg="true" :expandWidth="150" @click="handleCategoryClick"/>
      </div>
      <div class="llm-table">
        <PageData
          ref="pageDataRef"
          page-url="/api/v1/aiLlm/page"
          :page-size="10"
          :init-query-params="{ status: 1 }"
        >
          <template #default="{ pageList }">
            <el-table :data="pageList" style="width: 100%">
              <el-table-column prop="id" label="id" width="180" />
              <el-table-column prop="title" label="名称" width="180" />
            </el-table>
          </template>
        </PageData>
      </div>
    </div>


  </div>
</template>

<style scoped>
.llm-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
}
.llm-header{
  margin-bottom: 20px;
}
.llm-content{
 display: flex;
}

.llm-table{
  width: 100%;
}
</style>
