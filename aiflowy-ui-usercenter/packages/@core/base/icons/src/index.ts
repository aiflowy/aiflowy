import mdi from '@iconify/json/json/mdi.json';
import {
  addCollection,
  addIcon,
  Icon as IconifyIcon,
  listIcons,
} from '@iconify/vue';

addCollection(mdi);

export * from './create-icon';

export * from './lucide';

export type { IconifyIcon as IconifyIconStructure } from '@iconify/vue';
export { addCollection, addIcon, IconifyIcon, listIcons };
