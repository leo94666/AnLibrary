package com.top.screenshot;

import java.util.ArrayList;

public class ViewTreeNode<T> {
    T value;
    ViewTreeNode<T> parent;
    ArrayList<ViewTreeNode<T>> children;

    public ViewTreeNode(T value) {
        this.value = value;
    }


    public void addChildren(T value) {
        ViewTreeNode<T> children = new ViewTreeNode<>(value);
        if (this.children==null){
            this.children= new ArrayList<>();
        }
        this.children.add(children);
    }

    public void addChildren( ViewTreeNode<T> node) {
        if (this.children==null){
            this.children= new ArrayList<>();
        }
        this.children.add(node);
    }

}
