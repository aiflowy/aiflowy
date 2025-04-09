import {ThemeConfig} from "antd";

import zhCN from 'antd/locale/zh_CN';

export const appConfig = {

    // 默认语言
    locale: zhCN,

    // 主题配色
    theme: {

//         亮色或者暗色配置
//         algorithm:darkAlgorithm,

        //全局配置
        token: {
            // 主色调
            colorPrimary: '#152b39',
            colorPrimaryText:"#fff",

            // colorPrimaryBg:'#afafaf',
            // colorPrimaryText:'red',
            // colorBgBase:'red',
            // colorPrimaryText:'red',

            // 边框默认颜色
            colorBorder: '#aaa',
            colorBorderSecondary: '#ccc',
            groupTitleColor: '#ccc',

            // 圆角大小，默认 6
            borderRadius: 3,

            //获得焦点后的外部轮廓
            controlOutline: "#ccc",
            controlOutlineWidth: 2,
        },

        //组件配置
        components: {
            Menu: {
                //菜单项文字选中颜色
                itemSelectedColor: "#fff",
                itemColor: "#bbb",

                //菜单项文字悬浮颜色
                itemHoverColor: "#ccc",

                //菜单分组
                groupTitleColor: '#999',
                // groupTitleFontSize: '16px',
                groupTitleLineHeight: '2',

                //icon 大小
                iconSize: 16,
            },
            Segmented: {
                trackPadding: 4
            },
        },
    } as ThemeConfig,

    // 布局方式
    layout: {
        menuPosition: "left",
        contentWidth: "Fixed",
        fixedHeader: false,
        fixedSliderBar: false,
        hideHintAlert: false,
        hideCopyButton: false
    }
}