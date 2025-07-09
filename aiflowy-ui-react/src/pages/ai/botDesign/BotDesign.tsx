import React, {useEffect, useState} from 'react';

import {useLayout} from '../../../hooks/useLayout.tsx';
import {Row} from 'antd/lib/index';
import {App, Avatar, Badge, Button, Col, Collapse, Form, Input, Modal, Select, Space, Switch, Tooltip} from 'antd';
import Title from 'antd/es/typography/Title';
import {DeleteOutlined, PlusOutlined} from "@ant-design/icons";
import {
    useDetail,
    useGetManual,
    useList,
    usePost,
    usePostManual,
    useRemove,
    useSave,
    useUpdate
} from "../../../hooks/useApis.ts";
import {useParams} from "react-router-dom";
import {WorkflowsModal} from "./Workflows.tsx";
import {DebouncedTextArea} from "../../../components/DebouncedTextArea";
import {LlmSlider} from "./LlmSlider.tsx";
import {KnowledgeModal} from "./Knowledge.tsx";
import TextArea from "antd/es/input/TextArea";
import {getSessionId} from "../../../libs/getSessionId.ts";
import {AiProChat, ChatMessage} from "../../../components/AiProChat/AiProChat";
import {PluginTools} from "./PluginTools.tsx";
import { processArray} from "../../../libs/parseAnswerUtil.tsx";
import {useSseWithEvent} from "../../../hooks/useSseWithEvent.ts";
import {useCheckPermission} from "../../../hooks/usePermissions.tsx";

const colStyle: React.CSSProperties = {
    background: '#fafafa',
    padding: '8px',
    border: "1px solid #eee",
    height: 'calc(100vh - 68px)',
    overflowY: 'hidden',
};


type CollapseLabelProps = {
    text: string,
    onClick: () => void,
    plusDisabled?: boolean
    badgeCount?:number
}

const CollapseLabel: React.FC<CollapseLabelProps> = ({text, onClick, plusDisabled = false,badgeCount = 0}) => {
    return <div style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
    }}>
        <span>{text}</span>
        {!plusDisabled && <div style={{
            display: "flex",
            justifyContent:"space-between",
            alignItems: "center",
            gap:"15px",
        }}>
            <Badge
                className="site-badge-count-109"
                count={badgeCount}
                style={{ backgroundColor: '#52c41a' }}
            />
            <PlusOutlined onClick={(event) => {
                event.stopPropagation();
                onClick?.();
            }}/>
        </div>}
    </div>
}
export interface PresetQuestion {
    key: string;
    description: string;
    icon?: React.ReactNode;
}

export const ListItem: React.FC<{
    title?: string,
    description?: string | React.ReactNode,
    icon?: string,
    onButtonClick?: () => void
}> = ({title, description, icon = "/favicon.png", onButtonClick}) => {
    return (
        <div style={{
            display: "flex",
            gap: "20px",
            alignItems: "center",
            background: "#eaeaea",
            padding: "10px",
            borderRadius: "3px"
        }}>
            <div style={{"width": "40px"}}>
                <Avatar shape="square" src={icon || "/favicon.png"} />
            </div>
            <div style={{flexGrow: 1}}>
                <div>{title}</div>
                <div style={{color: "#999999"}}>
                    <Tooltip title={description}>
                        <div style={{
                            display: '-webkit-box',
                            WebkitLineClamp: 1,      // 限制显示行数
                            WebkitBoxOrient: 'vertical',
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                        }}>
                            {description}
                        </div>
                    </Tooltip>

                </div>
            </div>
            <div>
                <Button type={"text"} onClick={() => {
                    onButtonClick?.()
                }} icon={<DeleteOutlined/>}/>
            </div>
        </div>
    )
}


