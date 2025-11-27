export function bindMethods<T extends object>(instance: T): void {
  const prototype = Object.getPrototypeOf(instance);
  const propertyNames = Object.getOwnPropertyNames(prototype);

  propertyNames.forEach((propertyName) => {
    const descriptor = Object.getOwnPropertyDescriptor(prototype, propertyName);
    const propertyValue = instance[propertyName as keyof T];

    if (
      typeof propertyValue === 'function' &&
      propertyName !== 'constructor' &&
      descriptor &&
      !descriptor.get &&
      !descriptor.set
    ) {
      instance[propertyName as keyof T] = propertyValue.bind(instance);
    }
  });
}

/**
 * 获取嵌套对象的字段值
 * @param obj - 要查找的对象
 * @param path - 用于查找字段的路径，使用小数点分隔
 * @returns 字段值，或者未找到时返回 undefined
 */
export function getNestedValue<T>(obj: T, path: string): any {
  if (typeof path !== 'string' || path.length === 0) {
    throw new Error('Path must be a non-empty string');
  }
  // 把路径字符串按 "." 分割成数组
  const keys = path.split('.') as (number | string)[];

  let current: any = obj;

  for (const key of keys) {
    if (current === null || current === undefined) {
      return undefined;
    }
    current = current[key as keyof typeof current];
  }

  return current;
}

/**
 * 获取资源类型
 * @param suffix
 */
export const getResourceType = (suffix: any): number => {
  if (!suffix) {
    return 99;
  }
  const img = ['png', 'jpg', 'jpeg', 'gif', 'webp', 'svg'];
  const video = ['mp4', 'm4v', 'webm', 'ogg'];
  const audio = ['mp3', 'wav', 'ogg'];
  const doc = [
    'doc',
    'docx',
    'xls',
    'xlsx',
    'ppt',
    'pptx',
    'pdf',
    'txt',
    'md',
    'csv',
  ];
  if (img.includes(suffix)) {
    return 0;
  } else if (video.includes(suffix)) {
    return 1;
  } else if (audio.includes(suffix)) {
    return 2;
  } else if (doc.includes(suffix)) {
    return 3;
  } else {
    return 99;
  }
};

/**
 * 将字节格式化为可读大小
 */
export function formatBytes(bytes: any, decimals = 2) {
  if (bytes === 0 || !bytes) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${Number.parseFloat((bytes / k ** i).toFixed(decimals))} ${sizes[i]}`;
}

/**
 * 获取选项
 * @param labelKey
 * @param valueKey
 * @param options
 */
export function getOptions(labelKey: string, valueKey: string, options: any[]) {
  if (options && options.length > 0) {
    return options.map((item: any) => {
      return {
        label: item[labelKey],
        value: item[valueKey],
      };
    });
  }
  return [];
}

/**
 * 工作流节点排序
 * @param nodesJson
 */
export function sortNodes(nodesJson: any): any[] {
  const { nodes, edges } = nodesJson;

  // 创建数据结构
  const nodeMap: any = {};
  const graph: any = {};
  const inDegree: any = {};

  // 初始化
  nodes.forEach((node: any) => {
    const nodeId = node.id;
    nodeMap[nodeId] = {
      key: nodeId,
      label: node.data?.title || nodeId,
      original: node,
      children: '',
      extra: '',
    };
    graph[nodeId] = [];
    inDegree[nodeId] = 0;
  });

  // 处理参数依赖
  nodes.forEach((node: any) => {
    const parameters = node.data?.parameters || [];
    parameters.forEach((param: any) => {
      if (param.ref) {
        const [sourceNodeId] = param.ref.split('.');
        if (nodeMap[sourceNodeId]) {
          graph[sourceNodeId].push(node.id);
          inDegree[node.id]++;
        }
      }
    });
  });

  // 处理边依赖
  edges.forEach((edge: any) => {
    const { source, target } = edge;
    if (nodeMap[source] && nodeMap[target]) {
      graph[source].push(target);
      inDegree[target]++;
    }
  });

  // 拓扑排序
  const queue = nodes
    .filter((node: any) => inDegree[node.id] === 0)
    .map((node: any) => node.id);
  const sortedNodes = [];

  while (queue.length > 0) {
    const nodeId = queue.shift();
    sortedNodes.push(nodeMap[nodeId]);

    graph[nodeId].forEach((neighborId: any) => {
      inDegree[neighborId]--;
      if (inDegree[neighborId] === 0) {
        queue.push(neighborId);
      }
    });
  }

  // 检查循环依赖
  if (sortedNodes.length !== nodes.length) {
    console.error('检测到循环依赖，排序结果可能不完整');
  }

  // 只返回需要的格式
  return sortedNodes.map((node) => ({
    key: node.key,
    label: node.label,
    original: node.original,
    children: node.children,
    extra: node.extra,
  }));
}
