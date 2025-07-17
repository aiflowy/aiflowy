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
                <div style={{display: 'flex', flexDirection: 'column', height: '100vh', overflow: 'hidden'}}>
                    <AntdLayout style={{height: '100vh',display: 'flex',alignItems:'stretch'}}>
                        <div>
                            {options.showLeftMenu && <LeftMenu collapsed={options.leftMenuCollapsed ?? false}/>}
                        </div>

                        <AntdLayout>
                            <div style={{position: 'sticky', top: 0, zIndex: 1}}>
                                <Header collapsed={options.leftMenuCollapsed ?? false}/>
                            </div>
                            <div>
                                <AntdLayout>
                                    {options.showBreadcrumb && <Breadcrumb/>}
                                    <div style={{height: '100%',  overflow: 'auto'}}>
                                        <Content style={{ borderRadius: '3px'}}>
                                            {/*<CheckPerms>*/}
                                            <Outlet/>
                                            {/*</CheckPerms>*/}
                                        </Content>
                                    </div>

                                </AntdLayout>
                            </div>

                        </AntdLayout>
                    </AntdLayout>
                </div>

            </LayoutContext.Provider>
        </CheckLogin>
    );
};


export default Layout;