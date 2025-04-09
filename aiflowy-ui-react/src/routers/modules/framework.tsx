// import {
//     BlockOutlined, FundOutlined, MenuOutlined,
//     TagsOutlined, TeamOutlined, ThunderboltOutlined,
//     UserOutlined, UserSwitchOutlined,
// } from "@ant-design/icons";
// import {MenuRouteObject} from "../router.tsx";
// import Welcome from "../../pages/framework/Welcome.tsx";
// import Accounts from "../../pages/framework/Accounts.tsx";
// import Depts from "../../pages/framework/Depts.tsx";
// import Dicts from "../../pages/framework/Dicts.tsx";
// import Roles from "../../pages/framework/Roles.tsx";
// import Menus from "../../pages/framework/Menus.tsx";
// import Permissions from "../../pages/framework/Permissions.tsx";
//
// const frameworkRouter: MenuRouteObject[] = [
//     {
//         path: "index",
//         label: "仪表盘",
//         sortNo:0,
//         icon: <FundOutlined/>,
//         element: <Welcome/>
//     },
//     {
//         path: "system",
//         label: "系统配置",
//         icon: <UserOutlined/>,
//         type: "group",
//         sortNo: 99,
//         children: [
//             {
//                 path: "account",
//                 label: "用户列表",
//                 icon: <TeamOutlined/>,
//                 element: <Accounts/>
//             },
//             {
//                 path: "dept",
//                 label: "部门管理",
//                 icon: <BlockOutlined/>,
//                 element: <Depts/>
//             },
//             {
//                 path: "role",
//                 label: "角色管理",
//                 icon: <TagsOutlined/>,
//                 element: <Roles/>
//             },
//             {
//                 path: "permission",
//                 label: "权限管理",
//                 icon: <ThunderboltOutlined/>,
//                 element: <Permissions/>
//             },
//             {
//                 path: "menu",
//                 label: "菜单管理",
//                 icon: <MenuOutlined/>,
//                 element: <Menus/>
//             },
//             {
//                 path: "position",
//                 label: "数据字典",
//                 icon: <UserSwitchOutlined/>,
//                 element: <Dicts/>
//             },
//         ] as MenuRouteObject[]
//     }
// ]
//
// export default frameworkRouter