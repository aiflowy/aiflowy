import {ColumnConfig} from ".";
import dayjs from 'dayjs';

const dateComponents = ["datepicker", "timepicker", "datetimepicker", "rangepicker"];
export const convertRowDataToDayjs = (rowData: any, columns: (ColumnConfig)[]) => {
    if (!rowData || !columns) return;

    const newData = {
        ...rowData,
    }


    for (let column of columns) {
        if (dateComponents.includes(column.form?.type?.toLowerCase() || "")) {
            const columnValue = newData[(column as any).key || (column as any).dataIndex];
            if (typeof columnValue !== "undefined") {
                newData[column.key || (column as any).dataIndex] = dayjs(columnValue);
            }
        }
    }
    return newData;
}

export const convertDayjsToString = (values: any) => {
    if (!values) return values;

    const newValues = {
        ...values
    }

    for (let key in values) {
        const value = values[key];
        if (value && value.isValid) {
            newValues[key] = value.format("YYYY-MM-DD HH:mm:ss");
        }
        if (Array.isArray(value)) {
            const newArray = [];
            for (let v of value) {
                if (v?.isValid) {
                    newArray.push(v.format("YYYY-MM-DD HH:mm:ss"))
                } else {
                    newArray.push(v)
                }
            }
            newValues[key] = newArray;
        }
    }

    return newValues;
}