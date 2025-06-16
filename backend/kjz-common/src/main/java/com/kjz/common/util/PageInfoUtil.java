package com.kjz.common.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kjz.common.entity.po.PageInfo;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangcheng
 */
@Component
public class PageInfoUtil {

    @Resource
    private MapperFacade mapperFacade;

    /**
     * page转换成PageInfo
     *
     * @param source
     * @param target
     * @return
     */
    public <S, T> PageInfo<T> convert(Page<S> source, Class<T> target) {
        if (Objects.isNull(source)) {
            return null;
        }
        List<S> records = source.getRecords();
        PageInfo<T> pageInfo = new PageInfo<>();
        if (CollectionUtils.isNotEmpty(records)) {
            pageInfo.setList(mapperFacade.mapAsList(records, target));
        }
        pageInfo.setPageSize(source.getSize());
        pageInfo.setPageNum(source.getCurrent());
        pageInfo.setTotal(source.getTotal());
        return pageInfo;
    }
}
