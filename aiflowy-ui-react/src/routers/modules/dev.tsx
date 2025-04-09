// import {
//     AlignLeftOutlined, ApartmentOutlined, CloudServerOutlined,
//     PlusOutlined,
//     UserOutlined,
// } from "@ant-design/icons";
// import {MenuRouteObject} from "../router.tsx";
// import CreateProject from "../../pages/dev/CreateProject.tsx";
// import ProjectList from "../../pages/dev/ProjectList.tsx";
// import React from "react";
// import ModelConfig from "../../pages/dev/ModelConfig.tsx";
// import VectorStoreConfig from "../../pages/dev/VectorStoreConfig.tsx";
// import AiEditorDemo from "../../pages/demos/AiEditorDemo.tsx";
//
// const devRouter: MenuRouteObject[] = [
//     {
//         path: "project",
//         label: "项目管理",
//         sortNo: 1,
//         icon: <UserOutlined/>,
//         type: "group",
//         children: [
//             {
//                 path: "create",
//                 label: "创建项目",
//                 icon: <PlusOutlined/>,
//                 element: React.createElement(CreateProject.component)
//             },
//             {
//                 path: "list",
//                 label: "项目管理",
//                 icon: <AlignLeftOutlined/>,
//                 element: <ProjectList/>,
//             },
//         ] as MenuRouteObject[]
//     },
//     {
//         path: "ai",
//         label: "Ai 配置",
//         type: "group",
//         sortNo: 2,
//         children: [
//             {
//                 path: "model",
//                 label: "大模型配置",
//                 icon: <ApartmentOutlined/>,
//                 element: <ModelConfig.component/>
//             },
//             {
//                 path: "vectorStorage",
//                 label: "向量数据存储",
//                 icon: <CloudServerOutlined/>,
//                 element: <VectorStoreConfig.component/>
//             },
//             {
//                 path: "editor",
//                 label: "AI 编辑演示",
//                 icon: <CloudServerOutlined/>,
//                 element: <AiEditorDemo/>
//             },
//         ] as MenuRouteObject[]
//     }
// ]
//
// export default devRouter