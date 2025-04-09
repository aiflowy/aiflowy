package tech.aiflowy.common.tree;

import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.StringUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class Tree<T extends TreeNode> {

    private List<T> root;
    private final Method idGetter;
    private final Method pidGetter;

    public Tree(List<T> nodes, String idFieldName, String pidFieldName) {
        //noinspection unchecked
        this((Class<T>) nodes.get(0).getClass(), idFieldName, pidFieldName);
        for (T node : nodes) {
            this.addNode(node);
        }
    }


    public Tree(Class<T> clazz, String idFieldName, String pidFieldName) {
        String idGetterMethodName = "get" + StringUtil.firstCharToUpperCase(idFieldName);
        this.idGetter = ClassUtil.getFirstMethod(clazz, method -> {
            String methodName = method.getName();
            return methodName.equals(idGetterMethodName) && Modifier.isPublic(method.getModifiers());
        });

        String pidGetterMethodName = "get" + StringUtil.firstCharToUpperCase(pidFieldName);
        this.pidGetter = ClassUtil.getFirstMethod(clazz, method -> {
            String methodName = method.getName();
            return methodName.equals(pidGetterMethodName) && Modifier.isPublic(method.getModifiers());
        });

        if (this.idGetter == null || this.pidGetter == null) {
            throw new IllegalStateException("Can not find method \"" + idGetterMethodName + "\" or \"" + pidGetterMethodName + "\" in class: " + clazz.getName());
        }
    }


    public void addNode(T node) {
        if (root == null) {
            root = new ArrayList<>();
            root.add(node);
        } else {
            addToTree(this.root, node);
        }
    }

    public void print() {
        doPrint(0, this.root);
    }


    private void doPrint(int layerNo, List<T> nodes) {
        if (nodes != null && !nodes.isEmpty()) {
            for (T node : nodes) {
                System.out.println(getPrefix(layerNo) + node.toString());
                //noinspection unchecked
                doPrint(layerNo + 1, (List<T>) node.getChildren());
            }
        }
    }

    public static String getPrefix(int layerNo) {
        if (layerNo == 0) {
            return "";
        } else if (layerNo == 1) {
            return "|-";
        } else {
            StringBuilder sb = new StringBuilder("|-");
            for (int i = 0; i < (layerNo - 1); i++) {
                sb.append("--");
            }
            return sb.toString();
        }
    }

    private void addToTree(List<T> root, T newNode) {
        List<T> children = new ArrayList<>();
        T parent = findParentAndChildren(root, newNode, children);
        if (!children.isEmpty()) {
            //noinspection unchecked
            newNode.setChildren((List<TreeNode>) children);
        }
        if (parent == null) {
            root.add(newNode);
        } else {
            parent.addChild(newNode);
        }
    }

    private T findParentAndChildren(List<T> root, T newNode, List<T> children) {
        T parent = null;
        for (T node : root) {
            if (children != null && equalsInString(getId(newNode), getPid(node))) {
                children.add(node);
            }

            if (parent == null) {
                if (equalsInString(getId(node), getPid(newNode))) {
                    parent = node;
                } else if (node.getChildren() != null) {
                    //noinspection unchecked
                    parent = findParentAndChildren((List<T>) node.getChildren(), newNode, null);
                }
            }
        }
        if (children != null && !children.isEmpty()) {
            root.removeAll(children);
        }
        return parent;
    }

    private Object getId(Object object) {
        try {
            return idGetter.invoke(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getPid(Object object) {
        try {
            return pidGetter.invoke(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> getRoot() {
        return root;
    }

    public List<T> getRootAsLayer() {
        List<T> all = new ArrayList<>();
        addNodesToList(this.root, all);
        return all;
    }

    private void addNodesToList(List<T> nodes, List<T> list) {
        if (nodes != null && !nodes.isEmpty()) {
            for (T node : nodes) {
                list.add(node);
                //noinspection unchecked
                addNodesToList((List<T>) node.getChildren(), list);
            }
        }
    }


    public static <T, X extends TreeNode> List<T> tryToTree(List<T> list) {
        return tryToTree(list, true);
    }

    @SuppressWarnings("unchecked")
    public static <T, X extends TreeNode> List<T> tryToTree(List<T> list, Boolean condition) {
        if (condition != null && condition && list != null && !list.isEmpty()) {
            T data = list.get(0);
            if (data != null && TreeNode.class.isAssignableFrom(data.getClass())) {
                Tree<X> tree = new Tree<>((List<X>) list, "id", "pid");
                list = (List<T>) tree.getRoot();
            }
        }
        return list;
    }

    private static boolean equalsInString(Object o1, Object o2) {
        if (o1 == o2) return true;
        if (o1 == null || o2 == null) return false;
        return o1.toString().equals(o2.toString());
    }

    @SuppressWarnings("unchecked")
    public static <T, X extends TreeNode> List<T> tryToTree(List<T> list,String idFieldName,String pidFieldName) {
        if (list != null && !list.isEmpty()) {
            T data = list.get(0);
            if (data != null && TreeNode.class.isAssignableFrom(data.getClass())) {
                Tree<X> tree = new Tree<>((List<X>) list, idFieldName, pidFieldName);
                list = (List<T>) tree.getRoot();
            }
        }
        return list;
    }
}
