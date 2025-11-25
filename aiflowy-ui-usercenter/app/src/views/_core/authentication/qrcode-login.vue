<script lang="ts" setup>
import { AuthenticationQrCodeLogin } from '@aiflowy/common-ui';
import { LOGIN_PATH } from '@aiflowy/constants';

defineOptions({ name: 'QrCodeLogin' });
</script>

<template>
  <AuthenticationQrCodeLogin :login-path="LOGIN_PATH" />
</template>
