import React, {JSX, useCallback, useEffect, useRef, useState} from "react";
import {Button, Dropdown, MenuProps, message, Popconfirm, Space, Table, TableProps, Tooltip} from "antd";
import {ColumnGroupType, ColumnType, SorterResult, TableRowSelection} from "antd/es/table/interface";
import SearchForm from "./SearchForm";
import {
    ColumnHeightOutlined,
    DeleteOutlined,
    DownloadOutlined,
    EditOutlined,
    EyeOutlined, FormatPainterOutlined,
    ReloadOutlined,
    RestOutlined, SwapOutlined
} from "@ant-design/icons";
import EditForm, {EditFormProps, EditLayout} from "./EditForm.tsx";
import {useReactToPrint} from "react-to-print";
import DictItems from "./DictItems.tsx";
import {Rule} from "antd/es/form/index";
import {
    convertObjetToAttrs,
    getObjectKeyIfHasChildren,
    removeIf
} from "../../libs/utils.ts";
import {Key} from "rc-table/lib/interface";
import {LabelTooltipType} from "antd/es/form/FormItemLabel";

export type DictConfig = {
    url?: string,
    name?: string,
    asTree?: boolean,
    paramKeys?: string[],
    disabledItemAndChildrenKey?: string,
    editCondition?: (item: any) => boolean,
    editExtraData?: any[]
}


export type FormItemConfig = {
    //组件的类型
    type?: string,

    //是否包裹 col 组件，如果包裹，由 DynamicFormItem 自行包裹
    wrapCol?: boolean,

    //组件的其他属性
    attrs?: any,

    //验证规则
    rules?: Rule[],

    //备注说明
    extra?: string | JSX.Element,

    //提示信息，参考： https://ant-design.antgroup.com/components/form-cn#formitem
    tooltip?: LabelTooltipType
}


export type ColumnGroup = {
    key: string,
    title: string,
}


export type ColumnConfig<RecordType = unknown> = ((ColumnGroupType<RecordType> | ColumnType<RecordType>) & {
    //编辑类型
    form?: FormItemConfig,

    //数据字典，配置后，自动去请求后台的数据字典 url 来填充数据
    dict?: string | DictConfig,

    //分组的 key
    groupKey?: string,

    //占位字符
    placeholder?: string,

    //是否支持搜索
    supportSearch?: boolean,

    onValuesChange?: (changedValues: any, allValues: any) => void,

    editCondition?: (data: any) => boolean,
});

export type ColumnsConfig<RecordType = unknown> = ColumnConfig<RecordType> [];

export type Actions<T> = {
    //获取数据列表
    onFetchList?: (pageNumber?: number, pageSize?: number, searchParams?: any, sortKey?: string, sortType?: "asc" | "desc") => void,

    //获取数据详情
    onFetchDetail?: (row: T) => T,

    //删除单条数据
    onDelete?: (row: T) => void,

    //批量删除数据
    onDeleteBatch?: (rows: T[]) => void,

    //数据更新
    onUpdate?: (row: T) => void,

    //数据创建
    onCreate?: (row: T) => void,

    // //初始化搜索框的值
    // onSearchValueInit?: (key: string) => any
}

/**
 * 操作项配置
 */
export type ActionConfig<T> = {
    hidden?: boolean,
    title?: string,
    fixedRight?: boolean,
    width?: number | string,
    detailButtonEnable?: boolean,
    editButtonEnable?: boolean,
    deleteButtonEnable?: boolean,
    customActions?: (data: T) => JSX.Element | null
}

