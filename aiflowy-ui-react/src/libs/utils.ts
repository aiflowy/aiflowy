import {ColumnConfig} from "../components/AntdCrud";

/**
 * 负责把 object 对象合并到根节点的对象
 * 例如：
 * <pre>
 * {
 *     key1:"aaa",
 *     key2.xxx:"yyy"
 * }
 * </pre>
 * 转化为
 * <pre>
 * {
 *     key1:"aaa",
 *     key2:{
 *         xxx:"yyy"
 *     }
 * }
 * </pre>
 *
 *  这样做的目的是为了方便后台 fastjson 程序自动转换为 java Object
 *
 * @param obj
 */
export const convertAttrsToObject = (obj: any) => {
    if (!obj) return obj;

    if (Array.isArray(obj)) {
        for (let i = 0; i < obj.length; i++) {
            obj[i] = convertAttrsToObject(obj[i]);
        }
    } else {
        for (let key of Object.keys(obj)) {
            const attrs = key.split(".");
            if (attrs.length <= 1) {
                continue;
            }
            const value = obj[key];
            let attrObject = obj;
            let index = 0;
            for (let attr of attrs) {
                //the last
                if (++index == attrs.length) {
                    attrObject[attr] = value;
                } else {
                    let newObject = attrObject[attr];
                    if (!newObject) {
                        newObject = {};
                        attrObject[attr] = newObject;
                    }
                    attrObject = newObject;
                }
            }
        }
    }
    return obj;
}


/**
 * 负责把 object 对象合并到根节点的对象
 * 例如：
 * <pre>
 * {
 *     key1:"aaa",
 *     key2:{
 *         xxx:"yyy"
 *     }
 * }
 * </pre>
 * 转化为
 * <pre>
 * {
 *     key1:"aaa",
 *     key2.xxx:"yyy"
 * }
 * </pre>

 *
 * 这样做的目的是为了 antd 的 form 表单自动根据 key 【"option.xxx"】自动读取和展现
 *
 * @param obj
 */
export const convertObjetToAttrs = (obj: any) => {
    if (!obj) return obj;

    if (Array.isArray(obj)) {
        for (let i = 0; i < obj.length; i++) {
            obj[i] = convertObjetToAttrs(obj[i]);
        }
    } else {
        for (let key of Object.keys(obj)) {
            const value = obj[key];
            if (typeof value === "object") {
                for (let childKey of Object.keys(value)) {
                    obj[key + "." + childKey] = value[childKey];
                }
            }
        }
    }
    return obj;
}


const pushKeys: any = (obj: any, keys: any[]) => {
    if (!obj) return;
    // debugger
    if (Array.isArray(obj)) {
        for (let i = 0; i < obj.length; i++) {
            pushKeys(obj[i], keys);
        }
    } else {
        const children = obj["children"];
        if (children && children.length > 0) {
            keys.push(obj["id"]);
            pushKeys(children, keys);
        }
    }
}


export const getObjectKeyIfHasChildren: any = (obj: any) => {
    const keys: any[] = [];
    pushKeys(obj, keys);
    return keys;
}


export const removeIf = (array: any[], predicate: (value: any, index: number, array: any[]) => boolean) => {
    if (!array || array.length === 0) {
        return array;
    }
    let i = 0;
    while (i < array.length) {
        if (predicate(array[i], i, array)) {
            array.splice(i, 1);
        } else {
            ++i;
        }
    }
    return array;

}


const disabledDictItemAndChildren0 = (obj: any) => {
    if (!obj) return;

    if (Array.isArray(obj)) {
        for (let i = 0; i < obj.length; i++) {
            disabledDictItemAndChildren0(obj[i]);
        }
    } else {
        obj["disabled"] = true;
        disabledDictItemAndChildren0(obj["children"])
    }
    return obj;
}

export const disabledDictItemAndChildren = (array: any[], key: any) => {
    if (!array || array.length == 0 || !key) {
        return
    }

    let disableLayerNo: number | null = null;
    array.forEach((item) => {
        if (item.key === key) {
            disableLayerNo = item.layerNo;
            disabledDictItemAndChildren0(item);
        } else if (disableLayerNo != null) {
            if (typeof item.layerNo !== "undefined" && item.layerNo > disableLayerNo) {
                disabledDictItemAndChildren0(item);
            } else {
                disableLayerNo = null;
            }
        }
    })
}


export const isHidden = (columnConfig: ColumnConfig) => {
    if (!columnConfig.form?.type) {
        return false;
    }
    return "hidden" === columnConfig.form.type.toLowerCase();
}

export const getDefault = <T>(value: T, defaultValue: T): T => {
    if (typeof defaultValue === "number" || (typeof defaultValue === "boolean")) {
        if (value === null || typeof value === "undefined") {
            return defaultValue;
        } else {
            return value;
        }
    }

    return value || defaultValue;
}

export const removeUndefinedOrNull = (obj: any) => {
    if (!obj) {
        return obj;
    }
    if (Array.isArray(obj)) {
        for (let i = 0; i < obj.length; i++) {
            removeUndefinedOrNull(obj[i]);
        }
    }
    Object.keys(obj).forEach(key => (obj[key] === undefined || obj[key] === null) && delete obj[key])
    return obj;
}

export const dateFormat = (dateOrTime: Date | null | string | number = null, format: string) => {
    let date: Date = dateOrTime ? new Date(dateOrTime) : new Date();
    let pattern: RegExp = /(Y{2,4}|M{1,2}|D{1,2}|H{1,2}|h{1,2}|m{1,2}|s{1,2}|C{1,2}|W{1,2})/g;
    let tmp: any = (S: string | number) => (Number(S) < 10 ? "0" + S : S);
    let res: string = format.replace(pattern, ($0: string) => {
        switch ($0) {
            case "YY":
                return date.getFullYear().toString().slice(-2);
            case "YYYY":
                return date.getFullYear();
            case "M":
                return date.getMonth() + 1;
            case "MM":
                return tmp(date.getMonth() + 1);
            case "D":
                return date.getDate();
            case "DD":
                return tmp(date.getDate());
            case "W":
                return date.getDay();
            case "WW":
                return ["日", "一", "二", "三", "四", "五", "六"][date.getDay()];
            case "H":
                return date.getHours();
            case "HH":
                return tmp(date.getHours());
            case "m":
                return date.getMinutes();
            case "mm":
                return tmp(date.getMinutes());
            case "s":
                return date.getSeconds();
            case "ss":
                return tmp(date.getSeconds());
            case "C":
                return Math.trunc(date.getTime() / 1000);
            case "CC":
                return date.getTime();
            default:
                return $0;
        }
    });
    return res;
};
