package com.top.arch.data.link;

/**
 *
 * @param <Obj>
 */
public class LinkNode<Obj> {
    Obj item;
    LinkNode<Obj> next;

    public LinkNode(Obj item) {
        this.item = item;
        this.next = null;
    }
}
