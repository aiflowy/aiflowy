import {useGet} from "./useApis.ts";
import {useLocation} from "react-router-dom";
import {removeIf} from "../libs/utils.ts";
import {MenuProps} from "antd";
import AntdIcon from "../components/AntdIcon";

type MenuItem = Required<MenuProps>['items'][number] & { children?: MenuItem[], label: string };

const convert = (menus: any[]) => {
    if (menus) {
        const menuItems: MenuItem[] = [];
        for (let menu of menus) {
            menuItems.push({
                key: menu.url || menu.id,
                children: convert(menu.children),
                label: menu.name,
                type: menu.groupType,
                icon: <AntdIcon name={menu.icon}/>
            } as MenuItem)
        }
        return menuItems;
    }
}

const convertV3 = (menus: any[]) => {
    if (menus) {
        const menuItems: MenuItem[] = [];
        for (let menu of menus) {
            menuItems.push({
                key: menu.menuUrl || menu.id,
                children: convertV3(menu.children),
                label: menu.menuTitle,
                type: (menu.parentId === 0 && !menu.menuUrl)? "group" : undefined,
                icon: <AntdIcon name={menu.menuIcon}/>
            } as MenuItem)
        }
        return menuItems;
    }
}


export const useMenus = () => {

    let {pathname} = useLocation();

    // const {loading, result} = useList("sysMenu", {asTree: true});
    // const menuItems = convert(result?.data)

    const {loading, result} = useGet("/api/v1/sysMenu/tree", {menuType: 0,isShow: 1});
    const menuItems = convertV3(result?.data)

    const selectItems: MenuItem[] = [];

    if (menuItems) {
        let picked = false;
        const pick = (items: MenuItem[]) => {
            for (const item of items) {
                if (picked) break;

                selectItems.push(item);

                if (item.children) {
                    pick(item.children)
                }

                if (item.key === pathname) {
                    picked = true;
                }

                if (!picked) {
                    removeIf(selectItems, (v) => v == item)
                }
            }
        }
        pick(menuItems)
    }

    return {
        loading,
        menuItems,
        selectItems,
    }
}