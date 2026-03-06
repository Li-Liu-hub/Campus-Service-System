package com.jsyl.controller;

import com.jsyl.entity.CampusInfo;
import com.jsyl.result.Result;
import com.jsyl.service.CampusInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/jsyl/common")
@Api(tags = "通用接口")
public class CampusInfoController {

    @Autowired
    private CampusInfoService campusInfoService;

    @GetMapping("/campusList")
    @ApiOperation("获取校区列表")
    public Result<List<CampusInfo>> list() {
        List<CampusInfo> list = campusInfoService.list();
        return Result.success(list);
    }
}