export type AntdCrudProps<T> = {

    //列配置
    columns: ColumnsConfig<T>,

    //分组显示
    groups?: ColumnGroup[],

    //操作方法
    actions: Actions<T>,

    //操作配置
    actionConfig?: ActionConfig<T>,

    //自定义form 表单渲染器
    formRenderFactory?: (position: "edit" | "search", columnConfig: ColumnConfig) => JSX.Element | null

    customButton?: () => React.ReactNode | null,

    //数据源
    dataSource: any,

    pageNumber?: number,

    pageSize?: number,

    paginationHidden?: boolean,

    totalRow?: number,

    loading?: boolean,

    defaultExpandedAllRow?: boolean,

    tableAttrs?: TableProps<any>,

    addButtonEnable?: boolean,

    rowSelectEnable?: boolean,

    intelligentFilling?: EditFormProps<any>['intelligentFilling'],

    initSearchParams?: any,

    onSearchValueInit?: (key: string) => any,

    editLayout?: EditLayout,
}


/**
 * 下载 excel 数据
 * @param columns
 * @param dataSource
 */
function download(columns: ColumnsConfig<any>, dataSource: any[]) {
    let cvs = '';
    columns.map((column) => {
        cvs += `${column.title}, `;
    })
    cvs += '\r\n'

    dataSource.map((data) => {
        columns.map((column) => {
            cvs += `${data[column.key as string]}, `;
        })
        cvs += '\r\n'
    });

    const _utf = "\uFEFF"; // 为了使Excel以utf-8的编码模式，同时也是解决中文乱码的问题
    const url = 'data:application/csv;charset=utf-8,' + _utf + encodeURIComponent(cvs);
    const link = document.createElement("a");
    link.href = url;
    link.style.cssText = "visibility:hidden";
    link.download = "data.csv";
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}


