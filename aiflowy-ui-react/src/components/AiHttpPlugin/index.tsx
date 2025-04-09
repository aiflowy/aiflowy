import React, {useEffect, useState} from 'react';
import ApiConfig from "./ApiConfig.tsx";
import HeadersConfig, {Header} from "./HeadersConfig.tsx";
import ParamsConfig, {Param} from "./ParamsConfig.tsx";
import BodyConfig, {BodyType, FormDataItem, UrlEncodedItem} from "./BodyConfig.tsx";

export type AiHttpPluginProps = {
    method: string;
    url: string;
    headers: Header[];
    params: Param[];
    body: {
        type: BodyType;
        content: null| FormDataItem[] | UrlEncodedItem[] | string;
    };
};

export type PluginProps = {
    attrs: AiHttpPluginProps;
    setAttrs: (attrs: AiHttpPluginProps) => void;
}
const AiHttpPlugin: React.FC<PluginProps> = (props) => {

    const {attrs,setAttrs} = props;
    const [httpPluginConfig, setHttpPluginConfig] = useState<any>(attrs)

    useEffect(() => {
        setHttpPluginConfig(attrs)
    },[attrs])

    const setMethod = (method: string) => {
        setHttpPluginConfig({
            ...httpPluginConfig,
            method: method,
        })
        setAttrs({
            ...httpPluginConfig,
            method: method,
        })
    };
    const setUrl = (url: string) => {
        setHttpPluginConfig({
            ...httpPluginConfig,
            url: url,
        })
        setAttrs({
            ...httpPluginConfig,
            url: url,
        })
    };

    const setHeaders = (headers: Header[]) => {
        setHttpPluginConfig({
            ...httpPluginConfig,
            headers: headers,
        })
        setAttrs({
            ...httpPluginConfig,
            headers: headers,
        })
    };

    const setParams = (params: Param[]) => {
        setHttpPluginConfig({
            ...httpPluginConfig,
            params: params,
        })
        setAttrs({
            ...httpPluginConfig,
            params: params,
        })
    };

    const setBody = (type: BodyType, content: null| FormDataItem[] | UrlEncodedItem[] | string) => {
        setHttpPluginConfig({
            ...httpPluginConfig,
            body: {
                type: type,
                content: content
            }
        })
        setAttrs({
            ...httpPluginConfig,
            body: {
                type: type,
                content: content
            }
        })
    };

    return (
        <>
            <ApiConfig method={httpPluginConfig.method} url={httpPluginConfig.url} setMethod={setMethod} setUrl={setUrl}/>
            <HeadersConfig initialHeaders={httpPluginConfig.headers} setHeaderArray={setHeaders} />
            <ParamsConfig initialParams={httpPluginConfig.params} setParamArray={setParams} />
            <BodyConfig type={httpPluginConfig.body.type} content={httpPluginConfig.body.content} setBodyConfig={setBody} />
        </>
    );
};

export default AiHttpPlugin;