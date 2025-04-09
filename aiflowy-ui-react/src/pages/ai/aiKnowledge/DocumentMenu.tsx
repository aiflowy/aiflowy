import React from 'react';

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
        <div className="menu-container">
            {items.map((item) => (
                <div
                    key={item.id}
                    className={`menu-item ${selectedId === item.id ? 'active' : ''}`}
                    onClick={() => onSelect(item.id)}
                >
                    {item.label}
                </div>
            ))}
        </div>
    );
};

export default DocumentMenu;