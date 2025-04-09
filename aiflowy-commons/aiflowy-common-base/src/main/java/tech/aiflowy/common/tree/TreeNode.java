package tech.aiflowy.common.tree;

import com.mybatisflex.annotation.Column;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    @Column(ignore = true)
    private List<TreeNode> children;

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public void addChild(TreeNode newNode) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(newNode);
    }
}
