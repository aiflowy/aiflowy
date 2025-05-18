import React, {forwardRef, useEffect, useImperativeHandle, useState} from 'react';
import {Table, Input, Select, Button, Space, Form, Switch, Tooltip} from 'antd';
import type {ExpandableConfig} from 'antd/es/table/interface';
import {DeleteOutlined, PlusOutlined} from '@ant-design/icons';

export interface TreeTableNode {
    key: string;
    name: string;
    description: string;
    method?: 'Query' | 'Body' | 'Path' | 'Header';
    required?: boolean;
    defaultValue?: string;
    enabled?: boolean;
    type?: any;
    children?: TreeTableNode[];
}

interface PluginInputDataProps {
    value?: TreeTableNode[],
    onChange?: (value: TreeTableNode[]) => void,
    onSubmit?: (value: TreeTableNode[]) => void,
    editable?: boolean,
    isEditOutput?: boolean,
    submitParams?: () => void,
}
export interface PluginInputDataRef {
    handleSubmitParams: () => void;
}
const PluginInputAndOutputData: React.ForwardRefRenderFunction<PluginInputDataRef, PluginInputDataProps> = ({
                                                             value = [],
                                                             onChange,
                                                             onSubmit,
                                                             editable = false,
                                                             isEditOutput = false
                                                         }, ref) => {
    const [data, setData] = useState<TreeTableNode[]>(value);
    useEffect(() => {
        if (value) {
            setData([...value]); // 或者 cloneDeep(value)
        }
    }, [value]);
    const [expandedKeys, setExpandedKeys] = useState<React.Key[]>(['1']);
    const [errors, setErrors] = useState<Record<string, Partial<Record<keyof TreeTableNode, string>>>>({});
    const updateData = (newData: TreeTableNode[]) => {
        setData(newData);
        if (onChange) {
            onChange(newData);
        }
    };
    const handleSubmitParams = () => {
        if (!validateFields()) return;
        if (onSubmit) {
            onSubmit(data); // 这里会传给父组件
        }
    };
    // 暴露方法给父组件
    useImperativeHandle(ref, () => ({
        handleSubmitParams,
    }));

    const addNewRootNode = () => {
        if (!editable) return;
        const newKey = `${Date.now()}`;
        const newNode: TreeTableNode = {
            key: newKey,
            name: '',
            description: '',
            enabled: true,
            type: 'String',
            // 动态添加 method 字段
            ...(isEditOutput ? {} : { method: 'Query', defaultValue: '', required: false, }),
        };

        const newData = [...data, newNode];
        updateData(newData);
    };

    const onExpand = (expanded: boolean, record: TreeTableNode) => {
        const keys = expanded
            ? [...expandedKeys, record.key]
            : expandedKeys.filter(key => key !== record.key);
        setExpandedKeys(keys);
    };


    const handleAddChild = (parentKey: any) => {
        if (!editable || !parentKey) return;
        const newChild: TreeTableNode = {
            key: `${parentKey}-${Date.now()}`,
            name: '',
            description: '',
            required: false,
            enabled: true,
            type: 'String',
            ...(isEditOutput ? {} : { method: 'Query', defaultValue: '', }),
        };
        const addChildToNode = (nodes: TreeTableNode[]): TreeTableNode[] =>
            nodes.map(node => {
                if (node.key === parentKey) {
                    return {
                        ...node,
                        children: [...(node.children || []), newChild],
                    };
                }
                if (node.children) {
                    return {
                        ...node,
                        children: addChildToNode(node.children),
                    };
                }
                return node;
            });
        const newData = addChildToNode(data);
        updateData(newData);
        if (!expandedKeys.includes(parentKey)) {
            setExpandedKeys([...expandedKeys, parentKey]);
        }
    };

    const deleteNode = (key: string) => {
        if (!editable) return;
        const removeNodeRecursively = (nodes: TreeTableNode[]): TreeTableNode[] =>
            nodes.filter(node => {
                if (node.key === key) return false;
                if (node.children)
                    node.children = removeNodeRecursively(node.children);
                return true;
            });
        const newData = removeNodeRecursively(data);
        updateData(newData);
    };

    const validateFields = (): boolean => {
        const newErrors: Record<string, Partial<Record<keyof TreeTableNode, string>>> = {};
        let isValid = true;

        const checkNode = (node: TreeTableNode): boolean => {
            const {name, description, method, type} = node;
            const nodeErrors: Partial<Record<keyof TreeTableNode, string>> = {};

            if (!name?.trim()) {
                nodeErrors.name = '参数名称不能为空';
                isValid = false;
            }

            if (!description?.trim()) {
                nodeErrors.description = '参数描述不能为空';
                isValid = false;
            }
            if ((isRootNode(node) && !method) && !isEditOutput) {
                nodeErrors.method = '传入方法不能为空';
                isValid = false;
            }

            if (!type) {
                nodeErrors.type = '参数类型不能为空';
                isValid = false;
            }

            if (Object.keys(nodeErrors).length > 0) {
                newErrors[node.key] = nodeErrors;
            }

            if (node.children) {
                node.children.forEach(child => {
                    if (!checkNode(child)) isValid = false;
                });
            }

            return isValid;
        };

        data.forEach(node => {
            if (!checkNode(node)) isValid = false;
        });

        setErrors(newErrors);

        return isValid;
    };

    const isRootNode = (record: TreeTableNode) => {
        return !record.key.includes('-');
    };

    // @ts-ignore
    const columns = [
        {
            title: <span>参数名称<span style={{color: 'red'}}>*</span></span>,
            dataIndex: 'name',
            key: 'name',
            width: '20%',
            onCell: () => ({style: {paddingLeft: 2}}),
            // @ts-ignore
            render: (text: string, record: TreeTableNode) => {
                const fieldError = errors[record.key]?.name;
                const level = String(record.key).split('-').length - 1;
                const indentSize = 2;

                if (!editable) {
                    return (
                        <div style={{paddingLeft: level * indentSize}}>
                            {record.name || ''}
                        </div>
                    );
                }

                const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
                    const value = e.target.value;
                    const updateNode = (nodes: TreeTableNode[]): TreeTableNode[] =>
                        nodes.map(node => {
                            if (node.key === record.key) {
                                return {...node, name: value};
                            }
                            if (node.children) {
                                return {...node, children: updateNode(node.children)};
                            }
                            return node;
                        });
                    const newData = updateNode(data);
                    updateData(newData);
                };
                // if (record.type ==== 'Array'){
                //
                // }
                return (
                    <div style={{
                        display: 'flex',
                        position: 'relative',
                        flexDirection: "column",
                        justifyContent: "flex-start"
                    }}>
                        <div style={{display: 'flex', alignItems: "center"}}>
                            <div style={{width: level * indentSize}}></div>
                            <Input
                                variant="filled"
                                value={record.name || ''}
                                onChange={handleChange}
                                size="middle"
                                disabled={record.name === 'arrayItem'}
                                style={{flex: 1}}
                            />

                        </div>
                        {fieldError && <div style={{
                            color: 'red',
                            fontSize: '12px',
                            top: '90%',
                            marginLeft: level * indentSize,
                            position: 'absolute',
                        }}>{fieldError}{level}</div>}
                    </div>
                );
            },
        },
        {
            title: <span>参数描述<span style={{color: 'red'}}>*</span></span>,
            dataIndex: 'description',
            key: 'description',
            width: '20%',
            // @ts-ignore
            render: (text: string, record: TreeTableNode) => {
                const fieldError = errors[record.key]?.description;

                if (!editable) {
                    return <span>{record.description || ''}</span>;
                }

                const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
                    const value = e.target.value;
                    const updateNode = (nodes: TreeTableNode[]): TreeTableNode[] =>
                        nodes.map(node => {
                            if (node.key === record.key) {
                                return {...node, description: value};
                            }
                            if (node.children) {
                                return {...node, children: updateNode(node.children)};
                            }
                            return node;
                        });
                    const newData = updateNode(data);
                    updateData(newData);
                };

                return (
                    <div style={{position: 'relative'}}>
                        <Input
                            variant="filled"
                            size="middle"
                            value={record.description || ''}
                            onChange={handleChange}
                            disabled={!editable}
                        />
                        {fieldError && <div style={{
                            color: 'red',
                            fontSize: '12px',
                            position: 'absolute',
                            top: '90%'
                        }}>{fieldError}</div>}
                    </div>
                );
            },
        },
        {
            title: <span>参数类型<span style={{color: 'red'}}>*</span></span>,
            dataIndex: 'type',
            key: 'type',
            width: '10%',
            // @ts-ignore
            render: (text: string, record: TreeTableNode) => {
                const fieldError = errors[record.key]?.type;
                const handleChange = (value: 'String' | 'Number' | 'Object' | 'Boolean' | 'Array' | 'Array[String]' |
                    'Array[Number]' | 'Array[Boolean]' | 'Array[Integer]' | 'Array[Object]') => {
                    if (!editable) return;

                    const updateNode = (nodes: TreeTableNode[]): TreeTableNode[] =>
                        nodes.map(node => {
                            if (node.key === record.key) {
                                // 如果是 String 或 Number，移除 children
                                if (value === 'String' || value === 'Number' || value === 'Boolean' || value === 'Array[String]'
                                    || value === 'Array[Number]' || value === 'Array[Boolean]' || value === 'Array[Integer]' || 'Array[Object]') {
                                    return {
                                        ...node,
                                        type: value,
                                        children: undefined, // 移除 children
                                    };
                                }
                                // 如果是 Object 或 Array，保留或初始化 children
                                return {
                                    ...node,
                                    type: value,
                                    children: node.children || [], // 保留现有 children 或初始化为空数组
                                };
                            }
                            if (node.children) {
                                return {
                                    ...node,
                                    children: updateNode(node.children),
                                };
                            }
                            return node;
                        });

                    const newData = updateNode(data);
                    updateData(newData);

                    // 如果是 Object 或 Array，添加默认子节点并展开
                    if (value === 'Object' || value === 'Array' || value === 'Array[Object]') {
                        const newChild: TreeTableNode = {
                            key: `${record.key}-${Date.now()}`, // 唯一 key
                            name: value === 'Array' ? 'arrayItem' : '', // Array 类型子节点固定名称为 arrayItem
                            description: '',
                            enabled: true,
                            ...(isEditOutput ? {} : { method: 'Query', defaultValue: '', required: false, }),
                            type: value === 'Array' ? 'Array[String]' : 'String', // Array 的子节点默认类型为 Array[String]
                        };

                        const addChildToNode = (nodes: TreeTableNode[]): TreeTableNode[] =>
                            nodes.map(node => {
                                if (node.key === record.key) {
                                    return {
                                        ...node,
                                        children: [newChild],
                                    };
                                }
                                if (node.children) {
                                    return {
                                        ...node,
                                        children: addChildToNode(node.children),
                                    };
                                }
                                return node;
                            });

                        const finalData = addChildToNode(newData);
                        updateData(finalData);

                        // 自动展开父节点
                        if (!expandedKeys.includes(record.key)) {
                            setExpandedKeys([...expandedKeys, record.key]);
                        }
                    }
                };
                if (!editable) {
                    return <span>{record.type || ''}</span>;
                }
                return (
                    <Form.Item style={{margin: 0}}>
                        <Select
                            value={record.type || 'String'}
                            variant="filled"
                            onChange={handleChange}
                            options={record.name === 'arrayItem' ?
                                [
                                    {label: 'Array[String]', value: 'Array[String]'},
                                    {label: 'Array[Number]', value: 'Array[Number]'},
                                    {label: 'Array[Boolean]', value: 'Array[Boolean]'},
                                    {label: 'Array[Object]', value: 'Array[Object]'}
                                ]
                                :
                                [
                                    {label: 'String', value: 'String'},
                                    {label: 'Boolean', value: 'Boolean'},
                                    {label: 'Number', value: 'Number'},
                                    {label: 'Object', value: 'Object'},
                                    {label: 'Array', value: 'Array'},
                                ]
                            }
                            size="middle"
                            disabled={!editable}
                        />
                        {fieldError && <div style={{color: 'red', fontSize: '12px'}}>{fieldError}</div>}
                    </Form.Item>
                );
            },
        },

        ...(!isEditOutput
            ? [
                {
                    title: <span>传入方法<span style={{color: 'red'}}>*</span></span>,
                    dataIndex: 'method',
                    key: 'method',
                    width: '10%',
                    // @ts-ignore
                    render: (text: string, record: TreeTableNode) => {
                        const fieldError = errors[record.key]?.method;
                        // if (!isRootNode(record)) {
                        //     return <span>{record.method || ''}</span>;
                        // }
                        if (record.name === 'arrayItem') {
                            return <span>{''}</span>;
                        }
                        const handleChange = (value: 'Query' | 'Body' | 'Path' | 'Header') => {
                            if (!editable) return;
                            const updateNode = (nodes: TreeTableNode[]): TreeTableNode[] =>
                                nodes.map(node => {
                                    if (node.key === record.key) {
                                        return {...node, method: value};
                                    }
                                    if (node.children) {
                                        return {...node, children: updateNode(node.children)};
                                    }
                                    return node;
                                });
                            const newData = updateNode(data);
                            updateData(newData);
                        };
                        if (!editable) {
                            return <span>{record.method || ''}</span>;
                        }
                        return (
                            <Form.Item style={{margin: 0}}>
                                <Select
                                    value={record.method || 'Query'}
                                    onChange={handleChange}
                                    variant="filled"
                                    options={[
                                        {label: 'Query', value: 'Query'},
                                        {label: 'Body', value: 'Body'},
                                        {label: 'Path', value: 'Path'},
                                        {label: 'Header', value: 'Header'},
                                    ]}
                                    size="middle"
                                    disabled={!editable}
                                />
                                {fieldError && <div style={{color: 'red', fontSize: '12px'}}>{fieldError}</div>}
                            </Form.Item>
                        );
                    },
                },
                {
                    title: '是否必填',
                    dataIndex: 'required',
                    key: 'required',
                    width: '8%',
                    // @ts-ignore
                    render: (text: boolean, record: TreeTableNode) => {
                        const handleChange = (checked: boolean) => {
                            if (!editable) return;
                            const updateNode = (nodes: TreeTableNode[]): TreeTableNode[] =>
                                nodes.map(node => {
                                    if (node.key === record.key) {
                                        return { ...node, required: checked };
                                    }
                                    if (node.children) {
                                        return { ...node, children: updateNode(node.children) };
                                    }
                                    return node;
                                });
                            const newData = updateNode(data);
                            updateData(newData);
                        };
                        return <Switch checked={record.required} onChange={handleChange} disabled={!editable} />;
                    },
                },
                {
                    title: '默认值',
                    dataIndex: 'defaultValue',
                    key: 'defaultValue',
                    width: '12%',
                    // @ts-ignore
                    render: (text: string, record: TreeTableNode) => {
                        if (record.type === 'Object') {
                            return <span></span>;
                        }

                        const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
                            const value = e.target.value;
                            const updateNode = (nodes: TreeTableNode[]): TreeTableNode[] =>
                                nodes.map(node => {
                                    if (node.key === record.key) {
                                        return {...node, defaultValue: value};
                                    }
                                    if (node.children) {
                                        return {...node, children: updateNode(node.children)};
                                    }
                                    return node;
                                });
                            const newData = updateNode(data);
                            updateData(newData);
                        };
                        if (!editable) {
                            return <span>{record.defaultValue || ''}</span>;
                        }
                        return (
                            <Input
                                size="middle"
                                variant="filled"
                                value={record.defaultValue || ''}
                                onChange={handleChange}
                                disabled={!editable}
                            />
                        );
                    },
                },

            ]
            : []),



        {
            title: '启用状态',
            dataIndex: 'enabled',
            key: 'enabled',
            width: '8%',
            // @ts-ignore
            render: (text: boolean, record: TreeTableNode) => {
                const handleChange = (checked: boolean) => {
                    if (!editable) return;
                    const updateNode = (nodes: TreeTableNode[]): TreeTableNode[] =>
                        nodes.map(node => {
                            if (node.key === record.key) {
                                return {...node, enabled: checked};
                            }
                            if (node.children) {
                                return {...node, children: updateNode(node.children)};
                            }
                            return node;
                        });
                    const newData = updateNode(data);
                    updateData(newData);
                };
                return (
                    <Switch
                        checked={record.enabled}
                        onChange={handleChange}
                        disabled={!editable}
                    />
                );
            },
        },
        ...(editable
            ? [
                {
                    title: '操作',
                    key: 'action',
                    width: '12%',
                    render: (_: any, record: TreeTableNode) => (
                        <Space size="middle">
                            {(record.type === 'Object' || record.type === 'Array[Object]') && (
                                <Tooltip title="添加子节点" placement="top">
                                    <Button
                                        type="link"
                                        onClick={() => handleAddChild(record.key)}
                                        icon={<PlusOutlined/>}
                                        size="small"
                                    />
                                </Tooltip>
                            )}
                            <Button
                                type="link"
                                danger
                                icon={<DeleteOutlined/>}
                                onClick={() => deleteNode(record.key)}
                                size="small"
                            />
                        </Space>
                    ),
                },
            ]
            : []),
    ];

    const expandable: ExpandableConfig<TreeTableNode> = {
        expandedRowKeys: expandedKeys,
        onExpand: onExpand,
        rowExpandable: (record) => !!record.children && record.children.length > 0,
    };

    return (
        <>
            <Table
                columns={columns}
                expandable={expandable}
                dataSource={data}
                rowKey="key"
                pagination={false}
                size="middle"
                bordered
            />
            {editable && (
                <div style={{marginTop: 16, textAlign: 'left'}}>
                    {/*<Button type="primary" onClick={handleSubmitParams}>*/}
                    {/*    提交参数*/}
                    {/*</Button>*/}
                    <Button type="default" onClick={addNewRootNode}>
                        新增参数
                    </Button>
                </div>
            )}
        </>
    );
};

export default forwardRef(PluginInputAndOutputData);
