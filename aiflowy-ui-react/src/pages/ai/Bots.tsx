import React from 'react';
import {
    SmileOutlined,
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
        title: 'Icon',
        dataIndex: 'icon',
        key: 'icon',
        form: {
            type: "image"
        }
    },
    {
        title: '名称',
        dataIndex: 'title',
        key: 'title',
        placeholder: "请输入 Bot 的名称",
        supportSearch: true,
    },
    {
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        form: {
            type: "TextArea",
            attrs: {
                rows: 3
            }
        }
    },

];


const Bots: React.FC<{ paramsToUrl: boolean }> = () => {
    return (
        <>
            <CardPage tableAlias={"aiBot"}
                      editModalTitle={"新增/编辑 Bot"}
                      columnsConfig={columnsColumns}
                      addButtonText={"新增 Bot"}
                      avatarKey="icon"
                      defaultAvatarSrc={"/favicon.png"}
                      editLayout={{labelWidth: 80}}
                      customActions={(item, existNodes) => {
                          return [
                              <SmileOutlined title="Bot 设置" onClick={() => {
                                  window.open(`/ai/bot/design/${item.id}`, "_blank")
                              }}/>,
                              ...existNodes
                          ]
                      }}
            />
        </>
    )
};

export default {
    path: "/ai/bots",
    element: Bots
};
