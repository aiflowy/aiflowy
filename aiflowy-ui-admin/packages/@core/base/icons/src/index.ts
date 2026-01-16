import antDesign from '@iconify/json/json/ant-design.json';
import ep from '@iconify/json/json/ep.json';
import {
  addCollection,
  addIcon,
  Icon as IconifyIcon,
  listIcons,
} from '@iconify/vue';

addCollection(antDesign);
addCollection(ep);

export * from './create-icon';

export * from './lucide';

export type { IconifyIcon as IconifyIconStructure } from '@iconify/vue';
export { addCollection, addIcon, IconifyIcon, listIcons };
