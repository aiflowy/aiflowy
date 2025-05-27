# 自定义节点

自定义节点，需要前端添加节点信息后，后端也要新增对应的处理逻辑和方法。

## 前端
在  `src/pages/ai/workflowDesign/customNode` 文件夹下预置了一些自定义节点，可作为参考，
下面介绍一下怎么添加自定义节点。
### 新增节点信息 
在 `src/pages/ai/workflowDesign/customNode` 文件夹下新建一个 `yourNode.ts` 文件，里面包含了节点信息，如下：
```typescript
export default {
    'your-node': { // 节点唯一标识
        title: '节点名称',
        description: '描述', // 描述
        icon: ' svg 图标', // 图标，可到 https://remixicon.com 获取
        sortNo: 100, // 节点排序
        group: 'base', // 节点位置： 'base' 基础节点 | 'tools' 业务节点,
        rootClass: '', // 根节点容器的样式类名
        rootStyle: '', // 根节点容器的样式。
        parameters: [ // 输入参数
            {
                name: 'paramName', // 参数名称
                nameDisabled: true, // 是否禁用修改参数名称
                title: 'paramTitle', // 参数标题
                dataType: 'String', // 参数类型
                required: true, // 是否必填
                description: '描述', // 参数描述
            },
        ],
        parametersEnable: true, // 是否启用输入参数
        parametersAddEnable: true, // 是否允许添加输入参数
        outputDefs: [], // 输出参数，同输入参数
        outputDefsEnable: true, // 是否启用输出参数
        outputDefsAddEnable: true, // 是否允许添加输出参数
        render: (parent, node, flowInstance) => { // 节点渲染函数
            parent.innerHTML = 
                `<select style="width: 100%">
                    <option>test</option>
                    <option>test1</option>
                    <option>test2</option>
                </select>`;

            parent.querySelector('select')
                ?.addEventListener('change', (e) => {
                    console.log('select change: ', e);
                    flowInstance.updateNodeData(node.id, {
                        attrName: e.target.value
                    });
                })
            ;

            console.log('render: ', node, flowInstance);
        },
        onUpdate: (parent, node) => { // 监听节点数据更新
            console.log('onUpdate: ', node);
        },
        forms: [  // 节点表单
            {
                // 'input' | 'textarea' | 'select' | 'slider' | 'heading' | 'chosen'
                type: 'heading', 
                label: '表单头',
            },
            {
                type: 'select',
                label: '文件类型',
                description: '请选择生成的文件类型',
                name: 'suffix', // 属性名称
                defaultValue: 'docx',
                options: [
                    {
                        label: 'docx',
                        value: 'docx'
                    }
                ]
            },
            {
                // 用法可参考插件节点的代码
                type: 'chosen',
                label: '插件选择',
                chosen: {
                    // 节点自定义属性
                    labelDataKey: 'pluginName',
                    valueDataKey: 'pluginId',
                    // updateNodeData 可动态更新节点属性
                    // value 为选中的 value
                    // label 为选中的 label
                    onChosen: ((updateNodeData: (data: Record<string, any>) => void, value?: string, label?: string, event?: Event) => {
                        console.warn('No onChosen handler provided for plugin-node');
                    })
                }
            }
        ],
    }
}
```
### 引用节点信息
将新增的节点信息添加到同目录下的 `index.ts` 文件中。

```
import yourNode from './yourNode.ts'

export default {
    ...yourNode,
}
```
## 后端

后端使用了 [tinyflow](https://www.tinyflow.cn/zh/) 和 [agents-flex](https://agentsflex.com) 作为工作流的实现，可点击查看对应文档。

### 继承 BaseNode 类

继承 `com.agentsflex.core.chain.node.BaseNode` 类，并实现 `execute` 方法。
```java
public class YourNode extends BaseNode {
    
    @Override
    protected Map<String, Object> execute(Chain chain) {
        Map<String, Object> res = new HashMap<>();
        
        // 获取输入参数
        Map<String, Object> map = chain.getParameterValues(this);
        
        // 获取输出参数定义
        List<Parameter> outputDefs = getOutputDefs();
        
        // 执行节点逻辑，并将结果放入 res 中返回。
        
        return res;
    }
}
```

### 继承 BaseNodeParser 类
继承 `dev.tinyflow.core.parser.BaseNodeParser` 类，并实现 `parse` 方法。
```java
public class YourNodeParser extends BaseNodeParser {
    
    @Override
    public ChainNode parse(JSONObject jsonObject, Tinyflow tinyflow) {
        // 获取节点数据
        JSONObject data = getData(jsonObject);
        // 创建自定义节点
        YourNode yourNode = new YourNode();
        // 添加输入参数
        addParameters(yourNode, data);
        // 添加输出参数
        addOutputDefs(yourNode, data);
        return docNode;
    }

    // 节点名称，要和前端节点名称保持一致
    public String getNodeName() {
        return "your-node";
    }
}
```
### 注册节点

找到 `tech.aiflowy.ai.utils.TinyFlowConfigService` 类的 `setExtraNodeParser` 方法。

添加如下代码：
```java
YourNodeParser yourNodeParser = new YourNodeParser();
chainParser.addNodeParser(yourNodeParser.getNodeName(), yourNodeParser);
```