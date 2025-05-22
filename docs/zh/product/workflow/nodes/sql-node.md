# sql 查询
![sql-node.png](../resource/sql-node.png)

## 参数说明
### 输入参数
点击输入参数后面的"+"即可新增参数
### 占位符
```text
{{...}}：用于占位不可在程序中使用"?"占位的部分，如表名、列名
{{?...}}：用于占位可在程序中使用"?"占位的部分，如查询条件
```
### 输出参数
- queryData：查询数据，数组类型
- queryData 可以增加子属性，属性名为查询结果的具体字段，图中的示例增加了 title 和 id，皆为查询表中的字段，子属性可以被下一个节点读取

## 简单示例
### 示例一：直接读取查询结果
![sql-node-example1.png](../resource/sql-node-example1.png)
上图工作流执行结果如下
```json
{
    "result": {
        "queryData": [
            {
                "id": "277546322004111360",
                "dept_id": 1,
                "tenant_id": 1000000,
                "title": "xxxxx",
                "brand": "openai",
                "support_embed": true,
                "llm_endpoint": "xxxxx",
                "llm_model": "Doubao-pro-4k",
                "llm_api_key": "xxxxx",
                "llm_extra_config": "xxxxx"
            }
        ]
    },
    "errorCode": 0
}
```
### 示例二：读取 sql 查询节点定义的子属性
![sql-node-example2.png](../resource/sql-node-example2.png)
上图工作流中，内容模板节点读取了 sql 查询节点中定义的子属性 id 和 title ，执行结果如下
```json
{
    "result": {
        "queryData": "[277546322004111360],[Doubao-pro-4k]"
    },
    "errorCode": 0
}
```

## 注意事项
> sql 查询节点仅支持查询语句