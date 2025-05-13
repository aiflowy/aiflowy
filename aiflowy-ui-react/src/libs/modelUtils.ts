export interface ModelSpec {
    /** 核心模型名称（小写） */
    name: string
    /** 是否为对话模型 */
    supportChat: boolean
    /** 是否支持函数调用 */
    supportFunctionCalling: boolean
    /** 是否为embedding模型 */
    supportEmbed: boolean
}


export const MODEL_SPECS: ModelSpec[] = [
    // LLM 系列
    {
        name: 'deepseek-r1',
        supportChat: true,
        supportFunctionCalling: false,
        supportEmbed: false
    },
    {
        name: 'qwen2',
        supportChat: true,
        supportFunctionCalling: true,
        supportEmbed: false
    },
    {
        name: 'qwen2.5',
        supportChat: true,
        supportFunctionCalling: true,
        supportEmbed: false
    },
    {
        name: 'qwen3',
        supportChat: true,
        supportFunctionCalling: true,
        supportEmbed: false
    },
    {
        name: 'gpt-4o',
        supportChat: true,
        supportFunctionCalling: true,
        supportEmbed: false
    },
    {
        name: 'gpt-3.5-turbo',
        supportChat: true,
        supportFunctionCalling: true,
        supportEmbed: false
    },


    // Embedding 系列
    {
        name: 'dmeta-embedding-zh',
        supportChat: false,
        supportFunctionCalling: false,
        supportEmbed: true
    },
    {
        name: 'nomic-embed-text',
        supportChat: false,
        supportFunctionCalling: false,
        supportEmbed: true
    },
    {
        name: 'mxbai-embed-large',
        supportChat: false,
        supportFunctionCalling: false,
        supportEmbed: true
    },
    {
        name: 'bge-m3',
        supportChat: false,
        supportFunctionCalling: false,
        supportEmbed: true
    },
    {
        name: 'snowflake-arctic-embed',
        supportChat: false,
        supportFunctionCalling: false,
        supportEmbed: true
    },
    {
        name: 'all-minilm',
        supportChat: false,
        supportFunctionCalling: false,
        supportEmbed: true
    },
    {
        name: 'bge-large',
        supportChat: false,
        supportFunctionCalling: false,
        supportEmbed: true
    },
    {
        name: 'snowflake-arctic-embed2',
        supportChat: false,
        supportFunctionCalling: false,
        supportEmbed: true
    },
    {
        name: 'paraphrase-multilingual',
        supportChat: false,
        supportFunctionCalling: false,
        supportEmbed: true
    },
    {
        name: 'granite-embedding',
        supportChat: false,
        supportFunctionCalling: false,
        supportEmbed: true
    }
];

// 创建快速查找的Map
const MODEL_MAP = new Map<string, ModelSpec>(
    MODEL_SPECS.map(model => [model.name, model])
);

// 带缓存的名称解析器
const parseCoreModelName = (() => {
    const cache = new Map<string, string>();

    return (fullName: string): string => {
        const lowerName = fullName.toLowerCase().trim();

        if (cache.has(lowerName)) {
            return cache.get(lowerName)!;
        }
        const parse = (name: string) => {
            // 步骤1：分割命名空间
            const namespaceParts = name.split('/');
            const lastSegment = namespaceParts[namespaceParts.length - 1];

            // 步骤2：分割版本标签
            const versionParts = lastSegment.split(':');

            // 返回核心模型名称（不带版本号）
            return versionParts[0];
        };
        const coreName = parse(lowerName);
        cache.set(lowerName, coreName);
        return coreName;
    };
})();

// 通用模型属性查询
const getModelSpec = (modelName: string): ModelSpec | undefined => {
    const coreName = parseCoreModelName(modelName);
    return MODEL_MAP.get(coreName);
};

// 是对话模型
export const isSupportChat = (modelName: string): boolean => {
    return getModelSpec(modelName)?.supportChat ?? false;
};

// 支持方法调用
export const isSupportFunctionCalling = (modelName: string): boolean => {
    return getModelSpec(modelName)?.supportFunctionCalling ?? false;
};

// 是Embedding模型
export const isSupportEmbed = (modelName: string): boolean => {
    const spec = getModelSpec(modelName);
    return spec ? spec.supportEmbed : modelName.toLowerCase().includes('embed');
};

// 获取完整规格信息
export const getModelDetail = (modelName: string): ModelSpec | null => {
    const spec = getModelSpec(modelName);
    return spec ? { ...spec } : null;
};
