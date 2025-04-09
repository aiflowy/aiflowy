import {useSearchParams} from "react-router-dom";
import {useCallback, useEffect, useState} from "react";
import {removeUndefinedOrNull} from "../libs/utils.ts";

export const useUrlParams = () => {

    const [searchParams, setSearchParams] = useSearchParams();

    const initParams = {} as any;
    for (let [key, value] of searchParams.entries()) {
        initParams[key] = value;
    }

    const [myParams, setMyParams] = useState<any>(initParams)


    useEffect(() => {
        const newParams = {} as any;
        for (let [key, value] of searchParams.entries()) {
            newParams[key] = value;
        }
        setMyParams(newParams)
    }, [searchParams])


    const setUrlParams = useCallback((params: any, append?: boolean) => {
        removeUndefinedOrNull(params)
        setSearchParams(append ? {...myParams, ...params} : params);
    }, [searchParams])


    return [myParams, setUrlParams]
}
