/**
 * @author kf7688
 * @date 2018/11/13
 * @version 1.0
 */
package com.mqtt.transfer.util;

import lombok.Data;

import java.util.List;

@Data
public class PageResult {
    private Long totalCount;
    private Integer pageNum;
    private Integer PageSize;
    private List<?> list;

    public PageResult(Long totalCount, Integer pageNum, Integer pageSize, List<?> list) {
        this.totalCount = totalCount;
        this.pageNum = pageNum;
        this.PageSize = pageSize;
        this.list = list;
    }

    public static PageResult build(Long totalCount, Integer pageNum, Integer pageSize, List list){
        return new PageResult(totalCount,pageNum,pageSize,list);
    }

}
