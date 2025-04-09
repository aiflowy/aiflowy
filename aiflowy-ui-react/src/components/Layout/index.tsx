import React, {useState} from 'react';
import {Layout as AntdLayout} from 'antd';
import {Outlet} from "react-router-dom";
import Header from "./Header.tsx";
import LeftMenu from "./LeftMenu.tsx";
import Breadcrumb from "../Breadcrumb";
import CheckLogin from "../CheckLogin";
import {LayoutContext, LayoutOptions} from "../../hooks/useLayout.tsx";

const {Content} = AntdLayout;


const Layout: React.FC = () => {

    const [options, setOptions0] = useState<LayoutOptions>({
        showHeader: true,
        showLeftMenu: true,
        leftMenuCollapsed: false,
        showFooter: true,
        showBreadcrumb: true,
    });

    const setOptions = (o: LayoutOptions) => {
        setOptions0(prevOptions => ({...prevOptions, ...o}))
    }

    return (
        <CheckLogin>
            <LayoutContext.Provider value={{options, setOptions}}>
                <AntdLayout style={{minHeight: '100vh'}}>
                    {options.showLeftMenu && <LeftMenu collapsed={options.leftMenuCollapsed ?? false}/>}
                    <AntdLayout>
                        <Header collapsed={options.leftMenuCollapsed ?? false}/>
                        <AntdLayout style={{padding: '10px'}}>
                            {options.showBreadcrumb && <Breadcrumb/>}
                            <Content style={{background: "#fff", borderRadius: '3px', overflow: "hidden"}}>
                                {/*<CheckPerms>*/}
                                <Outlet/>
                                {/*</CheckPerms>*/}
                            </Content>
                        </AntdLayout>
                    </AntdLayout>
                </AntdLayout>
            </LayoutContext.Provider>
        </CheckLogin>
    );
};


export default Layout;