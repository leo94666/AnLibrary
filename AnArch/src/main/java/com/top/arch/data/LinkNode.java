package com.top.arch.data;

/**
 *
 * @param <O>
 */
public class LinkNode<O> {
    O item;
    LinkNode<O> next;

    public LinkNode(O item) {
        this.item = item;
        this.next = null;
    }
}
