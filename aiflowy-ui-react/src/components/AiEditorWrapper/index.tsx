import React, {useCallback, useEffect, useRef} from 'react'
import {AiEditor} from "aieditor";
import "aieditor/dist/style.css"

const baseUrl = `${import.meta.env.VITE_APP_SERVER_ENDPOINT}`;
const authKey = `${import.meta.env.VITE_APP_AUTH_KEY || "authKey"}`;

type Props = {
    value?: string;
    onChange?: (value: string) => void;
    style?: React.CSSProperties;
    onSave?: (editor: AiEditor) => boolean;
}
const AiEditorWrapper: React.FC<Props> = ({value, onChange, style, onSave}) => {
    const divRef = useRef<HTMLDivElement>(null);
    const aiEditorRef = useRef<AiEditor | null>(null);

    const getHeaders = useCallback(() => {
        return {
            Authorization: localStorage.getItem(authKey)
        }
    }, [])

    useEffect(() => {
        if (!divRef.current) return;
        if (aiEditorRef.current) return;

        aiEditorRef.current = new AiEditor({
            element: divRef.current,
            placeholder: "点击输入内容...",
            content: value || "",
            onSave,
            image: {
                allowBase64: true,
                uploadUrl: `${baseUrl}/api/v1/aieditor/upload/image`,
                uploadHeaders: getHeaders,
            },
            video: {
                uploadUrl: `${baseUrl}/api/v1/aieditor/upload/video`,
                uploadHeaders: getHeaders
            },
            attachment: {
                uploadUrl: `${baseUrl}/api/v1/aieditor/upload/attachment`,
                uploadHeaders: getHeaders
            },
            ai: {
                models: {
                    custom: {
                        url: `${baseUrl}/api/v1/ai/chat`,
                        headers: getHeaders,
                        messageWrapper: (message: string) => {
                            return JSON.stringify({prompt: message})
                        },
                        messageParser: (message: string) => {
                            return {
                                role: "assistant",
                                content: message,
                            }
                        },
                    } as any
                }
            },
        });

        return () => {
            if (aiEditorRef.current) {
                aiEditorRef.current.destroy();
                aiEditorRef.current = null;
            }
        }
    }, [])


    useEffect(() => {
        if (aiEditorRef.current && value !== aiEditorRef.current.getHtml()) {
            aiEditorRef.current.setContent(value || "");
        }
    }, [value]);


    useEffect(() => {
        if (aiEditorRef.current) {
            aiEditorRef.current.options.onSave = onSave;
        }
    }, [onSave]);


    useEffect(() => {
        if (aiEditorRef.current) {
            aiEditorRef.current.options.onChange = (editor) => {
                const html = editor.getHtml();
                onChange?.(html === "<p></p>" ? "" : html);
            };
        }
    }, [onChange]);

    return (
        <div ref={divRef} style={{height: "600px", ...style}}/>
    );
};

export default AiEditorWrapper;
