import React from 'react';
import {App, Form, FormProps, Input, Modal} from "antd";
import {useForm} from "antd/es/form/Form";
import {usePostManual} from "../../hooks/useApis.ts";

type ResetPasswordProps = {
    open: boolean,
    onClose: () => void
}
const UpdatePassword: React.FC<ResetPasswordProps> = ({open, onClose}) => {

    const {loading, doPost} = usePostManual("/api/v1/sysAccountv3/updatePassword");
    const {message} = App.useApp();

    const [form] = useForm()
    const onFinish: FormProps['onFinish'] = (values) => {
        doPost({
            data: values
        }).then(resp => {
            if (resp.data.errorCode == 0) {
                message.success("密码修改成功！")
                onClose();
            }
        })
    };

    const handleOk = () => {
        form.submit();
    };


    return (
        <Modal title="修改密码" open={open} onOk={handleOk}
               onCancel={onClose}
               destroyOnClose={true}
               confirmLoading={loading}>
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
                    label="旧密码"
                    name="password"
                    rules={[{required: true, message: '请输入密码'}]}
                >
                    <Input.Password/>
                </Form.Item>

                <Form.Item
                    label="新密码"
                    name="newPassword"
                    rules={[{required: true, message: '请输入密码'}]}
                >
                    <Input.Password/>
                </Form.Item>

                <Form.Item
                    label="确认密码"
                    name="confirmPassword"
                    rules={[{required: true, message: '请输入确认密码'}]}
                >
                    <Input.Password/>
                </Form.Item>
            </Form>
        </Modal>
    )
};

export default UpdatePassword;
