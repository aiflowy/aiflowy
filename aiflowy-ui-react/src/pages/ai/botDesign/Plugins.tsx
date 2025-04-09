import React from "react";
import {BotModal, BotModalProps} from "./BotModal.tsx";

export type PluginsModalProps = Omit<BotModalProps, 'tableAlias' | 'tableTitle'>;
export const PluginModal: React.FC<PluginsModalProps> = (props) => {
    return (
        <BotModal tableAlias="aiPlugins" tableTitle="插件"{...props}   />
    );
}