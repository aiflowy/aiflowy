import React from 'react';
import '../style/document.css'; // 样式文件放这里

interface MenuItem {
    id: string;
    label: string;
}

interface MenuProps {
    items: MenuItem[];
    onSelect: (id: string) => void;
    selectedId: string | null;
}

const DocumentMenu: React.FC<MenuProps> = ({ items, onSelect, selectedId }) => {
    return (
        <div className="document-menu">
            <ul className="menu-list">
                {items.map((item) => (
                    <li
                        key={item.id}
                        className={`menu-item ${selectedId === item.id ? 'active' : ''}`}
                        onClick={() => onSelect(item.id)}
                    >
                        {item.label}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default DocumentMenu;