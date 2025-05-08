import {Result} from "../types/Result.ts";
import {useAxios} from "./useAxios.ts";
import {Options} from "axios-hooks";
import {useEffect} from "react";
import {App} from "antd";
import {useNavigate} from "react-router-dom";
import {useAppStore} from "../store/appStore.ts";

const arrayParamsSerializer = (params: Record<string, any>) => {
    let options = '';
    for (const [key, value] of Object.entries(params)) {
        if (Array.isArray(value)) {
            for (const element of value) {
                options += `${key}=${element}&`;
            }
        } else {
            options += `${key}=${value}&`;
        }
    }
    return options.slice(0, -1);
};

const authKey = `${import.meta.env.VITE_APP_AUTH_KEY || "authKey"}`;

export const useGetManual = (url: string, manual: boolean = true) => {
    return useGet(url, null, {manual});
}

export const useGet = (url: string, params?: any, options?: Options) => {

    const [{loading, data, error, response}, doGet] = useAxios<Result<any>>({
        url,
        params,
        method: "get",
        paramsSerializer: arrayParamsSerializer,
    }, {manual: false, autoCancel: false, useCache: false, ...options});

    const {message} = App.useApp();
    const navigate = useNavigate();
    const store = useAppStore();

    useEffect(() => {
        if (response) {
            const errorCode = response.data?.errorCode;
            if (errorCode === 99) {
                localStorage.removeItem(authKey)
                if (response?.data?.message) {
                    message.error(response.data.message);
                }
                store.logOut();
                navigate("/login");
            } else if (errorCode !== 0 && response?.data?.message) {
                message.error(response.data.message)
            }
            if (response?.headers?.jwt) {
                useAppStore.getState().setJwt(response.headers.jwt);
            }
        }
    }, [response]);

    useEffect(() => {
        //缓存取消？
        if (error && "ERR_CANCELED" !== error?.code) {
            const status = error.response?.status;
            const msg = error.code + (status ? `(${status})` : "") + ": " + error.message
            message.error(msg)
        }
    }, [error]);

    return {
        loading,
        result: data,
        error,
        doGet
    }
}


export const usePostManual = (url: string, manual: boolean = true) => {
    return usePost(url, null, {manual});
}

export const usePost = (url: string, params?: any, options?: Options) => {
    const [{loading, data, error, response}, doPost] = useAxios<Result<any>>({
        url, method: "post", params,
    }, {manual: true, useCache: false, ...options});

    const {message} = App.useApp();
    const navigate = useNavigate();
    const store = useAppStore();

    useEffect(() => {
        if (response) {
            const errorCode = response.data?.errorCode;
            if (errorCode === 99) {
                localStorage.removeItem(authKey)
                if (response?.data?.message) {
                    message.error(response.data.message);
                }
                store.logOut();
                navigate("/login");
            } else if (errorCode !== 0 && response?.data?.message) {
                message.error(response.data.message)
            }
            if (response?.headers?.jwt) {
                useAppStore.getState().setJwt(response.headers.jwt);
            }
        }
    }, [response]);

    useEffect(() => {
        //缓存取消？
        if (error && "ERR_CANCELED" !== error?.code) {
            const status = error.response?.status;
            const msg = error.code + (status ? `(${status})` : "") + ": " + error.message
            message.error(msg)
        }
    }, [error]);

    return {
        loading,
        result: data,
        error,
        doPost
    }
}


export const useSave = (tableAlias: string, params?: any, options?: Options) => {
    const result = usePost(`/api/v1/${tableAlias}/save`, params, options);
    return {
        ...result,
        doSave: result.doPost
    }
}

export const useRemove = (tableAlias: string, params?: any, options?: Options) => {
    const result = usePost(`/api/v1/${tableAlias}/remove`, params, options);
    return {
        ...result,
        doRemove: result.doPost
    }
}

export const useRemoveBatch = (tableAlias: string, params?: any, options?: Options) => {
    const result = usePost(`/api/v1/${tableAlias}/removeBatch`, params, options);
    return {
        ...result,
        doRemoveBatch: result.doPost
    }
}

export const useUpdate = (tableAlias: string, params?: any, options?: Options) => {
    const result = usePost(`/api/v1/${tableAlias}/update`, params, options);
    return {
        ...result,
        doUpdate: result.doPost
    }
}

export const useList = (tableAlias: string, params?: any, options?: Options) => {
    return useGet(`/api/v1/${tableAlias}/list`, params, options)
}


export const usePage = (tableAlias: string, params?: any, options?: Options) => {
    return useGet(`/api/v1/${tableAlias}/page`, params, options)
}

export const useDetail = (tableAlias: string, id: any, options?: Options) => {
    return useGet(`/api/v1/${tableAlias}/detail`, {
        id
    }, options)
}

export const usePostFile = (url: string, options?: Options) => {
    const [{loading},doPost] = useAxios({
        url: url,
        method: "POST",
        headers: {
            "Content-Type": "multipart/form-data"
        },
        ...options
    });
    return {
        loading,
        doPost
    }
}

export const useUpload = () => {
    const result = usePost("/api/v1/commons/upload");
    return {
        ...result,
        doUpload: result.doPost
    }
}
