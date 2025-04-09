import React from "react";
import {BotModal, BotModalProps} from "./BotModal.tsx";

export type DatabasesModalProps = Omit<BotModalProps, 'tableAlias' | 'tableTitle'>;
export const DatabasesModal: React.FC<DatabasesModalProps> = (props) => {
    return (
        <BotModal tableAlias="aiDatabase" tableTitle="数据库" {...props} />
    );
}