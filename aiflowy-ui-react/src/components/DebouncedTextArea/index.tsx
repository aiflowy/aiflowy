import {useDebouncedCallback} from "use-debounce";
import TextArea, {TextAreaProps, TextAreaRef} from "antd/es/input/TextArea";
import * as React from "react";
import {ChangeEvent} from "react";
import {omit} from "radash";

export type DebouncedTextAreaProps = {
    debounced?: number
    onChangeImmediately?: (event: ChangeEvent<HTMLTextAreaElement>) => void,
    minRows?: number,
    maxRows?: number
} &
    TextAreaProps & React.RefAttributes<TextAreaRef>
export const DebouncedTextArea: React.FC<DebouncedTextAreaProps> = (props) => {

    const debounced = useDebouncedCallback(
        (event) => {
            props.onChange?.(event)
        },
        props.debounced || 300
    );

    const textAreaProps = omit(props, ['debounced', 'onChangeImmediately', "minRows", "maxRows"])
    return <TextArea autoSize={{minRows: props.minRows || (props.rows || 1), maxRows: props.maxRows || 5}}
                     {...textAreaProps}
                     onChange={event => {
                         props.onChangeImmediately?.(event)
                         debounced(event)
                     }}/>
}