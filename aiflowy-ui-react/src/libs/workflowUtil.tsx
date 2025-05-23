import {Uploader} from "../components/Uploader";
import {Button, Input} from "antd";
import {UploadOutlined} from "@ant-design/icons";

export function DynamicItem(item: any, form: any,  attrName: any): JSX.Element {

    function setNestedField(obj: any, path: string, value: any) {
        const keys = path.split('.');
        let current = obj;

        // 遍历路径（除了最后一个 key）
        for (let i = 0; i < keys.length - 1; i++) {
            const key = keys[i];
            if (!current[key]) {
                current[key] = {}; // 如果中间路径不存在，创建空对象
            }
            current = current[key];
        }

        // 设置最终的值
        current[keys[keys.length - 1]] = value;
        return obj;
    }

    function makeItem(item: any, form: any, attrName: any) {
        let inputComponent;
        switch (item.dataType) {
            case "File":
                inputComponent = (
                    <Uploader
                        maxCount={1}
                        action={'/api/v1/commons/uploadAntd'}
                        children={<Button icon={<UploadOutlined/>}>上传</Button>}
                        onChange={({file}) => {
                            if (file.status === 'done') {
                                let url = file.response?.response.url;
                                if (url.indexOf('http') < 0) {
                                    url = window.location.origin + url;
                                }
                                if (attrName.indexOf(".") > -1) {
                                    const currentValues = form.getFieldsValue();
                                    const updatedValues = setNestedField(currentValues, attrName, url);
                                    form.setFieldsValue(updatedValues);
                                } else {
                                    form.setFieldsValue({
                                        [attrName]: url,
                                    });
                                }

                            }
                        }}
                    />);
                break;
            default:
                inputComponent = <Input/>;
        }
        return inputComponent
    }

    return (
        <>
            {makeItem(item, form, attrName)}
        </>
    )

}

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
            children: "",
            extra: ""
        };
        graph[nodeId] = [];
        inDegree[nodeId] = 0;
    });

    // 处理参数依赖
    nodes.forEach((node:any) => {
        const parameters = node.data?.parameters || [];
        parameters.forEach((param:any) => {
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
    edges.forEach((edge:any) => {
        const { source, target } = edge;
        if (nodeMap[source] && nodeMap[target]) {
            graph[source].push(target);
            inDegree[target]++;
        }
    });

    // 拓扑排序
    const queue = nodes.filter((node:any) => inDegree[node.id] === 0).map((node:any) => node.id);
    const sortedNodes = [];

    while (queue.length) {
        const nodeId = queue.shift();
        sortedNodes.push(nodeMap[nodeId]);

        graph[nodeId].forEach((neighborId:any) => {
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
    return sortedNodes.map(node => ({
        key: node.key,
        label: node.label,
        original: node.original,
        children: node.children,
        extra: node.extra
    }));
}