import React from "react";
import * as Icon from "@ant-design/icons";
import {IconProps} from "@ant-design/icons/es/components/IconBase";


type Props = {
    name: string,
} & Omit<IconProps, "icon">

const AntdIcon: React.FC<Props> = (props): React.ReactNode => {
    if (!props.name) return;
    return React.createElement(Icon && (Icon as any)[props.name], props)
};

export default AntdIcon;
