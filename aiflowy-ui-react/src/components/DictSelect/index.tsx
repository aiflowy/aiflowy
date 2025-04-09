import {Select, SelectProps} from "antd";
import {useGetManual} from "../../hooks/useApis.ts";
import React, {useEffect, useState} from "react";


export type DictSelectProps = {
    code: string
    initvalue?: number
} & SelectProps

export const DictSelect: React.FC<DictSelectProps> = (props) => {

    const {code} = props
    const [options, setOptions] = useState([])
    const {doGet: getDictItems} = useGetManual(`/api/v1/dict/items/${code}`)

    useEffect(() => {
        getDictItems().then(res => {
            setOptions(res.data.data)
        })
    }, []);

    return (
        <>
            <Select style={{width: 200}} allowClear showSearch
                    {...props}
                    value={props.initvalue}
                    options={options}
            />
        </>
    )
}