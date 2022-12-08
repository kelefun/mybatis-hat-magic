package com.jedijava.mybatis.hat.face.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class PageResult<E> implements Serializable {

    private static final long serialVersionUID = 2905998851731730102L;

    public PageResult() {
    }

    public PageResult(Integer totalItem, Integer pageIndex, Integer pageSize, List<E> items) {
        this.totalItem = totalItem;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.items = items;
    }

    public static PageResult empty() {
        return new PageResult(0, 1, 10, Collections.EMPTY_LIST);
    }

    /**
     * 总记录数
     */
    private int totalItem = 0;

    /**
     * 当前页码
     */
    private int pageIndex = 1;

    /**
     * 每页的记录数
     */
    private int pageSize = 10;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 某一页的记录
     */
    private List<E> items;


    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return (totalItem + pageSize - 1) / pageSize;
    }


    public List<E> getItems() {
        return items;
    }

    public void setItems(List<E> items) {
        this.items = items;
    }
}
