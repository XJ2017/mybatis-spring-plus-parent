package com.ddblock.mybatis.spring.plus.mapper;

import org.apache.ibatis.session.RowBounds;

/**
 * @author XiaoJia
 * @since 2020/7/11 21:23
 */
public class RowBoundsEx extends RowBounds {

    /**
     * 存放满足SQL查询的总数
     */
    private long count;

    public RowBoundsEx(int offset, int limit) {
        super(offset, limit);
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
