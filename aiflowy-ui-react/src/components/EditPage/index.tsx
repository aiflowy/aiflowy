import React, {JSX} from 'react';
import {useSave, useUpdate} from "../../hooks/useApis.ts";
import {Actions, ColumnConfig, ColumnGroup, ColumnsConfig} from "../AntdCrud";
import {convertAttrsToObject} from "../../libs/utils.ts";
import EditForm, {EditLayout} from "../AntdCrud/EditForm.tsx";

interface CurdPageProps {
    tableAlias: string,
    modalTitle: string,
    open: boolean,
    columnsConfig: ColumnsConfig<any>,
    groups?: ColumnGroup[],
    params?: any,
    onRefresh?: () => any
    onSubmit: (values: any) => void,
    onCancel: () => void,
    formRenderFactory?: (position: "edit" | "search", columnConfig: ColumnConfig) => JSX.Element | null,
    data?: any
    layout?: EditLayout
}

const EditPage: React.FC<CurdPageProps> = ({
                                               tableAlias,
                                               modalTitle,
                                               open,
                                               columnsConfig,
                                               groups,
                                               params,
                                               onRefresh,
                                               onSubmit,
                                               onCancel,
                                               formRenderFactory,
                                               data,
                                               layout
                                           }) => {


    const {doSave} = useSave(tableAlias, params);
    const {doUpdate} = useUpdate(tableAlias, params);

    const actions: Actions<any> = {

        onCreate: (item) => {
            doSave({
                data: {
                    ...params,
                    ...item
                },
            }).then(onRefresh);
        },

        onUpdate: (item) => {
            doUpdate({
                data: {
                    ...params,
                    ...convertAttrsToObject(item)
                },
            }).then(onRefresh)
        }
    };

    return (
        <EditForm title={modalTitle}
                  columns={columnsConfig}
                  open={open}
                  groups={groups}
                  onSubmit={onSubmit}
                  onCancel={onCancel}
                  actions={actions}
                  formRenderFactory={formRenderFactory}
                  row={data}
                  layout={layout}
        />
    )
};

export default EditPage;
