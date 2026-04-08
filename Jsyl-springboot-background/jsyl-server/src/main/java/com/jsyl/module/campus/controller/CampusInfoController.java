package com.jsyl.module.campus.controller;

import com.jsyl.model.campus.entity.CampusInfo;
import com.jsyl.common.result.Result;
import com.jsyl.module.campus.service.CampusInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/jsyl/common")
public class CampusInfoController {

    @Autowired
    private CampusInfoService campusInfoService;

    @GetMapping("/campusList")
    public Result<List<CampusInfo>> list() {
        List<CampusInfo> list = campusInfoService.list();
        return Result.success(list);
    }
}
