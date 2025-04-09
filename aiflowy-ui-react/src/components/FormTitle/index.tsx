import React from 'react';
import Title from "antd/es/typography/Title";
import {Divider} from "antd";

type Props = {
    children?: React.ReactNode
    style?: React.CSSProperties
    first?: boolean
}
const FormTitle: React.FC<Props> = ({children, style, first = false}) => {
    style = first ? {...style, marginTop: "0"} : {...style, marginTop: "60px"};
    return (
        <div style={style}>
            <Title level={5} style={{paddingBottom: "0",}}>{children}</Title>
            <Divider orientation="left" orientationMargin="0" style={{marginBottom: "50px", marginTop: "0"}}></Divider>
        </div>
    );
};

export default FormTitle;
