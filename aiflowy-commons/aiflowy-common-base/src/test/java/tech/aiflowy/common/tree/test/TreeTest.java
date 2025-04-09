package tech.aiflowy.common.tree.test;

import tech.aiflowy.common.tree.Tree;

public class TreeTest {

    public static void main(String[] args) {

        Tree<TreeObj> tree = new Tree<>(TreeObj.class, "id", "pid");
        tree.addNode(new TreeObj("1", "0", "11"));
        tree.addNode(new TreeObj("2", "0", "22"));
        tree.addNode(new TreeObj("3", "1", "33"));
        tree.print();

        System.out.println("\n---------------------------");
        tree.addNode(new TreeObj("4", "2", "44"));
        tree.addNode(new TreeObj("5", "2", "12"));
        tree.print();


        System.out.println("\n---------------------------");
        tree.addNode(new TreeObj("6", "4", "12"));
        tree.addNode(new TreeObj("7", "5", "12"));
        tree.print();


        System.out.println("\n---------------------------");
        tree.addNode(new TreeObj("8", "3", "12"));
        tree.addNode(new TreeObj("9", "3", "12"));
        tree.print();

        System.out.println("\n---------------------------");
        tree.addNode(new TreeObj("8", "0", "12"));
        tree.addNode(new TreeObj("9", "0", "12"));
        tree.print();


        System.out.println("\n---------------------------");
        tree.addNode(new TreeObj("20", "8", "12"));
        tree.addNode(new TreeObj("21", "9", "12"));
        tree.print();

    }
}
