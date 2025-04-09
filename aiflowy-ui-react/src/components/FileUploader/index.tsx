import React, {useEffect, useState} from 'react';
import {Button, Upload, UploadFile, UploadProps} from "antd";
import {isBrowser} from "../../libs/ssr.ts";
import {UploadOutlined} from "@ant-design/icons";

const baseUrl = `${import.meta.env.VITE_APP_SERVER_ENDPOINT}/`;
const authKey = `${import.meta.env.VITE_APP_AUTH_KEY || "authKey"}`;


interface FileUploaderProps {
    value?: string;
    onChange?: (value: any) => void;
}

const FileUploader: React.FC<FileUploaderProps> = ({value, onChange}) => {

    const actionUrl = `${baseUrl}api/v1/commons/upload`;
    const token = isBrowser ? localStorage.getItem(authKey) : null;

    const headers = {
        Authorization: token || "",
    };

    const [fileList, setFileList] = useState<UploadFile[]>([]);

    useEffect(() => {
        if (value) {
            setFileList([
                {
                    uid: value,
                    name: value,
                    status: 'done',
                    url: value,
                } as UploadFile
            ])
        } else {
            setFileList([]);
        }
    }, [value])


    const handleChange: UploadProps['onChange'] = ({file, fileList: newFileList}) => {
        setFileList(newFileList);
        if (onChange && file.status === "done" && file.response?.path) {
            onChange(file.response.path as string);
        } else if (onChange && file.status === "removed") {
            onChange("");
        }
    }

    return (
        <Upload
            action={actionUrl}
            headers={headers}
            fileList={fileList}
            onChange={handleChange}
        >
            {<Button icon={<UploadOutlined/>}> 请选择文件 </Button>}
        </Upload>
    );
};

export default FileUploader;
