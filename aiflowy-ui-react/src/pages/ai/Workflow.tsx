import React from 'react';
import {
    NodeIndexOutlined,
} from "@ant-design/icons";
import CardPage from "../../components/CardPage";
import {ColumnsConfig} from "../../components/AntdCrud";


const columnsColumns: ColumnsConfig<any> = [
    {
        key: 'id',
        hidden: true,
        form: {
            type: "hidden"
        }
    },
    {
        title: '名称',
        dataIndex: 'title',
        key: 'title',
        placeholder: "请输入工作流名称",
        supportSearch: true,
        form: {
            rules: [{required: true, message: '请输入工作流名称'}]
        }
    },
    {
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        width: "50%",
        form: {
            type: "TextArea",
            attrs: {
                rows: 3
            }
        }
    },

];


const Workflow: React.FC<{ paramsToUrl: boolean }> = () => {
    return (
        <>
            <CardPage tableAlias={"aiWorkflow"}
                      editModalTitle={"新增/编辑工作流"}
                      columnsConfig={columnsColumns}
                      addButtonText={"新增工作流"}
                      defaultAvatarSrc={"/favicon.png"}
                      editLayout={{labelWidth: 140}}
                      customActions={(item, existNodes) => {
                          return [
                              <NodeIndexOutlined title="设计工作流" onClick={() => {
                                  window.open(`/ai/workflow/design/${item.id}`, "_blank")
                              }}/>,
                              ...existNodes
                          ]
                      }}
            />
        </>
    )
};

export default {
    path: "/ai/workflow",
    element: Workflow
};
