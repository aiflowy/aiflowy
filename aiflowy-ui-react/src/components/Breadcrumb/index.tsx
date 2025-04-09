import React from 'react';
import {Breadcrumb as AntdBreadcrumb, Flex} from 'antd';
import {useMenus} from "../../hooks/useMenus.tsx";
import {useLayout} from '../../hooks/useLayout.tsx';

/**
 * 面包屑组件
 */
const Breadcrumb: React.FC = () => {

    const {selectItems} = useMenus();
    const {options} = useLayout();

    const breadcrumbs = selectItems.map((item) => {
        return {
            // title: t(item.label)
            title: item.label
        }
    })
    breadcrumbs.unshift({title: "首页"})
    const lastItem = breadcrumbs[breadcrumbs.length - 1];

    return (
        <Flex style={{alignItems: "center", marginTop: "10px", marginBottom: "20px"}}>
            <h2 style={{margin: "0", padding: "0"}}>{lastItem?.title}</h2>
            <AntdBreadcrumb style={{marginLeft: '16px'}} items={breadcrumbs}/>
            <div style={{marginLeft: "auto", marginRight: "20px"}}>{options?.breadcrumbRightEl}</div>
        </Flex>
    );
};


export default Breadcrumb;