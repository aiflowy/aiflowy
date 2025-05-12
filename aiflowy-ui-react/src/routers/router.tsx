import {createBrowserRouter, Navigate, RouteObject} from "react-router-dom";
import Error404 from "../components/Error404";
import Layout from "../components/Layout";
import Login from "../pages/commons/login.tsx";
import React from "react";
import {ExternalBot} from "../pages/ExternalBot.tsx";
import {ExternalPage} from "../pages/ai/workflowDesign/ExternalPage.tsx";

/**
 * 登录成功之后的路由和菜单配置
 */
export const portalRouters: RouteObject[] = []

export const frontRouters: RouteObject[] = []


/**
 * 自动导入 pages 目录下的页面
 */
export const autoImportPages = import.meta.glob('../pages/**/*.tsx', {eager: true});

/**
 * 通过 import.meta 导入的路由配置，全部添加到变量 portalPages 上去
 */
Object.entries(autoImportPages).map(([_path, module]) => {
    const pageRoute = (module as any).default;
    if (typeof pageRoute === "object" && pageRoute.path && pageRoute.element) {
        //在 page 的导出定义中，element 可以是一个方法，也可以是一个节点，比如：<Login>
        const _paths = pageRoute.path.split(";");
        const isFrontPage = (pageRoute.frontEnable === true)
        for (let path of _paths) {
            const routers = isFrontPage ? frontRouters : portalRouters;
            if (typeof pageRoute.element === "function") {
                routers.push({
                    ...pageRoute,
                    //paramsToUrl 保存 CURD 的搜索、分页等数据同步到 URL
                    element: React.createElement(pageRoute.element, {paramsToUrl: true}),
                    path: path.trim()
                })
            } else {
                routers.push({
                    ...pageRoute,
                    path: path.trim()
                })
            }
        }

    }
})


/**
 * 全部路由
 */
const routers: RouteObject[] = [
    //需要登录的部分路由信息
    {
        path: "/",
        element: <Layout/>,
        errorElement: <Error404/>,
        children: [
            {
                path: "/",
                element: <Navigate to={"/login"}/>,
            },
            ...portalRouters
        ],
    },

    //其他不需要登录的路由配置
    {
        path: "/login",
        element: <Login/>,
    },
    {
        path: "/ai/externalBot",
        element: <ExternalBot/>,
    },
    {
        path: "/ai/workflow/external",
        element: <ExternalPage/>,
    },
    ...frontRouters
];

const router = createBrowserRouter(routers);
export {
    router,
}
