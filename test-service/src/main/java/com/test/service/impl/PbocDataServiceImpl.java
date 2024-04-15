package com.test.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.test.dao.inter.PbocDataDao;
import com.test.domain.entity.PbocData;
import com.test.service.PbocDataService;
import com.test.webapi.dto.input.GetPbocDataListReqDto;
import com.test.webapi.dto.output.GetPbocDataListResDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PbocDataServiceImpl implements PbocDataService {
    @Resource
    private PbocDataDao pbocDataDao;

    @Override
    public IPage getPbocDataListPage(GetPbocDataListReqDto reqDto) {

        IPage<PbocData> pbocDataList = pbocDataDao.selectPage(
                new Page<>(reqDto.getPageNum(),reqDto.getPageSize()),
                new QueryWrapper<PbocData>().lambda()
                .eq(PbocData::getVariableName,reqDto.getName()));

        return pbocDataList;
    }
}
