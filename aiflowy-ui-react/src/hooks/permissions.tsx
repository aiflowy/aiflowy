// import {useGetAccountPermissionsQuery} from "../apis/accountApi";

/**
 * 权限检测当前用户是否有某个权限
 * @param permissionKey 权限 key
 */
export const useHasPermission = (permissionKey: string | string[]) => {
    // const {data, isLoading} = useGetAccountPermissionsQuery();
    //
    // const hasPermission = (): boolean => {
    //     if (isLoading) {
    //         return false;
    //     }
    //     if (Array.isArray(permissionKey)){
    //         for (let key of permissionKey) {
    //             if (data!.permissions.includes(key)){
    //                 return false;
    //             }
    //         }
    //         return true;
    //     }
    //     return data!.permissions.includes(permissionKey);
    // }
    // return {isLoading, hasPermission};
    console.log(permissionKey)
    return {isLoading:false,hasPermission:()=>true}
}


/**
 * 权限检测当前用户是否有某个权限
 * @param permissionKey 权限 key
 */
export const useHasAnyPermission = (permissionKey: string | string[]) => {
    console.log(permissionKey)
    // const {data, isLoading} = useGetAccountPermissionsQuery();
    // const hasAnyPermission = (): boolean => {
    //     if (isLoading) {
    //         return false;
    //     }
    //     if (Array.isArray(permissionKey)){
    //         for (let key of permissionKey) {
    //             if (data!.permissions.includes(key)){
    //                 return true;
    //             }
    //         }
    //         return false;
    //     }
    //     return data!.permissions.includes(permissionKey);
    // }
    //
    // return {isLoading, hasAnyPermission};
}