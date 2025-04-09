import React from "react";
import {BotModal, BotModalProps} from "./BotModal.tsx";

export type WorkflowsModalProps = Omit<BotModalProps, 'tableAlias' | 'tableTitle'>;
export const WorkflowsModal: React.FC<WorkflowsModalProps> = (props) => {
    return (
        <BotModal tableAlias="aiWorkflow" tableTitle="工作流" {...props} />
    );
}