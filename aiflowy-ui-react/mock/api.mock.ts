import {defineMock} from 'vite-plugin-mock-dev-server'

/**
 * 更多配置参考如下链接
 * @see https://github.com/pengzhanbo/vite-plugin-mock-dev-server/blob/main/README.zh-CN.md
 */
export default defineMock([
    {
        url: '/api/v1/account/login',
        delay: 2000,
        body: (request) => {
            if (!request.body || request.body.username !== "admin") {
                return {
                    errorCode:1,
                    message: "没有该账户"
                }
            }
            if (request.body.password === "123456") {
                return {
                    errorCode:0,
                    message: "登录成功",
                    // jwt:'',
                    jwt:'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6Ind3dy5ranNvbi5jb20iLCJzdWIiOiJkZW1vIiwiaWF0IjoxNzA2MzUwNzY5LCJuYmYiOjE3MDYzNTA3NjksImV4cCI6MTcwNjQzNzE2OX0.MkCyJoB1f4NJLHCQLJf0azMt5tPeLLImvM6Y2XBBkjM',
                }
            } else {
                return {
                    errorCode:2,
                    message: "密码错误"
                }
            }
        }
    },

])