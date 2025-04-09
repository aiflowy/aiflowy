import {ColumnConfig} from "./index.tsx";
import {Tag} from "antd";
import {useDict} from "../../hooks/useDict.ts";
import React from "react";

export type DictItemsProps<T> = {
    columnConfig: ColumnConfig<T>,
    columnData: any,
}

const removePrefix = (name: string) => {
    if (!name) {
        return "";
    }
    if (name.indexOf("|") === 0) {
        name = name.substring(1, name.length);
        while (name.indexOf("-") == 0) {
            name = name.substring(1, name.length);
        }
    }
    return name;
}


const DictItems: React.FC<DictItemsProps<any>> = ({columnConfig, columnData}) => {

    const {loading, data} = useDict({
        dictInfo: columnConfig.dict,
        asTreeIfNecessary: true,
        paramGetter: (key) => columnData[key],
        manual: false,
        useCache: true
    })

    if (loading) {
        return (
            <>loading....</>
        )
    }


    if (data?.data) {

        //字典内容可能是 1 个值，在一对多、多对多情况下可能是一个数组
        const dictValue = columnData[(columnConfig as any).key];

        // debugger
        if (Array.isArray(dictValue)) {
            return data.data.map((dictItem: any) => {
                if (dictValue.indexOf(dictItem.key) >= 0) {
                    return <Tag color={"processing"} key={dictItem.key}>{removePrefix(dictItem.label)}</Tag>
                }
            })
        } else {
            return data.data.map((dictItem: any) => {
                if (dictItem.key === dictValue) {
                    return <Tag color={"processing"} key={dictItem.key}>{removePrefix(dictItem.label)}</Tag>
                }
            })
        }
    }

    return "";
};

export default DictItems