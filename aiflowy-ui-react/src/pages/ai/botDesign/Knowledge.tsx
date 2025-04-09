import React from "react";
import {BotModal, BotModalProps} from "./BotModal.tsx";

export type KnowledgeModalProps = Omit<BotModalProps, 'tableAlias' | 'tableTitle'>;
export const KnowledgeModal: React.FC<KnowledgeModalProps> = (props) => {
    return (
        <BotModal tableAlias="aiKnowledge" tableTitle="知识库" {...props} />
    );
}