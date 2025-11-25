<script lang="ts" setup>
import { onMounted, ref } from 'vue';

import { About } from '@aiflowy/common-ui';

import { api } from '#/api/request';
import { useDictStore } from '#/store';

defineOptions({ name: 'About' });
const dictStore = useDictStore();
onMounted(() => {
  test();
  dictStore.fetchDictionary('accountType');
});
const accountInfo = ref<any>();
function test() {
  api
    .get('/api/v1/sysAccount/myProfile')
    .then((res) => {
      accountInfo.value = res.data;
      // console.log('res', res);
    })
    .catch((error) => {
      console.error('error', error);
    });
}
</script>

<template>
  <div>
    <div>{{ accountInfo?.loginName }}</div>
    <div v-for="(item, index) in [0, 1, 99]" :key="index">
      <div>
        {{ dictStore.getDictLabel('accountType', item) }}
      </div>
    </div>
    <About />
  </div>
</template>
