export interface Page<T> {
    pageNumber: number,
    pageSize: number,
    totalPage: number,
    totalRow: number,
    records: T[]
}