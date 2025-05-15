import React from "react";
import {App, Button, Checkbox, Flex, Form, Input} from "antd";
import styles from "./login.module.less";
import loginBg from "../../assets/login-bg.png"
import loginImage from "../../assets/login-image.jpg"
import {LockOutlined, UserOutlined} from "@ant-design/icons";
import {useAppStore} from "../../store/appStore.ts";
import {useLocation, useNavigate} from "react-router-dom";
import {useLoginV3} from "../../hooks/useLogin.ts";
import {useCaptcha} from "../../hooks/useCaptcha.ts";

const Login: React.FC = () => {
    const [form] = Form.useForm();
    const {message} = App.useApp();
    const navigate = useNavigate();
    const location = useLocation()

    const {startCaptcha} = useCaptcha();
    //登录
    // const {loading, doLogin} = useLogin();
    const appStore = useAppStore();

    const {loading, doLogin} = useLoginV3();

    const onFinish = (values: any) => {
        startCaptcha((randstr, ticket) => {
            doLogin({
                data: {
                    randstr,
                    ticket,
                    ...values,
                }
            }).then((resp) => {
                if (resp.data.errorCode == 0) {
                    const from = new URLSearchParams(location.search).get('from');
                    if (from) {
                        navigate(from)
                    } else {
                        navigate("/ai/bots")
                        message.success("登录成功，欢迎回来")
                        navigate("/ai/bots")
                    }

                    // appStore.setLogin(resp.data.jwt, resp.data.nickName)
                    appStore.setToken(resp.data.data.token)
                    appStore.setNickname(resp.data.data.nickname)
                    appStore.setAvatar(resp.data.data.avatar)

                } else if (resp.data.message) {
                    message.error(resp.data.message)
                }
            }).catch((e) => {
                message.error("网络错误：" + e)
            })
        })
    }


    return (
        <div className={styles.container} style={{backgroundImage: `url(${loginBg})`,position:"relative"}}>
            <Flex vertical={false} style={{margin: "200px"}}>
                <div>
                    <img alt="" src={loginImage} style={{height: "600px"}}/>
                </div>
                <Form
                    form={form}
                    name="normal_login"
                    className="login-form"
                    initialValues={{remember: true}}
                    onFinish={onFinish}
                    style={{
                        width: "500px",
                        height: "600px",
                        background: "#fff",
                        padding: 50,
                    }}
                >

                    <h1 style={{margin: '50px 0',}}>登录</h1>

                    <Form.Item
                        name="account"
                        rules={[{required: true, message: '账户不能为空'}]}
                        // extra="测试账号 admin，密码 123456"
                    >
                        <Input size={"large"} prefix={<UserOutlined className="site-form-item-icon"/>}
                               placeholder="账户"/>
                    </Form.Item>

                    <Form.Item
                        name="password"
                        rules={[{required: true, message: '密码不能为空'}]}
                    >
                        <Input
                            size={"large"}
                            prefix={<LockOutlined className="site-form-item-icon"/>}
                            type="password"
                            placeholder="密码"
                        />
                    </Form.Item>

                    <Form.Item>
                        <Button size={"large"} type="primary" htmlType="submit" className="login-form-button" block
                                loading={loading}>
                            登 录
                        </Button>
                    </Form.Item>

                    <Form.Item>
                        <Form.Item name="remember" valuePropName="checked" noStyle>
                            <Checkbox>我已阅读《用户服务协议》以及《安全隐私协议》</Checkbox>
                        </Form.Item>

                    </Form.Item>
                </Form>
            </Flex>

            <div style={{position:"absolute",bottom:"20px",right:"20px",color:"#fff",fontSize:"14px"}}><a href="https://aiflowy.tech" style={{color:"#fff"}} target="_blank">AIFlowy.tech</a> (v1.0.2)</div>
        </div>
    )
}
export default Login;