function AntdCrud<T>({
                         columns,
                         groups,
                         dataSource,
                         actions,
                         actionConfig,
                         formRenderFactory,
                         customButton,
                         pageNumber = 1,
                         pageSize = 10,
                         totalRow = 0,
                         loading,
                         paginationHidden = false,
                         tableAttrs = {},
                         defaultExpandedAllRow = true,
                         addButtonEnable = true,
                         rowSelectEnable = true,
                         intelligentFilling,
                         initSearchParams,
                         onSearchValueInit,
                         editLayout,
                     }: AntdCrudProps<T>) {

    const tableRef = useRef<any>(null);

    const handlePrint = useReactToPrint({
        documentTitle: "表格打印",
        contentRef: tableRef,
        pageStyle: `@page {padding-top:10px;} @media print {body { font-family: "SimSun", "宋体", serif;}}`,
        // copyShadowRoots: false,
        // content: () => tableRef.current!,
    });

    dataSource = convertObjetToAttrs(dataSource);

    const [messageApi, contextHolder] = message.useMessage();

    const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
    const [selectedRows, setSelectedRows] = useState<T[]>([]);
    const [selectCount, setSelectCount] = useState(0);
    const [modalTitle, setModalTitle] = useState("");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalRow, setModalRow] = useState<T | null>(null);
    const [confirmLoading, setConfirmLoading] = useState(false);
    const [tableSize, setTableSize] = useState<"small" | "middle" | "large">("middle");
    const [formItemDisabled, setFormItemDisabled] = useState<boolean>(false);
    const [defaultExpandedRowKeys, setDefaultExpandedRowKeys] = useState<Key[]>([]);

    const [localPageNumber, setLocalPageNumber] = useState(pageNumber)
    const [localPageSize, setLocalPageSize] = useState(pageSize)

    const [searchParams, setSearchParams] = useState(initSearchParams)
    const [sortKey, setSortKey] = useState<string | undefined>()
    const [sortType, setSortType] = useState<"asc" | "desc" | undefined>()

    actionConfig = {
        title: '操作',
        fixedRight: true,
        width: 210,
        detailButtonEnable: false,
        editButtonEnable: true,
        deleteButtonEnable: true,
        ...actionConfig
    } as ActionConfig<T>

    const selectNone = () => {
        setSelectedRowKeys([]);
        setSelectedRows([]);
        setSelectCount(0);
    };

    useEffect(() => {
        if (defaultExpandedAllRow) {
            const keys = getObjectKeyIfHasChildren(dataSource);
            setDefaultExpandedRowKeys(keys)
        }
    }, [dataSource, defaultExpandedAllRow])


    const tableColumns = actionConfig.hidden ? columns : columns.concat({
        title: actionConfig.title,
        key: 'action',
        fixed: actionConfig.fixedRight ? 'right' : undefined,
        width: actionConfig.width,
        render: (_, row) => (
            <Space size="middle">

                {actionConfig?.customActions && actionConfig.customActions(row)}
                {actionConfig?.detailButtonEnable && <a onClick={() => {
                    setModalRow(row)
                    setModalTitle("查看")
                    setIsModalOpen(true)
                    setFormItemDisabled(true)
                }}> <EyeOutlined/> 查看 </a>}


                {actionConfig?.editButtonEnable && <a onClick={() => {
                    setModalRow(row)
                    setModalTitle("编辑")
                    setIsModalOpen(true)
                }}> <EditOutlined/> 编辑 </a>}


                {actionConfig?.deleteButtonEnable && <Popconfirm
                    title="确定删除？"
                    description="您确定要删除这条数据吗？"
                    okButtonProps={{loading: confirmLoading}}
                    onConfirm={() => {
                        setConfirmLoading(true)
                        actions.onDelete && actions.onDelete(row)
                        setConfirmLoading(false)
                        doRefresh()
                    }}
                    okText="确定"
                    cancelText="取消"
                >
                    <a> <RestOutlined/> 删除 </a>
                </Popconfirm>}

            </Space>
        ),
    });

    tableColumns.forEach((columnConfig) => {
        if (columnConfig.dict && !columnConfig["render"]) {
            // columnConfig["shouldCellUpdate"] = () => false;//防止 state 改变时进行全表渲染
            columnConfig["render"] = (_text, data) => {
                return (
                    <DictItems columnConfig={columnConfig} columnData={data}/>
                )
            }
        }
    });


    useEffect(() => {
        actions.onFetchList?.(localPageNumber, localPageSize, searchParams, sortKey, sortType);
    }, [localPageNumber, localPageSize, searchParams, sortKey, sortType])


    const items: MenuProps['items'] = [
        {
            key: '1',
            label: '宽松',
            onClick: () => {
                setTableSize("large")
            }
        },
        {
            key: '2',
            label: '一般',
            onClick: () => {
                setTableSize("middle")
            }
        },
        {
            key: '3',
            label: '紧凑',
            onClick: () => {
                setTableSize("small")
            }
        },
    ];

    const doRefresh = useCallback(() => {
        setTimeout(() => {
            actions.onFetchList?.(localPageNumber, localPageSize, searchParams, sortKey, sortType);
        }, 200)
    }, []);

    return (
        <div>
            {contextHolder}
            <SearchForm columns={columns} colSpan={6}
                        onSearch={(values: any) => {
                            setLocalPageNumber(1)
                            setSearchParams(values)
                        }}
                        onSearchValueInit={onSearchValueInit}
                        formRenderFactory={formRenderFactory}
            />

            <EditForm title={modalTitle}
                      intelligentFilling={intelligentFilling}
                      columns={columns}
                      open={isModalOpen}
                      groups={groups}
                      onSubmit={() => {
                          setIsModalOpen(false)
                      }}
                      onCancel={() => {
                          setIsModalOpen(false)
                          setFormItemDisabled(false)
                          setModalRow(null)
                      }}
                      actions={actions}
                      onActionsInvokeAfter={doRefresh}
                      formRenderFactory={formRenderFactory}
                      row={modalRow as T}
                      formReadOnly={formItemDisabled}
                      layout={editLayout}
            />

            <Space style={{display: "flex", justifyContent: "space-between", padding: "10px"}}>
                <Space align={"center"}>

                    {customButton?.()}

                    {addButtonEnable && <Button type="primary" onClick={() => {
                        setModalRow(null);
                        setModalTitle("新增")
                        setIsModalOpen(!isModalOpen)
                    }}>新增</Button>}

                    {selectCount > 0 &&
                        <div style={{
                            border: "1px solid #abdcff",
                            borderRadius: "6px",
                            padding: "0 10px",
                            margin: "-1px",
                            background: "#f0faff",
                            height: "32px",
                            display: "flex",
                            fontSize: "14px"
                        }}>
                            <Space>
                                <div>
                                    已选择 {selectCount} 项
                                </div>
                                <Popconfirm
                                    title="确定删除？"
                                    description="您确定要全部删除所选的数据吗？"
                                    okButtonProps={{loading: confirmLoading}}
                                    onConfirm={() => {
                                        setConfirmLoading(true)
                                        actions.onDeleteBatch && actions.onDeleteBatch(selectedRows);
                                        setConfirmLoading(false)
                                        doRefresh();
                                    }}
                                    okText="确定删除"
                                    cancelText="取消"
                                >
                                    <a style={{color: "red"}}>
                                        <DeleteOutlined/>全部删除
                                    </a>

                                </Popconfirm>

                                <a style={{paddingLeft: "20px"}} onClick={selectNone}> 取消选择 </a>
                            </Space>
                        </div>
                    }
                </Space>

                <Space align={"center"} size={"middle"}>
                    <Tooltip placement="top" title="刷新">
                        <ReloadOutlined onClick={() => {
                            actions.onFetchList?.(pageNumber, pageSize, searchParams, sortKey, sortType);
                        }}/>
                    </Tooltip>

                    <Tooltip placement="top" title="导出">
                        <DownloadOutlined onClick={() => {
                            download(columns, dataSource);
                        }}/>
                    </Tooltip>

                    <Tooltip placement="top" title="行高">
                        <div>
                            <Dropdown menu={{items}} placement="bottom" trigger={["click"]} arrow>
                                <ColumnHeightOutlined/>
                            </Dropdown>
                        </div>

                    </Tooltip>

                    <Tooltip placement="top" title="打印">
                        <FormatPainterOutlined onClick={() => {
                            messageApi.success("打印数据准备中...");
                            handlePrint()
                        }}/>
                    </Tooltip>

                    <Tooltip placement="top" title="列设置">
                        <SwapOutlined/>
                    </Tooltip>
                </Space>

            </Space>

            <div ref={tableRef}>
                <Table columns={tableColumns}
                       dataSource={dataSource}
                       rowKey="id"
                       expandable={{
                           expandedRowKeys: defaultExpandedRowKeys,
                           onExpand: (expanded, record) => {
                               if (expanded) {
                                   setDefaultExpandedRowKeys(defaultExpandedRowKeys.concat(record["id"]))
                               } else {
                                   setDefaultExpandedRowKeys(removeIf(defaultExpandedRowKeys, (key) => {
                                       return key == record["id"];
                                   }))
                               }
                           }
                       }}
                       size={tableSize}
                       loading={loading}
                       onChange={(pagination, _, sorter) => {
                           setLocalPageNumber(pagination.current || 1);
                           setLocalPageSize(pagination.pageSize || pageSize)
                           if (sorter) {
                               const result = sorter as SorterResult<any>;
                               setSortKey(result.field as string);
                               if (result.order) {
                                   setSortType(result.order === "ascend" ? "asc" : "desc")
                               } else {
                                   setSortKey(undefined)
                                   setSortType(undefined)
                               }
                           }
                       }}
                       rowSelection={rowSelectEnable ? {
                           type: 'checkbox',
                           selectedRowKeys,
                           onChange: (selectedRowKeys: React.Key[], selectedRows: T[]) => {
                               setSelectedRows(selectedRows);
                               setSelectedRowKeys([...selectedRowKeys]);
                               setSelectCount(selectedRows.length);
                           }
                       } as TableRowSelection<any> : undefined}
                       pagination={
                           {
                               position: [paginationHidden ? "none" : "bottomCenter"],
                               pageSize: pageSize,
                               showQuickJumper: true,
                               current: pageNumber,
                               total: totalRow || 0,
                               showTotal: (total) => `共 ${total} 条数据`,
                           }
                       }

                       {...tableAttrs}
                />
            </div>
        </div>
    )
}

export default AntdCrud
