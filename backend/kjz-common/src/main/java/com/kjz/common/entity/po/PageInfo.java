package com.kjz.common.entity.po;

import lombok.Data;

import java.util.List;
@Data
public class PageInfo<T> {
    /**
     * 当前页数
     */
    private Long pageNum;
    /**
     * 每页大小
     */
    private Long pageSize;

    /**
     * 总数量
     */
    private Long total;
    /**
     * 返回list
     */
    private List<T> list;
}
