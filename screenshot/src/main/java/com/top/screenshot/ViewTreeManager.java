package com.top.screenshot;

public class ViewTreeManager {


    /**
     * getTreeNum: 判断树中节点个数
     *
     * @param root 根节点
     * @return int 返回类型
     */
    public static <T> int getTreeNum(ViewTreeNode<T> root) {
        if (root == null) {
            return 0;
        }
        int num = root.children.size();
        for (ViewTreeNode<T> childNode : root.children) {
            num += getTreeNum(childNode);
        }
        return num + 1;
    }





}
