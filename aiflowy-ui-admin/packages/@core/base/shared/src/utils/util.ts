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

  // 1. 预处理：建立节点映射并统计父子关系
  const parentChildrenMap: Record<string, string[]> = {}; // 记录 { parentId: [childId, ...] }

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

    // 收集子节点信息
    if (node.parentId) {
      if (!parentChildrenMap[node.parentId]) {
        parentChildrenMap[node.parentId] = [];
      }
      // @ts-ignore - parentChildrenMap[node.parentId] is defined
      parentChildrenMap[node.parentId].push(nodeId);
    }
  });

  // 2. 处理参数依赖 (保持不变)
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

  // 3. 处理边依赖 (修改核心逻辑)
  edges.forEach((edge: any) => {
    const { source, target } = edge;
    if (nodeMap[source] && nodeMap[target]) {
      // 建立显式的 Source -> Target 依赖
      graph[source].push(target);
      inDegree[target]++;

      // 【新增逻辑】处理父子节点的隐含依赖
      // 如果 Source 节点拥有子节点，且 Target 节点不是 Source 的子节点
      // 那么 Target 必须等待 Source 的所有子节点执行完毕
      // 场景：循环节点(Source) -> 结束节点(Target)。结束节点必须排在循环节点内部所有子节点之后。
      if (parentChildrenMap[source]) {
        const isTargetChildOfSource =
          nodeMap[target].original.parentId === source;

        if (!isTargetChildOfSource) {
          parentChildrenMap[source].forEach((childId) => {
            // 让子节点指向 Target，强制 Target 排在子节点后面
            graph[childId].push(target);
            inDegree[target]++;
          });
        }
      }
    }
  });

  // 4. 拓扑排序 (保持不变)
  const queue = nodes
    .filter((node: any) => inDegree[node.id] === 0)
    .map((node: any) => node.id);
  const sortedNodes = [];

  while (queue.length > 0) {
    const nodeId = queue.shift();
    sortedNodes.push(nodeMap[nodeId]);

    if (graph[nodeId]) {
      graph[nodeId].forEach((neighborId: any) => {
        inDegree[neighborId]--;
        if (inDegree[neighborId] === 0) {
          queue.push(neighborId);
        }
      });
    }
  }

  // 检查循环依赖
  if (sortedNodes.length !== nodes.length) {
    console.error('检测到循环依赖，排序结果可能不完整');
    // 如果有未被处理的节点，通常是因为成环，可以考虑把剩余节点按原始顺序补在后面，或者报错
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
