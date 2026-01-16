# 前置准备

为了更好的开发体验，我们提供了一些工具配置、项目说明，以便于您更好的开发。

## 需要掌握的基础知识

本项目需要一定前端基础知识，请确保掌握 Vue 的基础知识，以便能处理一些常见的问题。建议在开发前先学一下以下内容，提前了解和学习这些知识，会对项目理解非常有帮助:

- [Vue3](https://vuejs.org/)
- [Tailwind CSS](https://tailwindcss.com/)
- [TypeScript](https://www.typescriptlang.org/)
- [Vue Router](https://router.vuejs.org/)
- [Vitejs](https://vitejs.dev/)
- [Pnpm](https://pnpm.io/)
- [Turbo](https://turbo.build/)

## 工具配置

如果您使用的 IDE 是[vscode](https://code.visualstudio.com/)(推荐)的话，可以安装以下工具来提高开发效率及代码格式化:

- [Vue - Official](https://marketplace.visualstudio.com/items?itemName=Vue.volar) - Vue 官方插件（必备）。
- [Tailwind CSS](https://marketplace.visualstudio.com/items?itemName=bradlc.vscode-tailwindcss) - Tailwindcss 提示插件。
- [CSS Variable Autocomplete](https://marketplace.visualstudio.com/items?itemName=bradlc.vunguyentuan.vscode-css-variables) - Css 变量提示插件。
- [Iconify IntelliSense](https://marketplace.visualstudio.com/items?itemName=antfu.iconify) - Iconify 图标插件
- [i18n Ally](https://marketplace.visualstudio.com/items?itemName=Lokalise.i18n-ally) - i18n 插件
- [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint) - 脚本代码检查
- [Prettier](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode) - 代码格式化
- [Stylelint](https://marketplace.visualstudio.com/items?itemName=stylelint.vscode-stylelint) - css 格式化
- [Code Spell Checker](https://marketplace.visualstudio.com/items?itemName=streetsidesoftware.code-spell-checker) - 单词语法检查
- [DotENV](https://marketplace.visualstudio.com/items?itemName=mikestead.dotenv) - .env 文件 高亮

## 本地运行项目

如需本地运行，并进行调整，可以执行以下命令，执行该命令，你可以选择需要的应用进行开发：

```bash
# 后台管理系统
cd aiflowy-ui-admin
pnpm dev
```

```bash
# 用户中心
cd aiflowy-ui-usercenter
pnpm dev
```

## 区分构建环境

在实际的业务开发中，通常会在构建时区分多种环境，如测试环境`test`、生产环境`build`等。

此时可以修改三个文件，在其中增加对应的脚本配置来达到区分生产环境的效果。

以`aiflowy-ui-admin`添加测试环境`test`为例：

- `aiflowy-ui-admin\app\package.json`

```json
"scripts": {
  "build:prod": "pnpm vite build --mode production",
  "build:test": "pnpm vite build --mode test",
  "build:analyze": "pnpm vite build --mode analyze",
  "dev": "pnpm vite --mode development",
  "preview": "vite preview",
  "typecheck": "vue-tsc --noEmit --skipLibCheck"
},
```

增加命令`"build:test"`, 并将原`"build"`改为`"build:prod"`以避免同时构建两个环境的包。

- `aiflowy-ui-admin\package.json`

```json
"scripts": {
    "build": "cross-env NODE_OPTIONS=--max-old-space-size=8192 turbo build",
    "build:analyze": "turbo build:analyze",
    "build:app": "pnpm run build --filter=@aiflowy/app",
    "build-test:app": "pnpm run build --filter=@aiflowy/app build:test",

    ······
}
```

在`aiflowy-ui-admin\package.json`中加入构建测试环境的命令

- `turbo.json`

```json
"tasks": {
    "build": {
      "dependsOn": ["^build"],
      "outputs": [
        "dist/**",
        "dist.zip",
        ".vitepress/dist.zip",
        ".vitepress/dist/**"
      ]
    },

    "build-test:app": {
      "dependsOn": ["@aiflowy/app#build:test"],
      "outputs": ["dist/**"]
    },

    "@aiflowy/app#build:test": {
      "dependsOn": ["^build"],
      "outputs": ["dist/**"]
    },

    ······
```

在`aiflowy-ui-admin\turbo.json`中加入相关依赖的命令

## 公共静态资源

项目中需要使用到的公共静态资源，如：图片、静态HTML等，需要在开发中通过 `src="/xxx.png"` 直接引入的。

需要将资源放在对应项目的 `public/static` 目录下。引入的路径为：`src="/static/xxx.png"`。

## DevTools

项目内置了 [Vue DevTools](https://github.com/vuejs/devtools-next) 插件，可以在开发过程中使用。默认关闭，可在`.env.development` 内开启，并重新运行项目即可：

```bash
VITE_DEVTOOLS=true
```

开启后，项目运行会在页面底部显示一个 Vue DevTools 的图标，点击即可打开 DevTools。

![Vue DevTools](/assets/images/devtools.png)

## 本地运行文档

如需本地运行文档，并进行调整，可以执行以下命令：

```bash
cd docs
npm run docs:dev
#or
pnpm run docs:dev
```

## 问题解决

如果你在使用过程中遇到依赖相关的问题，可以尝试以下重新安装依赖：

```bash
# 请在项目根目录下执行
cd aiflowy-ui-admin
# or
cd aiflowy-ui-usercenter
# 该命令会删除整个仓库所有的 node_modules、yarn.lock、package.lock.json后
# 再进行依赖重新安装（安装速度会明显变慢）。
pnpm reinstall
```
