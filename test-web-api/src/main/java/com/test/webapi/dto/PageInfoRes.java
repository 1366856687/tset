package com.test.webapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class PageInfoRes<T>  implements Serializable {

    /**
     * @Fields pageNum : 页码
     */
    private int pageNum;

    /**
     * @Fields pageSize : 页大小
     */
    private int pageSize;

    /**
     * @Fields total : 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 当页记录列表
     */
    private List<T> list = Collections.emptyList();
}
