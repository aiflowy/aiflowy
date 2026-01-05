<script setup lang="ts">
import type { Component } from 'vue';

import type { BuiltinThemePreset } from '@aiflowy/preferences';
import type {
  BuiltinThemeType,
  LayoutType,
  ThemeModeType,
} from '@aiflowy/types';

import { computed, nextTick, ref } from 'vue';

import {
  CircleHelp,
  MoonStar,
  Sun,
  SunMoon,
  UserRoundPen,
} from '@aiflowy/icons';
import {
  FullContent,
  HeaderMixedNav,
  HeaderNav,
  HeaderSidebarNav,
  MixedNav,
  SidebarMixedNav,
  SidebarNav,
} from '@aiflowy/layouts';
import {
  BUILT_IN_THEME_PRESETS,
  preferences,
  updatePreferences,
} from '@aiflowy/preferences';
import { convertToHsl, TinyColor } from '@aiflowy/utils';

import { AIFlowyTooltip } from '@aiflowy-core/shadcn-ui';

import { useThrottleFn } from '@vueuse/core';
import {
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElSwitch,
} from 'element-plus';
import { tryit } from 'radash';

import { api } from '#/api/request';
import Cropper from '#/components/upload/Cropper.vue';
import { $t } from '#/locales';

interface PresetItem {
  name: string;
  tip: string;
  type: LayoutType;
}

const colorInput = ref();
const loading = ref(false);

const THEME_PRESET: Array<{ icon: Component; name: ThemeModeType }> = [
  {
    icon: Sun,
    name: 'light',
  },
  {
    icon: MoonStar,
    name: 'dark',
  },
  {
    icon: SunMoon,
    name: 'auto',
  },
];
const builtinThemePresets = computed(() => {
  return [...BUILT_IN_THEME_PRESETS];
});
const inputValue = computed(() => {
  return new TinyColor(preferences.theme.colorPrimary || '').toHexString();
});
const PRESET = computed((): PresetItem[] => [
  {
    name: $t('preferences.vertical'),
    tip: $t('preferences.verticalTip'),
    type: 'sidebar-nav',
  },
  {
    name: $t('preferences.horizontal'),
    tip: $t('preferences.horizontalTip'),
    type: 'header-nav',
  },
  {
    name: $t('preferences.mixedMenu'),
    tip: $t('preferences.mixedMenuTip'),
    type: 'mixed-nav',
  },
]);
const components: Record<LayoutType, Component> = {
  'full-content': FullContent,
  'header-nav': HeaderNav,
  'mixed-nav': MixedNav,
  'sidebar-mixed-nav': SidebarMixedNav,
  'sidebar-nav': SidebarNav,
  'header-mixed-nav': HeaderMixedNav,
  'header-sidebar-nav': HeaderSidebarNav,
};
const transitionPreset = ['fade', 'fade-slide', 'fade-up', 'fade-down'];

const updateThemeColorPrimary = useThrottleFn(
  (value: string) => {
    updatePreferences({
      theme: {
        colorPrimary: value,
      },
    });
  },
  300,
  true,
  true,
);
const updateAuthText = useThrottleFn(
  (value: string, key: string) => {
    updatePreferences({
      auth: {
        [key]: value,
      },
    });
  },
  300,
  true,
  true,
);

function nameView(name: string) {
  switch (name) {
    case 'auto': {
      return $t('preferences.followSystem');
    }
    case 'dark': {
      return $t('preferences.theme.dark');
    }
    case 'light': {
      return $t('preferences.theme.light');
    }
  }
}
function typeView(name: BuiltinThemeType) {
  switch (name) {
    case 'custom': {
      return $t('preferences.theme.builtin.custom');
    }
    case 'deep-blue': {
      return $t('preferences.theme.builtin.deepBlue');
    }
    case 'deep-green': {
      return $t('preferences.theme.builtin.deepGreen');
    }
    case 'default': {
      return $t('preferences.theme.builtin.default');
    }
    case 'gray': {
      return $t('preferences.theme.builtin.gray');
    }
    case 'green': {
      return $t('preferences.theme.builtin.green');
    }

    case 'neutral': {
      return $t('preferences.theme.builtin.neutral');
    }
    case 'orange': {
      return $t('preferences.theme.builtin.orange');
    }
    case 'pink': {
      return $t('preferences.theme.builtin.pink');
    }
    case 'rose': {
      return $t('preferences.theme.builtin.rose');
    }
    case 'sky-blue': {
      return $t('preferences.theme.builtin.skyBlue');
    }
    case 'slate': {
      return $t('preferences.theme.builtin.slate');
    }
    case 'violet': {
      return $t('preferences.theme.builtin.violet');
    }
    case 'yellow': {
      return $t('preferences.theme.builtin.yellow');
    }
    case 'zinc': {
      return $t('preferences.theme.builtin.zinc');
    }
  }
}
function handleSelectThemeColor(theme: BuiltinThemePreset) {
  updatePreferences({
    theme: {
      builtinType: theme.type,
      colorPrimary: theme.color,
    },
  });
}
function selectColor() {
  colorInput.value?.[0]?.click?.();
}
function handleInputChange(e: Event) {
  const target = e.target as HTMLInputElement;
  updateThemeColorPrimary(convertToHsl(target.value));
}
async function handleSubmit() {
  loading.value = true;
  const [, res] = await tryit(api.post)('/api/v1/sysOption/saveOption', {
    key: 'ui_config',
    value: preferences,
  });

  if (res && res.errorCode === 0) {
    ElMessage.success($t('message.saveOkMessage'));
  }
  loading.value = false;
}
</script>

