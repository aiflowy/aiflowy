/**
 * 格式化文件大小（字节转 B/KB/MB/GB/TB）
 * @param bytes - 文件大小（单位：字节 Byte）
 * @param decimalPlaces - 保留小数位数（默认 2 位）
 * @returns 格式化后的大小字符串（如：1.23 MB、456 B、7.8 GB）
 */
export function formatFileSize(
  bytes: number,
  decimalPlaces: number = 2,
): string {
  // 处理特殊情况：bytes 为 0 或非数字
  if (Number.isNaN(bytes) || bytes < 0) return '0 B';
  if (bytes === 0) return '0 B';

  // 单位数组（从 Byte 到 TB）
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  // 计算合适的单位索引（1 KB = 1024 B，每次除以 1024 切换单位）
  const unitIndex = Math.floor(Math.log(bytes) / Math.log(1024));
  // 计算对应单位的大小（保留指定小数位）
  const formattedSize = (bytes / 1024 ** unitIndex).toFixed(decimalPlaces);

  // 移除末尾多余的 .00（如 2.00 MB → 2 MB，1.50 KB → 1.5 KB）
  const sizeWithoutTrailingZeros = Number.parseFloat(formattedSize).toString();

  // 返回格式化结果（单位与大小拼接）
  return `${sizeWithoutTrailingZeros} ${units[unitIndex]}`;
}
