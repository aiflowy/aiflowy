export default {
    'download-node': {
        title: '素材同步',
        group: 'base',
        description: '下载素材文件并保存到系统素材库',
        icon:'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor"><path d="M13 12H16L12 16L8 12H11V8H13V12ZM15 4H5V20H19V8H15V4ZM3 2.9918C3 2.44405 3.44749 2 3.9985 2H16L20.9997 7L21 20.9925C21 21.5489 20.5551 22 20.0066 22H3.9934C3.44476 22 3 21.5447 3 21.0082V2.9918Z"></path></svg>',
        sortNo: 811,
        parametersAddEnable: false,
        outputDefsAddEnable: false,
        parameters: [
            {
                name: 'originUrl',
                nameDisabled: true,
                title: '源地址',
                dataType: 'String',
                required: true,
                description: '文件的源地址',
            },
        ],
        outputDefs: [
            {
                name: 'resourceUrl',
                title: '保存后的地址',
                dataType: 'String',
                dataTypeDisabled:true,
                required: true,
                parametersAddEnable: false,
                description: '保存后的地址',
                deleteDisabled:true,
            },
        ],
        forms: [  // 节点表单
            {
                // 'input' | 'textarea' | 'select' | 'slider' | 'heading' | 'chosen'
                type: 'heading',
                label: '保存选项',
            },
            {
                type: 'select',
                label: '素材类型',
                description: '请选择素材的类型',
                name: 'resourceType', // 属性名称
                defaultValue: '99',
                options: [
                    {
                        label: '图片',
                        value: '0'
                    },
                    {
                        label: '视频',
                        value: '1'
                    },
                    {
                        label: '音频',
                        value: '2'
                    },
                    {
                        label: '文档',
                        value: '3'
                    },
                    {
                        label: '其他',
                        value: '99'
                    }
                ]
            },
            // {
            //     // 用法可参考插件节点的代码
            //     type: 'chosen',
            //     label: '插件选择',
            //     chosen: {
            //         // 节点自定义属性
            //         labelDataKey: 'pluginName',
            //         valueDataKey: 'pluginId',
            //         // updateNodeData 可动态更新节点属性
            //         // value 为选中的 value
            //         // label 为选中的 label
            //         onChosen: ((updateNodeData: (data: Record<string, any>) => void, value?: string, label?: string, event?: Event) => {
            //             console.warn('No onChosen handler provided for plugin-node');
            //         })
            //     }
            // }
        ],
    }
}