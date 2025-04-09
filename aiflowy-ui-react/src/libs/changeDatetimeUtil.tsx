//把数据库中dateTime类型的日期转换为yyyy-MM-dd HH:mm:ss格式的日期
export const convertDatetimeUtil = (modified: number) => {
    // 将时间戳转换为日期对象
    const date = new Date(modified);

    // 格式化为年月日时分秒
    const formattedDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(
        date.getDate()
    ).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(
        2,
        '0'
    )}:${String(date.getSeconds()).padStart(2, '0')}`;

    return <span>{formattedDate}</span>;
}