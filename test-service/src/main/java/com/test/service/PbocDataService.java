package com.test.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.test.webapi.dto.input.GetPbocDataListReqDto;
import com.test.webapi.dto.output.GetPbocDataListResDto;

import java.util.List;

public interface PbocDataService {

    IPage getPbocDataListPage(GetPbocDataListReqDto reqDto);
}
