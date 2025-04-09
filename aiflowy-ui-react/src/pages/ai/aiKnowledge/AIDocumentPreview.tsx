import React, { useState } from 'react';
import {usePost} from "../../../hooks/useApis";

const AIDocumentPreview: React.FC = () => {
    const [fileContent, setFileContent] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [para] = useState();
    const {doPost : doPostDocPreview} = usePost('/api/v1/aiDocument/docPreview', para)

    const fetchFilePreview = async () => {
        setLoading(true);
        setError(null);
        try {
            const response = await doPostDocPreview({data: {documentId: '262835137610547200'}});
            const contentType = response.data.data.headers['Content-Type'];
            const data = response.data.data.body;
            if (contentType.includes('text/plain')) {
                // 文本文件
                setFileContent(data);
            } else if (contentType.includes('image')) {
                // 图片文件
                setFileContent(`data:${contentType};base64,${data}`);
            } else if (contentType.includes('application/pdf')) {
                // PDF 文件
                setFileContent(`data:application/pdf;base64,${data}`);
            } else {
                setError('不支持的文件类型');
            }
        } catch (err: any) {
            setError(err.response?.data || '无法加载文件内容');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <button onClick={() => fetchFilePreview()}>预览文件</button>

            {loading && <p>加载中...</p>}
            {error && <p style={{ color: 'red' }}>错误：{error}</p>}
            {fileContent && (
                <div>
                    {typeof fileContent === 'string' && fileContent.startsWith('data:image') ? (
                        <img src={fileContent} alt="预览" style={{ maxWidth: '100%' }} />
                    ) : typeof fileContent === 'string' && fileContent.startsWith('data:application/pdf') ? (
                        <iframe src={fileContent} width="100%" height="600px" title="PDF 预览" />
                    ) : (
                        <pre>{fileContent}</pre>
                    )}
                </div>
            )}
        </div>
    );
};

export default {
    path: "/AIDocumentPreview",
    element: AIDocumentPreview
};