import React, {useEffect, useState} from 'react';
import {message, Progress} from 'antd';
import {useSse} from "../../hooks/useSse";

export type ModelInstallPageProps = {
    modelName: string;
    onSendMessage: (isInstalled: boolean) => void;
}

const ModelInstallPage: React.FC<ModelInstallPageProps> = ({ modelName, onSendMessage}) => {
    const {start, stop} = useSse("/api/v1/ollama/installModel", {},'ollamaInstall');
    const [progress, setProgress] = useState<number>(0); // 存储进度百分比
    const [status, setStatus] = useState<'active' | 'exception' | 'success'>('active'); // 进度条状态：active | exception | success
    const [isLoading, setIsLoading] = useState<boolean>(true); // 是否正在安装
    useEffect(() => {
        if (isLoading) {
            setStatus('active'); // 设置进度条状态为进行中
            setIsLoading(true); // 开始加载
            start({
                data: {
                    modelName: modelName
                },
                onMessage: (msg) => {
                    const data = JSON.parse(msg); // 解析 JSON 数据
                    if (data?.error == 'pull model manifest: file does not exist') {
                        setStatus('exception'); // 设置进度条状态为异常
                        setIsLoading(false); // 停止加载状态
                        message.error('模型不存在');
                        stop()
                        onSendMessage(true)
                        return;
                    }
                    // 如果数据中包含 completed 和 total，计算进度百分比
                    if (data.completed !== undefined && data.total !== undefined) {
                        const percent = Math.round((data.completed / data.total) * 100);
                        setProgress(percent); // 更新进度百分比
                    }
                    // 如果状态是 complete，设置进度条为完成状态
                    if (data.status === 'complete'|| data.status === 'success' ) {
                            setProgress(100)
                            setStatus('success'); // 设置进度条状态为完成
                            setIsLoading(false); // 停止加载状态
                            onSendMessage(true)
                    }
                },
                onFinished: () => {
                },
                onError: () => {
                        setStatus('exception'); // 设置进度条状态为异常
                        setIsLoading(false); // 停止加载状态
                }
            })
        }
    }, [isLoading]);


    return (
        <div style={{padding: '20px'}}>
            <p>
                {isLoading ? `${modelName} 正在安装中...` : status == 'success' ? `${modelName} 安装成功！` :
                    status == 'exception'? `${modelName} 安装失败！` : ''}
            </p>
            <div style={{marginTop: '20px'}}>
                <Progress
                    size={'default'}
                    percent={progress} // 进度百分比
                    status={status} // 进度条状态
                />
            </div>
        </div>
    );
};

export default ModelInstallPage;