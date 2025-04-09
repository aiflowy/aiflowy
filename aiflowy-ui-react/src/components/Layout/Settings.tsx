import React, {useEffect} from 'react';
import {App, Button, Form, FormProps, Input, Modal, Tabs} from "antd";
import {useForm} from "antd/es/form/Form";
import {useGet, usePostManual} from "../../hooks/useApis.ts";
import "./settings.less"
import ImageUploader from "../ImageUploader";
import {useAppStore} from "../../store/appStore.ts";

const Profiles: React.FC = () => {

    const {loading: getLoading, result} = useGet("api/v1/sysAccount/myProfile");
    const {loading, doPost} = usePostManual("/api/v1/sysAccount/updateProfile");
    const {message} = App.useApp();
    const store = useAppStore();

    const [form] = useForm()

    const onFinish: FormProps['onFinish'] = (values) => {
        doPost({
            data: values
        }).then(resp => {
            if (resp.data.errorCode == 0) {
                store.setNickname(values.nickname as string)
                message.success("个人资料修改成功！")
            }
        })
    };

    useEffect(() => {
        if (result) {
            form.setFieldsValue(result.data)
        }
    }, [getLoading])


    return (
        <div>
            <Form
                form={form}
                labelCol={{span: 6}}
                wrapperCol={{span: 14}}
                style={{maxWidth: 600}}
                onFinish={onFinish}
                autoComplete="off"
                preserve={false}
            >
                <Form.Item
                    label="登录名"
                    name="loginName"
                >
                    <Input readOnly disabled/>
                </Form.Item>

                <Form.Item
                    label="昵称"
                    name="nickname"
                    rules={[{required: true, message: '请输入昵称'}]}
                >
                    <Input/>
                </Form.Item>

                <Form.Item
                    label="手机号"
                    name="mobile"
                    rules={[{required: true, message: '请输入手机号'}]}
                >
                    <Input/>
                </Form.Item>

                <Form.Item
                    label="邮件地址"
                    name="email"
                    rules={[{required: true, message: '请输入邮件地址'}]}
                >
                    <Input/>
                </Form.Item>

                <Form.Item wrapperCol={{offset: 6, span: 16}}>
                    <Button type="primary" htmlType="submit" loading={loading}>
                        保存
                    </Button>
                </Form.Item>

            </Form>
        </div>
    )
}


const Avatar: React.FC = () => {

    const {loading: getLoading, result} = useGet("api/v1/sysAccount/myProfile");
    const {loading, doPost} = usePostManual("/api/v1/sysAccount/updateProfile");
    const {message} = App.useApp();
    const store = useAppStore();
    const [form] = useForm()
    const onFinish: FormProps['onFinish'] = (values) => {
        doPost({
            data: values
        }).then(resp => {
            if (resp.data.errorCode == 0) {
                store.setAvatar(values.avatar as string)
                message.success("个人头像修改成功！")
            }
        })
    };

    useEffect(() => {
        if (result) {
            form.setFieldsValue(result.data)
        }
    }, [getLoading])

    return (
        <div>

            <Form
                form={form}
                labelCol={{span: 6}}
                wrapperCol={{span: 14}}
                style={{maxWidth: 600}}
                onFinish={onFinish}
                autoComplete="off"
                preserve={false}
            >
                <Form.Item
                    name="avatar"
                    style={{padding: "0 0 0 134px"}}
                >
                    <ImageUploader/>
                </Form.Item>

                <Form.Item wrapperCol={{offset: 6, span: 16}}>
                    <Button type="primary" htmlType="submit" loading={loading}>
                        保存头像
                    </Button>
                </Form.Item>

            </Form>
        </div>
    )
}


type SettingsProps = {
    open: boolean,
    onClose: () => void
}
const Settings: React.FC<SettingsProps> = ({open, onClose}) => {
    return (
        <Modal title="" open={open} onCancel={onClose} footer={[]} width={"760px"} style={{top: "140px"}}
               destroyOnClose>
            <div className="settings-main">
                <div className="settings-title">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                        <path
                            d="M7 21.5C4.51472 21.5 2.5 19.4853 2.5 17C2.5 14.5147 4.51472 12.5 7 12.5C9.48528 12.5 11.5 14.5147 11.5 17C11.5 19.4853 9.48528 21.5 7 21.5ZM17 11.5C14.5147 11.5 12.5 9.48528 12.5 7C12.5 4.51472 14.5147 2.5 17 2.5C19.4853 2.5 21.5 4.51472 21.5 7C21.5 9.48528 19.4853 11.5 17 11.5ZM3 8C3 5.23858 5.23858 3 8 3H11V5H8C6.34315 5 5 6.34315 5 8V11H3V8ZM19 13V16C19 17.6569 17.6569 19 16 19H13V21H16C18.7614 21 21 18.7614 21 16V13H19Z"
                            fill="currentColor"></path>
                    </svg>
                    <span><strong>个人设置</strong></span>
                </div>
                <Tabs
                    style={{background: "#fff", borderRadius: "5px", padding: "5px"}}
                    tabPosition="left"
                    items={[
                        {
                            key: 'Profiles',
                            label: '个人资料',
                            children: <Profiles/>,
                        },
                        {
                            key: 'Avatar',
                            label: '头像设置',
                            children: <Avatar/>,
                        },
                    ]}
                />
            </div>
        </Modal>
    )
};

export default Settings;
