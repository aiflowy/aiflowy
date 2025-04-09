import React from 'react';

interface ContentProps {
    selectedMenu: string | null;
}

const Content: React.FC<ContentProps> = ({ selectedMenu }) => {
    // 根据选中的菜单项渲染不同的内容
    switch (selectedMenu) {
        case 'file-management':
            return <FileManagement />;
        case 'file-import':
            return <FileImport />;
        case 'material-management':
            return <MaterialManagement />;
        case 'search-test':
            return <SearchTest />;
        case 'configuration':
            return <Configuration />;
        default:
            return <DefaultContent />;
    }
};

// 示例内容组件
const FileManagement: React.FC = () => (
    <div className="content">
        <h2>文件管理</h2>
        <p>这里是文件管理的内容。</p>
    </div>
);

const FileImport: React.FC = () => (
    <div className="content">
        <h2>文件导入</h2>
        <p>这里是文件导入的内容。</p>
    </div>
);

const MaterialManagement: React.FC = () => (
    <div className="content">
        <h2>素材管理</h2>
        <p>这里是素材管理的内容。</p>
    </div>
);

const SearchTest: React.FC = () => (
    <div className="content">
        <h2>检索测试</h2>
        <p>这里是检索测试的内容。</p>
    </div>
);

const Configuration: React.FC = () => (
    <div className="content">
        <h2>配置</h2>
        <p>这里是配置的内容。</p>
    </div>
);

const DefaultContent: React.FC = () => (
    <div className="content">
        <h2>请选择一个菜单项</h2>
        <p>请点击左侧菜单以查看具体内容。</p>
    </div>
);

export default Content;