package com.jedijava.mybatis.hat.face.entity;

/**
 * @author liukaiyang
 * @since 2019/8/29 11:39
 */
public class PageQuery extends QueryEntity{
    private static final long serialVersionUID = 6098601075328469449L;
    /**
     * 当前页码
     */
    private int pageIndex = 1;
    /**
     * 每页的记录数
     */
    private int pageSize = 10;

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

}
