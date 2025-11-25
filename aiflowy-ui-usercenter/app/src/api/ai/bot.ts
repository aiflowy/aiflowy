import { api } from '#/api/request.js';

export type GetBotListParams = {
  pageNumber: number;
  pageSize: number;
};

/** 获取bot列表 */
export const getBotList = (params: GetBotListParams) => {
  return api.get('/api/v1/aiBot/page', { params });
};

/** 获取bot详情 */
export const getBotDetails = (id: string) => {
  return api.get('/api/v1/aiBot/getDetail', { params: { id } });
};
