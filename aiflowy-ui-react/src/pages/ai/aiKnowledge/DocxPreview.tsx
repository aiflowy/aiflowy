import React, { useRef, useEffect } from 'react';
import { renderAsync } from 'docx-preview';

// 定义 DocxPreview 组件的 Props
interface DocxPreviewProps {
    textContent?: string; // 设置为可选，并提供默认值
}

const DocxPreview: React.FC<DocxPreviewProps> = ({ textContent = "" }) => {
    const previewRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        console.log("接收到的 textContent:", textContent);

        // 如果 textContent 为空或仅包含空白字符，直接返回
        if (!textContent || !textContent.trim()) {
            console.warn("textContent 为空或仅包含空白字符，无法渲染 DOCX 文件");
            return;
        }

        try {
            // 将 Base64 数据转换为 Blob
            const base64ToBlob = (base64: string): Blob => {
                const byteString = atob(base64.split(",")[1] || base64); // 支持直接的 Base64 数据
                const arrayBuffer = new ArrayBuffer(byteString.length);
                const uint8Array = new Uint8Array(arrayBuffer);

                for (let i = 0; i < byteString.length; i++) {
                    uint8Array[i] = byteString.charCodeAt(i);
                }

                return new Blob([uint8Array], { type: "application/vnd.openxmlformats-officedocument.wordprocessingml.document" });
            };

            // 检查 textContent 是否是 Base64 格式
            const isBase64 = /^(data:)?application\/vnd\.openxmlformats-officedocument\.wordprocessingml\.document;base64,/.test(textContent);

            if (!isBase64) {
                console.error("textContent 不是有效的 Base64 数据，请检查后端返回的数据格式");
                return;
            }

            const docxBlob = base64ToBlob(textContent);

            // 渲染 DOCX 文件
            if (previewRef.current) {
                renderAsync(docxBlob, previewRef.current, undefined, {
                    className: "docx-preview", // 自定义样式类
                    inWrapper: true, // 添加包装容器
                    ignoreWidth: false, // 不忽略宽度
                    ignoreHeight: false, // 不忽略高度
                    breakPages: true, // 分页
                }).catch((error) => {
                    console.error("渲染失败:", error);
                    alert("文档渲染失败，请检查文件内容或网络连接！");
                });
            }
        } catch (error) {
            console.error("处理 DOCX 文件失败:", error);
            alert("处理文档失败，请检查文件内容或网络连接！");
        }
    }, [textContent]);

    // 如果 textContent 为空，显示占位 UI
    if (!textContent || !textContent.trim()) {
        return (
            <div
                style={{
                    width: '100%',
                    height: '800px',
                    border: '1px solid #ccc',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '16px',
                    color: '#666',
                    backgroundColor: '#f9f9f9', // 背景色
                }}
            >
                暂无文档内容可预览
            </div>
        );
    }

    return <div ref={previewRef} style={{ width: '100%', height: '800px', border: '1px solid #ccc' }} />;
};

export default DocxPreview;