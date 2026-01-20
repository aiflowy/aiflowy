<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import { Profile } from '@aiflowy/common-ui';
import { useUserStore } from '@aiflowy/stores';

import { $t } from '#/locales';

import ProfileBase from './base-setting.vue';
import ProfileNotificationSetting from './notification-setting.vue';
import ProfilePasswordSetting from './password-setting.vue';
import ProfileSecuritySetting from './security-setting.vue';

const route = useRoute();
const userStore = useUserStore();

const tabsValue = ref<string>('basic');

const tabs = [
  {
    label: $t('settingsConfig.basic'),
    value: 'basic',
  },
  {
    label: $t('settingsConfig.updatePwd'),
    value: 'password',
  },
];

onMounted(() => {
  if (route.query.tab) {
    tabsValue.value = route.query.tab as string;
  }
});
</script>
<template>
  <Profile
    v-model:model-value="tabsValue"
    :title="$t('page.auth.profile')"
    :user-info="userStore.userInfo"
    :tabs="tabs"
  >
    <template #content>
      <ProfileBase v-if="tabsValue === 'basic'" />
      <ProfileSecuritySetting v-if="tabsValue === 'security'" />
      <ProfilePasswordSetting v-if="tabsValue === 'password'" />
      <ProfileNotificationSetting v-if="tabsValue === 'notice'" />
    </template>
  </Profile>
</template>
