import React from 'react';
import {
    PlayCircleOutlined,
    SettingOutlined,

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
        form: {
            rules: [{required: true, message: '请输入 Bot 的名称'}]
        }
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
                      optionsText={{
                          addCardTitle: "创建Bots",
                          noDataText: "你还没有Bots,快来创建你的Bots吧!",
                          noDataAddButtonText: "创建Bots"
                      }}
                      customActions={(item, existNodes) => {
                          return [
                              <SettingOutlined title="设置" onClick={() => {
                                  window.open(`/ai/bot/design/${item.id}`, "_blank")
                              }}/>,
                              <PlayCircleOutlined title="运行" onClick={() => {
                                  window.open(window.location.href.substring(0, window.location.href.indexOf('/ai')) + '/ai/externalBot/' + item.id, "_blank")
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
