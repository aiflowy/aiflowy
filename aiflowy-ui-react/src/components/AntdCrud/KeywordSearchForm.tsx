import React from 'react'; // 移除 useState 依赖
import { Button, Col, Dropdown, Form, Input, Row } from 'antd';
import { EllipsisOutlined, PlusOutlined } from '@ant-design/icons';
import { ColumnsConfig } from "./index.tsx";
import { useCheckPermission } from "../../hooks/usePermissions.tsx";

interface KeywordSearchFormProps {
    tableAlias?: string;
    onSearch: (params: Record<string, string>) => void,
    placeholder?: string,
    resetText?: string,
    columns: ColumnsConfig<any>,
    addButtonText?: string,
    customHandleButton?: React.ReactNode,
    setIsEditOpen?: (open: boolean) => void,
    customMenuItems?: any[],
    hiddenAddButton?: boolean
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
                                                                 customMenuItems,
                                                                 hiddenAddButton = false
                                                             }) => {
    const [form] = Form.useForm();
    const hasSavePermission = useCheckPermission(`/api/v1/${tableAlias}/save`);

    // 🌟 改用 Form 内置方法获取输入值
    const onFinish = () => {
        form.validateFields().then(values => {
            const trimmedKeywords = (values.keywords || '').trim();
            if (trimmedKeywords) {
                const searchParams: Record<string, string> = {};
                columns.forEach(column => {
                    if (column.supportSearch && column.key) {
                        searchParams[column.key as string] = trimmedKeywords;
                    }
                });
                searchParams['isQueryOr'] = String(true);
                onSearch(searchParams);
            }
        });
    };

    // 🌟 重置逻辑：仅调用 Form 的 resetFields，自动同步输入框
    const resetSearch = () => {
        form.resetFields(['keywords']); // 重置指定字段
        onSearch({});
    };

    return (
        <Form
            name="keyword_search"
            form={form}
            onFinish={onFinish}
            initialValues={{ keywords: '' }} // 初始化值
            style={{ maxWidth: 'none'}}
        >
            <Row>
                <Col span={6}>
                    <Form.Item
                        name="keywords"
                        rules={[{ required: false, message: '请输入搜索关键词' }]}
                    >
                        <Input
                            placeholder={placeholder}
                        />
                    </Form.Item>
                </Col>
                <Col>
                    <div style={{ marginLeft: 8, marginRight: 8, display: 'flex', alignItems: 'center', gap: 8 }}>
                        <Button onClick={onFinish} type="primary">
                            搜索
                        </Button>
                        <Button onClick={resetSearch}>{resetText}</Button>
                    </div>
                </Col>
                <div style={{ flex: 1 }}>
                    <div style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: 8,
                        marginLeft: 8,
                        justifyContent: 'flex-end',
                        flex: 1
                    }}>
                        {hasSavePermission && !hiddenAddButton &&
                            <Button type="primary" onClick={() => setIsEditOpen?.(true)}>
                                <PlusOutlined /> {addButtonText}
                            </Button>
                        }
                        {customHandleButton}
                        {customMenuItems && customMenuItems.length > 0 && (
                            <div >
                                <Dropdown menu={{ items: customMenuItems }} placement="bottomLeft" >
                                    <Button style={{ width: 32, height: 32}}>
                                        <EllipsisOutlined />
                                    </Button>
                                </Dropdown>
                            </div>
                        )}
                    </div>
                </div>
            </Row>
        </Form>
    );
};

export default KeywordSearchForm;