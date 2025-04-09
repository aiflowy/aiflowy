import React from 'react';
import {Menu} from 'antd';
import {useNavigate} from "react-router-dom";
import Sider from "antd/es/layout/Sider";
// import logo from "../../assets/logo.png";
import logo from "/favicon.png";
import {useMenus} from "../../hooks/useMenus.tsx";


/**React
 * 侧边栏
 * @constructor
 */
const LeftMenu: React.FC<{ collapsed: boolean }> = ({collapsed}) => {

        const navigate = useNavigate();
        const {loading, menuItems, selectItems} = useMenus();
        const selectMenuKeys: string[] = selectItems.map((item) => item.key as string);

        return (
            <Sider width={280} style={{background: "#2b2d30",minHeight:"100vh"}} collapsed={collapsed}>
                {/*<div style={{width: "100%", display: "flex", borderRight:"1px solid #ccc"}}>*/}
                <div >
                    <div
                        style={{
                            width: "100%",
                            margin: "auto",
                            background: "#fff",
                            padding: "5px 5px 5px 20px",
                            display: "flex",
                        }}>

                        <div>
                            <img alt="AIFlowy" src={logo} style={{height: "38px"}}/>
                        </div>

                        {!collapsed &&
                        <div style={{
                            fontSize:"28px",
                            height: "38px",
                            lineHeight: "38px",
                            marginLeft: "10px",
                            fontWeight: "bold",
                        }}>
                            AIFlowy
                        </div>
                        }
                    </div>
                </div>

                {!loading ? <Menu
                    style={{background: "#2b2d30",marginTop:"20px"}}
                    mode="inline"
                    defaultSelectedKeys={selectMenuKeys}
                    defaultOpenKeys={selectMenuKeys}
                    items={menuItems}
                    onClick={(item)=>{
                        navigate(item.key)
                    }}
                /> : <>loading...</>}
            </Sider>

        );
    }
;


export default LeftMenu;