<template>
  <div class="mx-auto flex w-full max-w-[900px] flex-col gap-4 p-5">
    <!-- 主题与配色 -->
    <div
      class="bg-background border-border flex flex-col gap-6 rounded-lg border p-5 pb-6"
    >
      <div class="text-base font-medium">
        {{ $t('sysAppearance.Theme and Color Scheme') }}
      </div>
      <!-- 主题模式 -->
      <div class="flex flex-col gap-2.5">
        <span class="text-sm font-medium">{{
          $t('sysAppearance.Theme Mode')
        }}</span>
        <div class="flex items-center gap-5">
          <template v-for="themeItem in THEME_PRESET" :key="themeItem.name">
            <div
              class="flex cursor-pointer flex-col"
              @click="updatePreferences({ theme: { mode: themeItem.name } })"
            >
              <div
                :class="{
                  'outline-box-active':
                    themeItem.name === preferences.theme.mode,
                }"
                class="outline-box flex-center py-4"
              >
                <component :is="themeItem.icon" class="mx-9 size-5" />
              </div>
              <div class="text-muted-foreground mt-2 text-center text-xs">
                {{ nameView(themeItem.name) }}
              </div>
            </div>
          </template>
        </div>
      </div>

      <!-- 主题色 -->
      <div class="flex flex-col gap-2.5">
        <span class="text-sm font-medium">{{
          $t('sysAppearance.Theme Color')
        }}</span>
        <div class="flex flex-wrap items-center gap-5">
          <template
            v-for="themeItem in builtinThemePresets"
            :key="themeItem.type"
          >
            <div
              class="flex cursor-pointer flex-col"
              @click="handleSelectThemeColor(themeItem)"
            >
              <div
                :class="{
                  'outline-box-active':
                    themeItem.type === preferences.theme.builtinType,
                }"
                class="outline-box flex-center group cursor-pointer"
              >
                <template v-if="themeItem.type !== 'custom'">
                  <div
                    :style="{ backgroundColor: themeItem.color }"
                    class="mx-9 my-2 size-5 rounded-md"
                  ></div>
                </template>
                <template v-else>
                  <div class="size-full px-9 py-2" @click.stop="selectColor">
                    <div class="flex-center relative size-5 rounded-sm">
                      <UserRoundPen
                        class="z-1 absolute size-5 opacity-60 group-hover:opacity-100"
                      />
                      <input
                        ref="colorInput"
                        :value="inputValue"
                        class="absolute inset-0 opacity-0"
                        type="color"
                        @input="handleInputChange"
                      />
                    </div>
                  </div>
                </template>
              </div>
              <div class="text-muted-foreground my-2 text-center text-xs">
                {{ typeView(themeItem.type) }}
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- 布局与导航 -->
    <div
      class="bg-background border-border flex flex-col gap-6 rounded-lg border p-5 pb-6"
    >
      <div class="text-base font-medium">
        {{ $t('sysAppearance.Layout & Navigation') }}
      </div>
      <!-- 布局模式 -->
      <div class="flex flex-col gap-2.5">
        <span class="text-sm font-medium">{{
          $t('sysAppearance.Layout Mode')
        }}</span>
        <div class="flex items-center gap-5">
          <template v-for="themeItem in PRESET" :key="themeItem.name">
            <div
              class="flex w-[100px] cursor-pointer flex-col"
              @click="updatePreferences({ app: { layout: themeItem.type } })"
            >
              <div
                :class="{
                  'outline-box-active':
                    themeItem.type === preferences.app.layout,
                }"
                class="outline-box flex-center"
              >
                <component :is="components[themeItem.type]" />
              </div>
              <div
                class="text-muted-foreground flex-center hover:text-foreground mt-2 text-center text-xs"
              >
                {{ themeItem.name }}
                <AIFlowyTooltip v-if="themeItem.tip" side="bottom">
                  <template #trigger>
                    <CircleHelp class="ml-1 size-3 cursor-help" />
                  </template>
                  {{ themeItem.tip }}
                </AIFlowyTooltip>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- 界面显示 -->
    <div
      class="bg-background border-border flex flex-col gap-6 rounded-lg border p-5 pb-6"
    >
      <div class="text-base font-medium">
        {{ $t('sysAppearance.Interface Display') }}
      </div>
      <!-- 页面标签页 -->
      <div class="flex flex-col gap-2.5">
        <span class="text-sm font-medium">{{
          $t('sysAppearance.Page Tabs')
        }}</span>
        <div class="flex items-center gap-5">
          <div class="flex items-center gap-3.5">
            <span class="text-muted-foreground text-xs">{{
              $t('preferences.tabbar.enable')
            }}</span>
            <ElSwitch
              :model-value="preferences.tabbar.enable"
              @change="
                (value) =>
                  nextTick(() =>
                    updatePreferences({ tabbar: { enable: value as boolean } }),
                  )
              "
            />
          </div>
          <div class="flex items-center gap-3.5">
            <span class="text-muted-foreground text-xs">{{
              $t('preferences.tabbar.draggable')
            }}</span>
            <ElSwitch
              :model-value="preferences.tabbar.draggable"
              @change="
                (value) =>
                  nextTick(() =>
                    updatePreferences({
                      tabbar: { draggable: value as boolean },
                    }),
                  )
              "
            />
          </div>
          <div class="flex items-center gap-3.5">
            <span class="text-muted-foreground text-xs">{{
              $t('preferences.tabbar.icon')
            }}</span>
            <ElSwitch
              :model-value="preferences.tabbar.showIcon"
              @change="
                (value) =>
                  nextTick(() =>
                    updatePreferences({
                      tabbar: { showIcon: value as boolean },
                    }),
                  )
              "
            />
          </div>
          <div class="flex items-center gap-3.5">
            <span class="text-muted-foreground text-xs">{{
              $t('preferences.tabbar.maxCount')
            }}</span>
            <ElInputNumber
              :min="0"
              :max="30"
              :step="5"
              :model-value="preferences.tabbar.maxCount"
              @change="
                (value) =>
                  nextTick(() =>
                    updatePreferences({
                      tabbar: { maxCount: value },
                    }),
                  )
              "
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 动画 -->
    <div
      class="bg-background border-border flex flex-col gap-6 rounded-lg border p-5 pb-6"
    >
      <div class="text-base font-medium">
        {{ $t('sysAppearance.Animation') }}
      </div>
      <div class="flex flex-wrap items-center gap-5">
        <div class="flex items-center gap-3.5">
          <span class="text-muted-foreground text-xs">{{
            $t('preferences.animation.progress')
          }}</span>
          <ElSwitch
            :model-value="preferences.transition.progress"
            @change="
              (value) =>
                nextTick(() =>
                  updatePreferences({
                    transition: { progress: value as boolean },
                  }),
                )
            "
          />
        </div>
        <div class="flex items-center gap-3.5">
          <span class="text-muted-foreground text-xs">{{
            $t('preferences.animation.loading')
          }}</span>
          <ElSwitch
            :model-value="preferences.transition.loading"
            @change="
              (value) =>
                nextTick(() =>
                  updatePreferences({
                    transition: { loading: value as boolean },
                  }),
                )
            "
          />
        </div>
        <div class="flex basis-full flex-col gap-4">
          <div class="flex items-center gap-3.5">
            <span class="text-muted-foreground text-xs">{{
              $t('preferences.animation.transition')
            }}</span>
            <ElSwitch
              :model-value="preferences.transition.enable"
              @change="
                (value) =>
                  nextTick(() =>
                    updatePreferences({
                      transition: { enable: value as boolean },
                    }),
                  )
              "
            />
          </div>

          <div
            v-if="preferences.transition.enable"
            class="flex w-fit justify-between gap-3 px-2"
          >
            <div
              v-for="item in transitionPreset"
              :key="item"
              :class="{
                'outline-box-active': preferences.transition.name === item,
              }"
              class="outline-box p-2"
              @click="updatePreferences({ transition: { name: item } })"
            >
              <div
                :class="`${item}-slow`"
                class="bg-accent h-10 w-12 rounded-md"
              ></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 登录页外观 -->
    <div
      class="bg-background border-border flex flex-col gap-6 rounded-lg border p-5 pb-1.5"
    >
      <div class="text-base font-medium">
        {{ $t('sysAppearance.Login Page Appearance') }}
      </div>
      <!-- 登录页布局 -->
      <div class="flex flex-col gap-2.5">
        <span class="text-sm font-medium">{{
          $t('sysAppearance.Login Page Layout')
        }}</span>
        <div class="flex items-center gap-5">
          <div class="flex items-center gap-3.5">
            <span class="text-muted-foreground text-xs">{{
              $t('authentication.layout.alignLeft')
            }}</span>
            <ElSwitch
              :model-value="preferences.app.authPageLayout === 'panel-left'"
              @change="
                () =>
                  nextTick(() =>
                    updatePreferences({
                      app: { authPageLayout: 'panel-left' },
                    }),
                  )
              "
            />
          </div>
          <div class="flex items-center gap-3.5">
            <span class="text-muted-foreground text-xs">{{
              $t('authentication.layout.center')
            }}</span>
            <ElSwitch
              :model-value="preferences.app.authPageLayout === 'panel-center'"
              @change="
                () =>
                  nextTick(() =>
                    updatePreferences({
                      app: { authPageLayout: 'panel-center' },
                    }),
                  )
              "
            />
          </div>
          <div class="flex items-center gap-3.5">
            <span class="text-muted-foreground text-xs">{{
              $t('authentication.layout.alignRight')
            }}</span>
            <ElSwitch
              :model-value="preferences.app.authPageLayout === 'panel-right'"
              @change="
                () =>
                  nextTick(() =>
                    updatePreferences({
                      app: { authPageLayout: 'panel-right' },
                    }),
                  )
              "
            />
          </div>
        </div>
      </div>

      <!-- 登录页图片 -->
      <div class="flex flex-col gap-2.5">
        <span class="text-sm font-medium">{{
          $t('sysAppearance.Login Page Image')
        }}</span>
        <div class="flex w-fit flex-col gap-2">
          <Cropper
            accept="image/jpeg"
            :model-value="preferences.auth.sloganImage"
            @upload-success="
              (url) =>
                updatePreferences({
                  auth: { sloganImage: url },
                })
            "
          />
          <span class="text-foreground/50 text-sm">{{
            $t('sysAppearance.OnlyJPG')
          }}</span>
        </div>
      </div>

      <!-- 登录页品牌文案 -->
      <div class="flex flex-col gap-2.5">
        <span class="text-sm font-medium">{{
          $t('sysAppearance.Login Page Brand Copy')
        }}</span>
        <ElForm
          require-asterisk-position="right"
          label-width="100px"
          label-position="left"
        >
          <ElFormItem :label="$t('sysAppearance.Welcome Title')">
            <ElInput
              :placeholder="$t('sysAppearance.Please enter the welcome title')"
              :model-value="preferences.auth.welcomeBack"
              @input="(value) => updateAuthText(value, 'welcomeBack')"
            />
          </ElFormItem>
          <ElFormItem :label="$t('sysAppearance.Welcome Description')">
            <ElInput
              :placeholder="
                $t('sysAppearance.Please enter the welcome description')
              "
              :model-value="preferences.auth.loginSubtitle"
              @input="(value) => updateAuthText(value, 'loginSubtitle')"
            />
          </ElFormItem>
          <ElFormItem :label="$t('sysAppearance.Slogan Title')">
            <ElInput
              :placeholder="$t('sysAppearance.Please enter the slogan title')"
              :model-value="preferences.auth.pageTitle"
              @input="(value) => updateAuthText(value, 'pageTitle')"
            />
          </ElFormItem>
          <ElFormItem :label="$t('sysAppearance.Slogan Description')">
            <ElInput
              :placeholder="
                $t('sysAppearance.Please enter the slogan description')
              "
              :model-value="preferences.auth.pageDescription"
              @input="(value) => updateAuthText(value, 'pageDescription')"
            />
          </ElFormItem>
        </ElForm>
      </div>
    </div>

    <div class="mx-auto flex">
      <!-- <ElButton type="primary">恢复默认</ElButton> -->
      <ElButton type="primary" :loading="loading" @click="handleSubmit">
        {{ $t('button.save') }}
      </ElButton>
    </div>
  </div>
</template>
