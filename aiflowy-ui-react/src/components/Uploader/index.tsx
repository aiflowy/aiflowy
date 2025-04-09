import {Upload, UploadProps} from "antd";
import {isBrowser} from "../../libs/ssr.ts";

const baseUrl = `${import.meta.env.VITE_APP_SERVER_ENDPOINT}/`;
const authKey = `${import.meta.env.VITE_APP_AUTH_KEY || "authKey"}`;
const tokenKey = `${import.meta.env.VITE_APP_TOKEN_KEY}`;

export type UploaderProps = {
    children?: React.ReactNode;
} & UploadProps

export const Uploader: React.FC<UploaderProps> = (props) => {

    let action = props.action;
    if (typeof action === "string" && action.startsWith("/")) {
        action = baseUrl + action.substring(1);
    } else {
        action = `${baseUrl}api/v1/commons/upload`;
    }

    const token = isBrowser ? localStorage.getItem(authKey) : null;

    const headers = {
        Authorization: token || "",
        [tokenKey]: token || "",
        ...props.headers
    };

    const uploadProps: UploadProps = {
        ...props,
        action,
        headers,
    };

    return (
        <Upload {...uploadProps}  >
            {props.children}
        </Upload>
    )
}