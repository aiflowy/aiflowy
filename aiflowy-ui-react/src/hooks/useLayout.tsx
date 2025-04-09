import React, {useContext} from "react"

export type LayoutOptions = {
    /**
     * 是否显示头部
     */
    showHeader?: boolean,

    /**
     * 头部左侧元素
     */
    headerLeftEl?: React.ReactNode,

    /**
     * 是否显示侧边栏
     */
    showLeftMenu?: boolean,

    /**
     * 侧边栏是否折叠
     */
    leftMenuCollapsed?: boolean,

    /**
     * 是否显示底部
     */
    showFooter?: boolean,

    /**
     * 是否显示面包屑
     */
    showBreadcrumb?: boolean,

    /**
     * 面包屑右侧元素
     */
    breadcrumbRightEl?: React.ReactNode,
}

export interface LayoutContextType {
    options?: LayoutOptions,
    setOptions: (options: LayoutOptions) => void,
}

export const LayoutContext = React.createContext<LayoutContextType>(null!)

export const useLayout = (): LayoutContextType => useContext(LayoutContext)