const BotDesign: React.FC = () => {

    const [span] = useState(8)
    const params = useParams();
    const {result: detail, doGet: reGetDetail} = useDetail("aiBot", params.id);
    const {message} = App.useApp()

    const [systemPrompt, setSystemPrompt] = useState<string>('')
    const [welcomeMessage, setWelcomeMessage] = useState<string>('')
    const [isOpenProblemPreset, setIsOpenProblemPreset] = useState<boolean>(false)

    const {doSave: doSavePlugin} = useSave("aiBotPlugins");
    const {doPost: doRemovePlugin} = usePostManual('/api/v1/aiBotPlugins/doRemove');
    // 添加状态管理预设问题数组
    const [presetQuestions, setPresetQuestions] = useState<PresetQuestion[]>([]);


    const {setOptions} = useLayout();
    useEffect(() => {
        setOptions({
            leftMenuCollapsed: true, showBreadcrumb: false,
            headerLeftEl: <span style={{marginLeft: "20px"}}>{detail?.data?.title}</span>,
        })
        return () => {
            setOptions({leftMenuCollapsed: false, showBreadcrumb: true, headerLeftEl: ''})
        }
    }, [detail]);

    const {doUpdate} = useUpdate("aiBot");

    const updateBot = (values: any) => {
        doUpdate({
            data: {
                ...values,
                id: params.id,
            }
        }).then(reGetDetail)
            .then(() => {
                message.success("保存成功")
            })
    }

    const {doPost: updateBotLLMOptions} = usePostManual("/api/v1/aiBot/updateLlmOptions")

    const doUpdateBotLLMOptions = (values: any) => {
        updateBotLLMOptions({
            data: {
                llmOptions: values,
                id: params.id,
            }
        })
    }

    const {doPost: updateBotOptions} = usePostManual("/api/v1/aiBot/updateOptions")
    const doUpdateBotOptions = (values: any) => {
        updateBotOptions({
            data: {
                options: values,
                id: params.id,
            }
        }).then(reGetDetail)
            .then(() => {
                message.success("保存成功")

            })
    }

    useEffect(() => {
        setSystemPrompt(detail?.data?.llmOptions?.systemPrompt || '你是一个AI助手，请根据用户的问题给出清晰、准确的回答。');
        setWelcomeMessage(detail?.data?.options?.welcomeMessage)
        // 转换后端数据为新的格式
        setPresetQuestions(detail?.data?.options?.presetQuestions || [])
        setAnonymousEnabled(detail?.data?.options?.anonymousEnabled)
        console.log(333,detail)
    }, [detail]);

    const {result: workflowResult, doGet: doGetWorkflow} = useList("aiBotWorkflow", {"botId": params.id});
    const {doSave: doSaveWorkflow} = useSave("aiBotWorkflow");
    const {doRemove: doRemoveAiBotWorkflow} = useRemove("aiBotWorkflow");
    const [workflowOpen, setWorkflowOpen] = useState(false)

    const pluginToolPermission = useCheckPermission("/api/v1/aiPlugin/query");
    const {doPost: doPostPluginTool} = usePost('/api/v1/aiPluginTool/tool/list')

    const {doPost: doRemovePluginTool} = usePostManual('/api/v1/aiBotPlugins/doRemove')
    const [pluginOpen, setPluginOpen] = useState(false)


    const {result: knowledgeResult, doGet: doGetKnowledge} = useList("aiBotKnowledge", {"botId": params.id});
    const {doSave: doSaveKnowledge} = useSave("aiBotKnowledge");
    const {doRemove: doRemoveAiBotKnowledge} = useRemove("aiBotKnowledge");
    const [knowledgeOpen, setKnowledgeOpen] = useState(false)


    const llmQueryPermission = useCheckPermission("/api/v1/aiLlm/query")

    const botSavePermission = useCheckPermission("/api/v1/aiBot/save")
    const botRemovePermission = useCheckPermission("/api/v1/aiBot/remove")
    const {result: llmResult, doGet: getLlmLst} = useGetManual("/api/v1/aiLlm/list",);

    const {start: startChat} = useSseWithEvent("/api/v1/aiBot/chat");
    const {doPost: doRemoveMsg} = usePostManual('/api/v1/aiBotMessage/removeMsg')
    const [chats, setChats] = useState<ChatMessage[]>([]);
    const {result: messageResult} = useList("aiBotMessage", {
        botId: params.id,
        sessionId: getSessionId()
    });
    const [pluginToolData, setPluginToolData] = useState([])

    const defaultWelcomeMessage = "欢迎使用AIFlowy";

    useEffect(() => {


        const messageList = messageResult?.data;
        const processedItems = processArray(messageList);

        setChats(processedItems);

    }, [messageResult]);
    useEffect(() => {

        console.log(pluginToolPermission)

        if (pluginToolPermission){
            doPostPluginTool({data: {botId: params.id}}).then(r => {
                setPluginToolData(r?.data?.data)
            })
        }


    }, [pluginToolPermission]);


    useEffect(() => {
        if (llmQueryPermission) {
            getLlmLst({
                params: {supportFunctionCalling: true}
            }).then();
        }
    }, [llmQueryPermission]);


    const clearMessage: () => void = () => {
        doRemoveMsg({
            data: {
                botId: params.id,
                sessionId: getSessionId(),
                isExternalMsg: 0
            }
        }).then(res => {
            if (res.data.errorCode === 0) {
                setChats([])
            }
        })
    }
    const handleProblemCancel = () =>{
        setIsOpenProblemPreset(false)
    }
    // 创建表单实例
    const [form] = Form.useForm();

    const [anonymousEnabled,setAnonymousEnabled] = useState<boolean>(false)
    const [anonymousSwitchLoading,setAnonymousSwitchLoading] = useState<boolean>(false)

    return (
        <>
            <WorkflowsModal open={workflowOpen} onClose={() => setWorkflowOpen(false)}
                            onCancel={() => setWorkflowOpen(false)}
                            goToPage={"/ai/workflow"}
                            addedItems={workflowResult?.data || []}
                            addedItemsKeyField={"workflowId"}
                            onSelectedItem={item => {

                                if (!botSavePermission) {
                                    message.warning("你没有配置bot的权限！")
                                    return;
                                }

                                setWorkflowOpen(false)
                                doSaveWorkflow({
                                    data: {
                                        botId: params.id,
                                        workflowId: item.id,
                                    }
                                }).then(doGetWorkflow)
                            }}
            />

            <PluginTools
                selectedItem={pluginToolData && pluginToolData.length ? pluginToolData.map(m => (m as any).id) : []}
                goToPage="/ai/plugin"
                open={pluginOpen} onClose={() => setPluginOpen(false)}
                onCancel={() => setPluginOpen(false)}
                onSelectedItem={item => {

                    if (!botSavePermission) {
                        message.warning("你没有配置bot的权限！")
                        return;
                    }

                    setPluginOpen(false)

                    if (!botSavePermission) {
                        message.warning("你没有配置bot的权限！")
                        return;
                    }

                    doSavePlugin({
                        data: {
                            botId: params.id,
                            pluginToolId: item.id,
                        }
                    }).then(r => {
                        if (r?.data?.errorCode === 0) {
                            message.success("添加成功")
                            // 重新获取插件数据
                            doPostPluginTool({data: {botId: params.id}}).then(r => {
                                setPluginToolData(r?.data?.data)
                            })
                        } else {
                            message.error("添加失败")
                        }
                    })
                }}
                onRemoveItem={(item) => {
                    setPluginOpen(false)


                    if (!botSavePermission || !botRemovePermission) {
                        message.warning("你没有配置bot的权限！")
                        return;
                    }

                    doRemovePlugin({data: {pluginToolId: item.id, botId: params.id}}).then(res => {
                        if (res?.data?.errorCode === 0){
                            message.success('删除成功')
                            // 重新获取插件数据
                            doPostPluginTool({data: {botId: params.id}}).then(r => {
                                setPluginToolData(r?.data?.data)
                            })
                        } else {
                            message.error('删除失败')
                        }
                    })
                }}
            />

            <KnowledgeModal open={knowledgeOpen} onClose={() => setKnowledgeOpen(false)}
                            onCancel={() => setKnowledgeOpen(false)}
                            goToPage={"/ai/knowledge"}
                            addedItems={knowledgeResult?.data || []}
                            addedItemsKeyField={"knowledgeId"}
                            onSelectedItem={item => {

                                if (!botSavePermission) {
                                    message.warning("你没有配置bot的权限！")
                                    return;
                                }

                                setKnowledgeOpen(false)
                                doSaveKnowledge({
                                    data: {
                                        botId: params.id,
                                        knowledgeId: item.id,
                                    }
                                }).then(doGetKnowledge)
                            }}/>
            <div style={{height: 'calc(100vh - 68px)', display: "flex"}} className={"agentsflow"}>
                <Row style={{width: "100%"}}>
                    <Col span={span} style={colStyle}>
                        <Title level={5}>系统提示词（System Prompt）</Title>
                        <DebouncedTextArea value={systemPrompt} style={{height: "calc(100% - 30px)"}} autoSize={false}
                                           onChangeImmediately={(e) => {

                                               if (!botSavePermission) {
                                                   message.warning("你没有配置bot的权限！")
                                                   return;
                                               }

                                               setSystemPrompt(e.target.value)
                                           }}
                                           onChange={e => {

                                               if (!botSavePermission) {
                                                   return;
                                               }

                                               doUpdateBotLLMOptions({systemPrompt: e.target.value})
                                           }}/>
                    </Col>
                    <Col span={span} style={{...colStyle,overflowY:"scroll"}}>
                        <div style={{margin: "10px 0 5px", color: "#666"}}>大模型</div>
                        <div style={{
                            background: "#f5f5f5",
                            borderRadius: "3px",
                            padding: "10px 20px",
                            display: "flex",
                            flexDirection: "column",
                            gap: "10px"
                        }}>
                            <Select
                                style={{width: "100%"}}
                                placeholder="请选择大模型"
                                allowClear
                                disabled={!llmQueryPermission || !botSavePermission}
                                // fieldNames={{label: 'title', value: 'id'}}
                                // options={[
                                //     ...llmResult?.data || []
                                // ]}

                                value={llmQueryPermission ? detail?.data?.llmId : "no_access"}
                                onChange={(value: any) => {
                                    value = value || ''
                                    updateBot({ llmId: value === 'no_access' ? '' : value || '' });
                                }}
                            >
                                {!llmQueryPermission && (
                                    <Select.Option value="no_access" disabled>
                                        无权配置
                                    </Select.Option>
                                )}
                                {llmResult?.data?.map((llm: { title: string; id: string }) => (
                                    <Select.Option key={llm.id} value={llm.id}>
                                        {llm.title}
                                    </Select.Option>
                                ))}
                            </Select>
                            <LlmSlider disabled={!botSavePermission} title="温度" min={0.1} max={1} step={0.1}
                                       defaultValue={detail?.data?.llmOptions?.temperature || 0.7}
                                       onChange={(value: number) => {
                                           doUpdateBotLLMOptions({temperature: value})
                                       }}/>
                            <LlmSlider disabled={!botSavePermission} title="TopK" min={1} max={10} step={1}
                                       defaultValue={detail?.data?.llmOptions?.topK || 4}
                                       onChange={(value: number) => {
                                           doUpdateBotLLMOptions({topK: value})
                                       }}/>
                            <LlmSlider disabled={!botSavePermission} title="TopP" min={0.1} max={1} step={0.1}
                                       defaultValue={detail?.data?.llmOptions?.topP || 0.7}
                                       onChange={(value: number) => {
                                           doUpdateBotLLMOptions({topP: value})
                                       }}/>
                            <LlmSlider disabled={!botSavePermission} title="最大回复长度" min={1024} max={10000}
                                       defaultValue={detail?.data?.llmOptions?.maxReplyLength || 2048}
                                       onChange={(value: number) => {
                                           doUpdateBotLLMOptions({maxReplyLength: value})
                                       }}/>
                            <LlmSlider disabled={!botSavePermission} title="携带历史条数" min={1} max={10} step={1}
                                       defaultValue={detail?.data?.llmOptions?.maxMessageCount || 3}
                                       onChange={(value: number) => {
                                           doUpdateBotLLMOptions({maxMessageCount: value})
                                       }}/>
                        </div>

                        <div style={{margin: "10px 0 5px", color: "#666"}}>技能</div>
                        <Collapse items={[
                            {
                                key: 'workflow',
                                label: <CollapseLabel text="工作流" badgeCount={workflowResult?.data?.length ?? 0}
                                                      onClick={() => {
                                                          setWorkflowOpen(true)
                                                      }}/>,
                                children:
                                    <div>
                                        {workflowResult?.data?.length ?
                                            workflowResult?.data?.map((item: any) => {
                                                return <ListItem key={item.id} title={item.workflow.title}
                                                                 description={item.workflow.description}
                                                                 icon={item.workflow.icon}
                                                                 onButtonClick={() => {

                                                                     if (!botSavePermission || !botRemovePermission) {
                                                                         message.warning("你没有配置bot的权限！")
                                                                         return;
                                                                     }

                                                                     Modal.confirm({
                                                                         title: '确定要删除该工作流吗？',
                                                                         content: '删除后，该工作流将不再关联该机器人，但工作流本身不会被删除。',
                                                                         onOk: () => {
                                                                             doRemoveAiBotWorkflow({
                                                                                 data: {id: item.id}
                                                                             }).then(doGetWorkflow)
                                                                         }
                                                                     })
                                                                 }}
                                                />
                                            }) : (
                                                <div style={{color: '#999', textAlign: 'center', padding: '8px 0'}}>
                                                    未绑定工作流，点击右上角"+"添加
                                                </div>
                                            )

                                        }
                                    </div>
                                ,
                            },
                            {
                                key: 'knowledge',
                                label: <CollapseLabel text="知识库" badgeCount={knowledgeResult?.data?.length ?? 0}
                                                      onClick={() => {
                                                          setKnowledgeOpen(true)
                                                      }}/>,
                                children:
                                    <div>
                                        {knowledgeResult?.data.length ?
                                            knowledgeResult?.data?.map((item: any) => {
                                                return <ListItem key={item.id} title={item.knowledge.title}
                                                                 description={item.knowledge.description}
                                                                 icon={item.knowledge.icon}
                                                                 onButtonClick={() => {

                                                                     if (!botSavePermission || !botRemovePermission) {
                                                                         message.warning("你没有配置bot的权限！")
                                                                         return;
                                                                     }

                                                                     Modal.confirm({
                                                                         title: '确定要删除该知识库吗？',
                                                                         content: '删除后，该知识库将不再关联该机器人，但知识库本身不会被删除。',
                                                                         onOk: () => {
                                                                             doRemoveAiBotKnowledge({
                                                                                 data: {id: item.id}
                                                                             }).then(doGetKnowledge)
                                                                         }
                                                                     })
                                                                 }}
                                                />
                                            }) :
                                            (
                                                <div style={{color: '#999', textAlign: 'center', padding: '8px 0'}}>
                                                    未绑定知识库，点击右上角"+"添加
                                                </div>
                                            )
                                        }
                                    </div>
                                ,
                            },
                            {
                                key: 'plugins',
                                label: <CollapseLabel text="插件" badgeCount={pluginToolData?.length ?? 0}
                                                      onClick={() => {
                                                          setPluginOpen(true)
                                                      }}/>,
                                children:
                                    <div>
                                        {pluginToolData?.length ?
                                            (
                                                pluginToolData.map((item: any) => {
                                                    return <ListItem key={item.id} title={item?.name}
                                                                     description={item.description}
                                                                     icon={item?.icon}
                                                                     onButtonClick={() => {

                                                                         if (!botSavePermission || !botRemovePermission) {
                                                                             message.warning("你没有配置bot的权限！")
                                                                             return;
                                                                         }

                                                                         Modal.confirm({
                                                                             title: '确定要删除该插件吗？',
                                                                             content: '删除后，该插件将不再关联该机器人，但插件本身不会被删除。',
                                                                             onOk: () => {
                                                                                 doRemovePluginTool({
                                                                                     data: {
                                                                                         pluginToolId: item.id,
                                                                                         botId: params.id
                                                                                     }
                                                                                 }).then(() => {
                                                                                     doPostPluginTool({data: {botId: params.id}})
                                                                                         .then(r => {
                                                                                             setPluginToolData(r?.data?.data)
                                                                                         })
                                                                                 })
                                                                             }
                                                                         })
                                                                     }}
                                                    />
                                                })
                                            ) :
                                            (
                                                <div style={{color: '#999', textAlign: 'center', padding: '8px 0'}}>
                                                    未绑定插件，点击右上角"+"添加
                                                </div>
                                            )
                                        }
                                    </div>
                                ,
                            },
                        ]} bordered={false}/>


                        <div style={{margin: "20px 0 5px", color: "#666"}}>对话设置</div>
                        <Collapse items={[
                            {
                                key: 'questions',
                                label: <CollapseLabel text="问题预设" badgeCount={presetQuestions.length ?? 0}
                                                      onClick={() => {
                                                          setIsOpenProblemPreset(true)
                                                          // reGetDetail().then(res =>{
                                                          //     setPresetQuestions(res?.data?.data?.options.presetQuestions)
                                                          //
                                                          // })
                                                      }}/>,
                                children: (
                                    <div style={{padding: '0'}}>
                                        {presetQuestions.length > 0 ? (
                                            <div style={{display: 'flex', flexDirection: 'column', gap: '8px'}}>
                                                {presetQuestions.map((question, index) => (
                                                    <div
                                                        key={index}
                                                        style={{
                                                            padding: '8px 12px',
                                                            background: '#eaeaea',
                                                            borderRadius: '4px',
                                                            display: 'flex',
                                                            justifyContent: 'space-between',
                                                            alignItems: 'center'
                                                        }}
                                                    >
                                                        <span>{question.description}</span>
                                                        <Button
                                                            type="text"
                                                            icon={<DeleteOutlined />}
                                                            onClick={() => {
                                                                const newQuestions = [...presetQuestions];
                                                                newQuestions.splice(index, 1);
                                                                setPresetQuestions(newQuestions);
                                                                    updateBotOptions({
                                                                        data: {
                                                                            options: { presetQuestions: newQuestions },
                                                                            id: params.id,
                                                                        }
                                                                    }).then((res) => {
                                                                        if (res?.data?.errorCode === 0){
                                                                            reGetDetail().then(response =>{
                                                                                setPresetQuestions(response?.data?.data?.options.presetQuestions)
                                                                            })
                                                                        }

                                                                    })
                                                            }}
                                                        />
                                                    </div>
                                                ))}
                                            </div>
                                        ) : (
                                            <div style={{ color: '#999', textAlign: 'center', padding: '8px 0' }}>
                                                暂无预设问题，点击右上角"+"添加
                                            </div>
                                        )}
                                    </div>
                                ),
                            },
                            {
                                key: 'welcomeMessage',
                                label: <CollapseLabel text="欢迎语" onClick={() => {
                                }} plusDisabled/>,
                                children: <div>
                                    <DebouncedTextArea
                                        disabled={!botSavePermission}
                                        value={welcomeMessage}
                                        onChange={(value) => {

                                            if (!botSavePermission) {
                                                message.warning("你没有配置bot的权限！")
                                                return;
                                            }

                                            doUpdateBotOptions({welcomeMessage: value.target.value.length ? value.target.value : defaultWelcomeMessage})
                                        }}
                                        onChangeImmediately={(event) => {

                                            if (!botSavePermission) {
                                                message.warning("你没有配置bot的权限！")
                                                return;
                                            }

                                            setWelcomeMessage(event.target.value ? event.target.value : defaultWelcomeMessage)
                                        }}
                                        placeholder="请输入欢迎语"
                                        autoSize={{minRows: 2, maxRows: 6}}
                                    />
                                </div>,
                            },

                        ]} bordered={false}/>


                        <div style={{margin: "20px 0 5px", color: "#666"}}>发布</div>
                        <Collapse items={[
                            {
                                key: 'embed',
                                label: <CollapseLabel text="嵌入" onClick={() => {
                                }} plusDisabled/>,
                                children: <div>
                                    <div style={{
                                        display: 'flex',
                                        justifyContent: 'space-between',
                                        marginBottom: '10px'
                                    }}>
                                        <span>启用匿名访问</span>
                                        <Switch
                                            checked={anonymousEnabled}
                                            loading={anonymousSwitchLoading}
                                            onChange={async (checked) => {

                                                if (!botSavePermission) {
                                                    message.warning("你没有配置bot的权限！")
                                                    setAnonymousEnabled(false)
                                                    return;
                                                }

                                                console.log(1)

                                                setAnonymousSwitchLoading(true)
                                                const resp = await updateBotOptions({
                                                    data: {
                                                        options: {anonymousEnabled: checked},
                                                        id: params.id,
                                                    }
                                                })
                                                if (resp?.data?.errorCode === 0){
                                                    const reGetResp = await reGetDetail();
                                                    setAnonymousEnabled(reGetResp.data?.data?.options.anonymousEnabled)
                                                }

                                                setAnonymousSwitchLoading(false)
                                            }}
                                        />
                                    </div>
                                    <div>
                                        <span>
                                            外部访问地址 <a
                                            href={window.location.href.substring(0, window.location.href.indexOf('/ai')) + '/ai/externalBot/' + detail?.data?.id}
                                            target={"_blank"}>打开</a>
                                        </span>
                                        <TextArea readOnly disabled style={{resize:"none"}}
                                                  value={window.location.href.substring(0, window.location.href.indexOf('/ai')) + '/ai/externalBot/' + detail?.data?.id}></TextArea>
                                    </div>
                                </div>,
                            },
                            {
                                key: 'api',
                                label: <CollapseLabel text="API" onClick={() => {
                                }} plusDisabled/>,
                                children: <div>
                                    <div>
                                        <span>
                                            使用手册请参考 <a
                                            href={'https://aiflowy.tech/zh/development/ai/apiKey.html'}
                                            target={"_blank"}>打开</a>
                                        </span>
                                        <TextArea readOnly disabled style={{resize:"none"}}
                                                  value={'https://aiflowy.tech/zh/development/ai/apiKey.html'}></TextArea>
                                    </div>
                                </div>
                            },
                        ]} bordered={false}/>

                    </Col>


                    <Col span={span} style={colStyle}>
                        <Title level={5}>预览</Title>
                        <div style={{height: "calc(100vh - 122px)", width: "100%"}} className={"bot-chat"}>
                            <AiProChat
                                chats={chats}
                                prompts={presetQuestions}
                                onChatsChange={setChats} // 确保正确传递 onChatsChange
                                // style={{ height: '600px' }}
                                llmDetail={detail?.data}
                                helloMessage={detail?.data?.options?.welcomeMessage}
                                clearMessage={clearMessage}
                                request={async (messages) => {
                                    const readableStream = new ReadableStream({
                                        async start(controller) {
                                            const encoder = new TextEncoder();
                                            startChat({
                                                data: {
                                                    botId: params.id,
                                                    sessionId: getSessionId(),
                                                    prompt: messages[messages.length - 1].content as string,
                                                    fileList:messages[messages.length - 1].files as Array<string>
                                                },
                                                onMessage: (msg) => {
                                                    controller.enqueue(encoder.encode(msg))
                                                },
                                                onFinished: () => {
                                                    controller.close()
                                                }
                                            })
                                        },
                                    });
                                    return new Response(readableStream);
                                }}
                            />
                        </div>
                    </Col>
                </Row>
            </div>

            <Modal
                title="问题预设"
                closable={{ 'aria-label': 'Custom Close Button' }}
                open={isOpenProblemPreset}
                onCancel={() => setIsOpenProblemPreset(false)}
                footer={null}
                afterOpenChange={(open) => {
                    if (open) {
                        const values: Record<string, string> = {};
                        [1, 2, 3, 4, 5].forEach((num: number) => {
                            values[`question${num}`] = presetQuestions[num - 1]?.description || '';
                        });
                        form.setFieldsValue(values);
                    }
                }}
            >
                <Form layout="vertical" form={form}
                      disabled={!botSavePermission}
                      onFinish={(values: Record<string, string>) => {

                          if (!botSavePermission) {
                              message.warning("你没有配置bot的权限!")
                              return;

                          }

                          // 将表单值转换为 PresetQuestion[] 类型
                          const questions = Object.entries(values)
                              .filter(([_key, value]) => value) // 过滤空值
                              .map(([_key, value], index) => ({
                                  key: (index + 1).toString(),
                                  description: value,
                                  // icon: '<ProductOutlined />',
                              }));
                          updateBotOptions({
                              data: {
                                  options: {presetQuestions: questions},
                                  id: params.id
                              }
                          }).then((res) => {
                              if (res.data.errorCode === 0) {
                                  setIsOpenProblemPreset(false);
                                  reGetDetail()
                              } else {
                                  message.error('保存失败')
                              }
                          });
                      }}>
                    {[1, 2, 3, 4, 5].map((num) => (
                        <Form.Item
                            key={num}
                            label={`预设问题 ${num}`}
                            name={`question${num}`}
                            initialValue={presetQuestions[num-1]?.description || ''}
                        >
                            <Input />
                        </Form.Item>
                    ))}
                    <Form.Item label={null}>
                        <Space style={{ display: 'flex', justifyContent: 'flex-end' }}>
                            <Button onClick={handleProblemCancel}>取消</Button>
                            <Button type="primary" htmlType="submit" style={{ marginRight: 8 }}>
                                确定
                            </Button>
                        </Space>
                    </Form.Item>
                </Form>
            </Modal>
        </>
    )
        ;
};

export default {
    path: "/ai/bot/design/:id",
    element: BotDesign
};
