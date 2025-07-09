import React, {useState} from 'react';
import {Button, Col, Dropdown, Form, Input, Row} from 'antd';
import {EllipsisOutlined, PlusOutlined} from '@ant-design/icons';
import {ColumnsConfig} from "./index.tsx";
import {useCheckPermission} from "../../hooks/usePermissions.tsx";

interface KeywordSearchFormProps {
    tableAlias?: string;
    onSearch: (params: Record<string, string>) => void,
    placeholder?: string,
    resetText?: string,
    columns: ColumnsConfig<any>,
    addButtonText?: string,
    // 自定义按钮
    customHandleButton?: any[],
    setIsEditOpen: (open: boolean) => void,
    // 自定义下拉菜单
    customMenuItems?: any[]
}

const KeywordSearchForm: React.FC<KeywordSearchFormProps> = ({
                                                                 tableAlias,
                                                                 onSearch,
                                                                 placeholder = '请输入搜索关键词',
                                                                 resetText = '重置',
                                                                 columns,
                                                                 addButtonText = '新增',
                                                                 customHandleButton,
                                                                 setIsEditOpen,
                                                                 customMenuItems
                                                             }) => {
    const [form] = Form.useForm();
    const [keywords, setKeywords] = useState('');

    const hasSavePermission = useCheckPermission(`/api/v1/${tableAlias}/save`)

    const onFinish = () => {
        const trimmedKeywords = keywords.trim();
        if (trimmedKeywords) {
            // 构建包含所有支持搜索字段的键值对对象
            const searchParams: Record<string, string> = {};

            columns.forEach(column => {
                if (column.supportSearch && column.key) {
                    searchParams[column.key as string] = trimmedKeywords;
                }
            });

            searchParams['isQueryOr'] = String(true);
            onSearch(searchParams);
        }
    };

    const resetSearch = () => {
        setKeywords('');
        form.resetFields();
        onSearch({});
    };

    return (
        <Form
            name="keyword_search"
            form={form}
            onFinish={onFinish}
            initialValues={{keywords}}
            style={{maxWidth: 'none', padding: 8}}
        >
            <Row>
                <Col span={6}>
                    <Form.Item name="keywords" rules={[{required: false, message: '请输入搜索关键词'}]}>
                        <Input
                            placeholder={placeholder}
                            value={keywords}
                            onChange={(e) => setKeywords(e.target.value)}
                        />
                    </Form.Item>
                </Col>
                <Col>
                    <div style={{marginLeft: 8, marginRight: 8, display: 'flex', alignItems: 'center', gap: 8}}>
                        <Button onClick={onFinish} type="primary">
                            搜索
                        </Button>
                        <Button onClick={resetSearch}>{resetText}</Button>
                    </div>
                </Col>
                <div style={{flex: 1}}>
                    <div style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: 8,
                        marginLeft: 8,
                        marginRight: 8,
                        justifyContent: 'flex-end',
                        flex: 1
                    }}>

                        {hasSavePermission &&
                            <Button type="primary" onClick={() => {
                                setIsEditOpen(true)
                            }}>
                                <PlusOutlined/> {addButtonText}
                            </Button>
                        }
                        {customHandleButton}
                        {customMenuItems && customMenuItems.length > 0 && <div>
                            <Dropdown menu={{items: customMenuItems}} placement="bottomLeft">
                                <Button>
                                    <EllipsisOutlined/>
                                </Button>
                            </Dropdown>
                        </div>}

                    </div>
                </div>
            </Row>
        </Form>
    );
};

export default KeywordSearchForm;
