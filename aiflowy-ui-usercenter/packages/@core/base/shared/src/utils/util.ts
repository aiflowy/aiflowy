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
 * 工作流节点排序（支持多层嵌套）
 * @param nodesJson
 */
export function sortNodes(nodesJson: any): any[] {
  const { nodes, edges } = nodesJson;

  // 创建数据结构
  const nodeMap: any = {};
  const graph: any = {}; // 邻接表
  const inDegree: any = {}; // 入度表
  const parentChildrenMap: Record<string, string[]> = {}; // 记录直接父子关系 { parentId: [childId, ...] }

  // 1. 预处理：建立节点映射并统计直接父子关系
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

    // 收集直接子节点信息
    if (node.parentId) {
      if (!parentChildrenMap[node.parentId]) {
        parentChildrenMap[node.parentId] = [];
      }
      parentChildrenMap[node.parentId]?.push(nodeId);
    }
  });

  // --- 辅助函数：递归获取某个节点的所有后代 ID (包括子节点、孙子节点等) ---
  const getAllDescendants = (parentId: string): string[] => {
    let descendants: string[] = [];
    const children = parentChildrenMap[parentId];
    if (children && children.length > 0) {
      children.forEach((childId) => {
        descendants.push(childId);
        // 递归收集子节点的后代
        descendants = [...descendants, ...getAllDescendants(childId)];
      });
    }
    return descendants;
  };
  // -------------------------------------------------------------

  // 2. 处理参数依赖 (保持不变)
  nodes.forEach((node: any) => {
    const parameters = node.data?.parameters || [];
    parameters.forEach((param: any) => {
      if (param.ref) {
        // 解析引用，例如 "someNodeId.output"
        const [sourceNodeId] = param.ref.split('.');
        // 确保 source 存在且不是指向自己（避免死锁）
        if (
          nodeMap[sourceNodeId] &&
          sourceNodeId !== node.id && // 这里通常不需要处理父子层级，因为参数引用是数据流，
          // 如果数据来自父容器，通常父容器已经 ready。
          // 但为了保险，也可以考虑加入 graph，这里暂保持原逻辑。
          !graph[sourceNodeId].includes(node.id)
        ) {
          graph[sourceNodeId].push(node.id);
          inDegree[node.id]++;
        }
      }
    });
  });

  // 3. 处理边依赖 (修改核心逻辑，支持递归嵌套)
  edges.forEach((edge: any) => {
    const { source, target } = edge;

    // 基础校验
    if (!nodeMap[source] || !nodeMap[target]) return;

    // A. 建立显式的 Source -> Target 依赖
    graph[source].push(target);
    inDegree[target]++;

    // B. 处理父子节点的隐含依赖 (递归版)
    // 如果 Source 是一个容器（有后代），且 Target 在容器外部，
    // 那么 Target 必须等待 Source 的 **所有后代**（包括深层嵌套）执行完毕。

    // 获取 Source 的所有层级后代
    const sourceDescendants = getAllDescendants(source);

    if (sourceDescendants.length > 0) {
      // 判断 Target 是否是 Source 的后代（即边是在容器内部连接的）
      const isTargetInsideSource = sourceDescendants.includes(target);

      if (!isTargetInsideSource) {
        // 如果 Target 在 Source 外部，则 Source 的所有后代都要指向 Target
        // 这样可以确保 Target 排在整个 Source 容器及其内部所有节点之后
        sourceDescendants.forEach((descendantId) => {
          // 防止重复添加边
          // 注意：这里可能会导致 graph 边很多，但对拓扑排序是必要的
          // 如果数据量巨大，可以用 Set 优化，这里用数组演示
          graph[descendantId].push(target);
          inDegree[target]++;
        });
      }
    }
  });

  // 4. 拓扑排序 (Kahn算法)
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
    console.warn(
      'Topological Sort: Circular dependency detected or nodes isolated.',
      {
        total: nodes.length,
        sorted: sortedNodes.length,
      },
    );
    // 兜底策略：将未排序的节点按原始顺序追加到末尾（可选）
    const sortedIds = new Set(sortedNodes.map((n) => n.key));
    const unsorted = nodes
      .filter((n: any) => !sortedIds.has(n.id))
      .map((n: any) => nodeMap[n.id]);
    sortedNodes.push(...unsorted);
  }

  // 返回结果
  return sortedNodes.map((node) => ({
    key: node.key,
    label: node.label,
    original: node.original,
    children: node.children,
    extra: node.extra,
  }));
}
