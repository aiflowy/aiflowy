import React, {forwardRef, useEffect, useImperativeHandle} from 'react';
import {useList, usePage, useRemove, useRemoveBatch, useSave, useUpdate} from "../../hooks/useApis.ts";
import AntdCrud, {ActionConfig, Actions, ColumnsConfig} from "../AntdCrud";
import {Page} from "../../types/Page.ts";
import {convertAttrsToObject} from "../../libs/utils.ts";

import {useUrlParams} from "../../hooks/useUrlParams.ts";
import {EditLayout} from "../AntdCrud/EditForm.tsx";

interface CurdPageProps {
    ref?: any,
    tableAlias: string,
    showType?: "list" | "page",
    defaultPageSize?: number,
    defaultExpandedAllRow?: boolean,
    columnsConfig: ColumnsConfig<any>,
    actionConfig?: ActionConfig<any>,
    addButtonEnable?: boolean,
    customButton?: () => React.ReactNode | null,
    rowSelectEnable?: boolean,
    params?: any,
    paramsToUrl?: boolean,
    editLayout?: EditLayout,
    onRefresh?: () => void;
    externalRefreshTrigger?: number; // 当这个值变化时触发刷新
}

const CrudPage: React.FC<CurdPageProps> = forwardRef(({
                                               tableAlias,
                                               showType = "page",
                                               defaultPageSize = 10,
                                               defaultExpandedAllRow = true,
                                               columnsConfig,
                                               actionConfig,
                                               addButtonEnable = true,
                                               customButton,
                                               rowSelectEnable = true,
                                               params,
                                               paramsToUrl = false,
                                               editLayout,
                                               externalRefreshTrigger
                                           },ref) => {

    useImperativeHandle(ref, () => ({
        refresh: () => {
            actions.onFetchList?.(pageNumber, pageSize, urlParams);
        }
    }));

    const isPage = showType === "page";
    const {
        loading,
        result,
        doGet: doQuery
    } = isPage ? usePage(tableAlias, params, {manual: true}) : useList(tableAlias, params, {manual: true});

    const {doSave} = useSave(tableAlias, params);
    const {doRemove} = useRemove(tableAlias, params);
    const {doRemoveBatch} = useRemoveBatch(tableAlias, params);
    const {doUpdate} = useUpdate(tableAlias, params);

    const [urlParams, setUrlParams] = useUrlParams();

    const actions: Actions<any> = {
        onFetchList: (pageNumber, pageSize, searchParams, sortKey, sortType) => {
            if (!isPage) {
                pageNumber = undefined
                pageSize = undefined
            }
            const newParams = {
                ...searchParams,
                ...params,
                pageNumber,
                pageSize,
                sortKey,
                sortType,
            }
            paramsToUrl && setUrlParams(newParams)
            doQuery({
                params: newParams
            })
        },
        onCreate: (item) => {
            doSave({
                data: {
                    ...params,
                    ...item
                },
            })
        },
        onDelete: (item) => {
            doRemove({
                data: {id: item.id}
            })
        },
        onDeleteBatch: (rows) => {
            doRemoveBatch({
                data: {
                    ids: rows.map((item) => item.id)
                }
            })
        },
        onUpdate: (item) => {
            doUpdate({
                data: {
                    ...params,
                    ...convertAttrsToObject(item)
                },
            })
        }
    };

    // const pageNumber = +(((result?.data) as Page<any>)?.pageNumber || urlParams.pageNumber || 1)
    // const pageSize = +(((result?.data) as Page<any>)?.pageSize || urlParams.pageSize || defaultPageSize)
    const pageNumber = +(urlParams.pageNumber || ((result?.data) as Page<any>)?.pageNumber || 1)
    let pageSize = +(urlParams.pageSize || ((result?.data) as Page<any>)?.pageSize || defaultPageSize)
    if (!isPage) {
        pageSize = 1000;
    }
    //
    // console.log("index", urlParams, {pageNumber, pageSize})

    useEffect(() => {
        actions.onFetchList?.(pageNumber, pageSize, urlParams);
    }, [externalRefreshTrigger]);

    return (
        <AntdCrud columns={columnsConfig}
                  tableAlias={tableAlias}
                  defaultExpandedAllRow={defaultExpandedAllRow}
                  editLayout={editLayout}
                  dataSource={isPage ? ((result?.data) as Page<any>)?.records : result?.data}
                  loading={loading}
                  actions={actions}
                  addButtonEnable={addButtonEnable}
                  customButton={customButton}
                  rowSelectEnable={rowSelectEnable}
                  actionConfig={actionConfig}
                  paginationHidden={!isPage}
                  initSearchParams={urlParams}
                  onSearchValueInit={(key) => urlParams[key]}
                  intelligentFilling={{
                      url: `/api/v1/${tableAlias}/intelligentFilling`
                  }}
                  pageNumber={pageNumber}
                  pageSize={pageSize}
                  totalRow={((result?.data) as Page<any>)?.totalRow}/>
    )
});

export default CrudPage;
