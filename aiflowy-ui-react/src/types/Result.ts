export interface Result<T> {
    errorCode: number,
    message: number,
    data: T,
    [key: string]: any;
}