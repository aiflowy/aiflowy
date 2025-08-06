import React, { useEffect, useState } from "react";
import { App, Button, Checkbox, Flex, Form, Input } from "antd";
import styles from "./login.module.less";
import loginBg from "../../assets/login-bg.png";
import loginImage from "../../assets/login-image.jpg";
import { LockOutlined, UserOutlined } from "@ant-design/icons";
import { useAppStore } from "../../store/appStore.ts";
import { useLocation, useNavigate } from "react-router-dom";
import { useLoginV3 } from "../../hooks/useLogin.ts";
import { useCaptcha } from "../../hooks/useCaptcha.ts";
import Loading from "../../components/Loading/Loading.tsx";

const Login: React.FC = () => {
    const [form] = Form.useForm();
    const { message } = App.useApp();
    const navigate = useNavigate();
    const location = useLocation();

    const { startCaptcha } = useCaptcha();
    const appStore = useAppStore();
    const { loading, doLogin } = useLoginV3();

    const [isPageLoaded, setIsPageLoaded] = useState(false);

    useEffect(() => {
        const img1 = new Image();
        const img2 = new Image();

        img1.src = loginBg;
        img2.src = loginImage;

        let loadedCount = 0;

        const handleLoad = () => {
            loadedCount++;
            if (loadedCount >= 2) {
                setTimeout(() => {
                    setIsPageLoaded(true);
                }, 2000);
            }
        };

        img1.onload = handleLoad;
        img2.onload = handleLoad;
        img1.onerror = handleLoad;
        img2.onerror = handleLoad;

        return () => {
            img1.onload = null;
            img2.onload = null;
        };
    }, []);

    const onFinish = (values: any) => {
        if (!values.agreement) {
            message.error("请先阅读并同意用户服务协议和隐私政策");
            return;
        }
        startCaptcha((randstr, ticket) => {
            doLogin({
                data: {
                    randstr,
                    ticket,
                    ...values,
                },
            })
                .then((resp) => {
                    if (resp.data.errorCode === 0) {
                        const from = new URLSearchParams(location.search).get("from");
                        if (from) {
                            navigate(from);
                        } else {
                            navigate("/ai/bots");
                            message.success("登录成功，欢迎回来");
                        }

                        appStore.setToken(resp.data.data.token);
                        appStore.setNickname(resp.data.data.nickname);
                        appStore.setAvatar(resp.data.data.avatar);
                        // 刷新页面获取权限列表
                        window.location.reload();
                    } else if (resp.data.message) {
                        message.error(resp.data.message);
                    }
                })
                .catch((e) => {
                    message.error("网络错误：" + e);
                });
        });
    };

    return isPageLoaded ? (
        <div
            className={styles.container}
            style={{ backgroundImage: `url(${loginBg})` }}
        >
            <div className={styles.logoLogin}>
                <img src={"/favicon.svg"} alt="Login Image" />
            </div>

            <div className={styles.formContainer} style={{  top: `calc(50% - ${522 / 2}px)`,
                left: "61.04%",}}>
                <Flex vertical={false}>
                    <Form
                        form={form}
                        name="normal_login"
                        initialValues={{ remember: true }}
                        onFinish={onFinish}
                        className={styles.loginForm}
                    >
                        <div className={styles.title}>登录</div>

                        <Form.Item name="account" rules={[{ required: true, message: "账户不能为空" }]}>
                            <Input
                                className={styles.inputField}
                                prefix={<UserOutlined />}
                                placeholder="账户"
                            />
                        </Form.Item>

                        <Form.Item name="password" rules={[{ required: true, message: "密码不能为空" }]}>
                            <Input
                                className={styles.inputField}
                                prefix={<LockOutlined />}
                                type="password"
                                placeholder="密码"
                            />
                        </Form.Item>

                        <Form.Item>
                            <Button
                                type="primary"
                                className={styles.submitButton}
                                htmlType="submit"
                                block
                                loading={loading}
                            >
                                登 录
                            </Button>
                        </Form.Item>

                        <Form.Item className={styles.agreement}>
                            <Form.Item name="agreement" valuePropName="checked" noStyle>
                                <Checkbox style={{ color: "#757575 " }}>
                                    我已阅读
                                    <a href="#">《用户服务协议》</a>
                                    以及
                                    <a href="#">《安全隐私协议》</a>
                                </Checkbox>
                            </Form.Item>
                        </Form.Item>
                        <Form.Item className={styles.agreement} style={{ margin: "0" }}>
                            <div className={styles.footer}>
                                <a href="https://aiflowy.tech" target="_blank" rel="noopener noreferrer">
                                    AIFlowy.tech
                                </a>{" "}
                                (v1.1.4)
                            </div>
                        </Form.Item>
                    </Form>

                </Flex>
            </div>


        </div>
    ) : (
        <Loading />
    );
};

export default Login;