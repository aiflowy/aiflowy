import type { UserInfo } from '@aiflowy/types';

import { requestClient } from '#/api/request';

/**
 * 获取用户信息
 */
export async function getUserInfoApi() {
  return requestClient.get<UserInfo>('/api/v1/sysAccount/myProfile');
}
