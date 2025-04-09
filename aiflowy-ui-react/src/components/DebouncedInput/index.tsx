import {useDebouncedCallback} from "use-debounce";
import * as React from "react";
import {Input, InputRef} from "antd";
import {InputProps} from "antd/es/input/Input";

export type DebouncedInputProps =
    { debounced?: number } &
    InputProps & React.RefAttributes<InputRef>
export const DebouncedInput: React.FC<DebouncedInputProps> = (props) => {

    const debounced = useDebouncedCallback(
        (event) => {
            props.onChange?.(event)
        },
        props.debounced || 300
    );
    return <Input {...props} onChange={event => debounced(event)}/>
}