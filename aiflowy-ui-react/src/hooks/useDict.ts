import {useGet} from "./useApis.ts";
import {ColumnConfig, DictConfig} from "../components/AntdCrud";

const isUrlPath = (path?: string) => {
    return path?.indexOf("/") === 0;
}

export type UseDictProps = {
    dictInfo: ColumnConfig["dict"], // string | DictConfig,
    asTreeIfNecessary: boolean,
    paramGetter: (key: string) => any,
    manual?: boolean,
    useCache?: boolean,
}

export const useDict = ({dictInfo, asTreeIfNecessary, paramGetter, manual = false, useCache = true}: UseDictProps) => {

    if (!dictInfo) {
        return {
            queryDict: false,
            loading: false,
            data: null,
            doGet: null
        }
    }


    const dictUrlOrName = typeof dictInfo === "string" ? dictInfo as string : (dictInfo as DictConfig).name || (dictInfo as DictConfig).url;
    const dictQueryUrl = isUrlPath(dictUrlOrName) ? dictUrlOrName : ("/api/v1/dict/items/" + dictUrlOrName);
    const params = {} as any

    if (typeof dictInfo !== "string" && dictInfo?.paramKeys) {
        for (let formItemName of dictInfo.paramKeys) {
            params[formItemName] = paramGetter(formItemName);
        }
    }

    if (asTreeIfNecessary && dictInfo !== "string" && !!(dictInfo as DictConfig)?.asTree) {
        params["asTree"] = true
    }

    const {loading, result: data, doGet} = useGet(dictQueryUrl!, params
        , {useCache, autoCancel: true, manual});


    return {
        queryDict: true,
        loading,
        data,
        doGet
    }


}
