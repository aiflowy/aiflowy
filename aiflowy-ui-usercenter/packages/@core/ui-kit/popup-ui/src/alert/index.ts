export type {
  AlertProps,
  BeforeCloseScope,
  IconType,
  PromptProps,
} from './alert';
export { useAlertContext } from './alert';
export { default as Alert } from './alert.vue';
export {
  aiflowyAlert as alert,
  clearAllAlerts,
  aiflowyConfirm as confirm,
  aiflowyPrompt as prompt,
} from './AlertBuilder';
