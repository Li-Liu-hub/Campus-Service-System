package com.jsyl.module.campus.service.impl;

import com.jsyl.model.campus.entity.CampusInfo;
import com.jsyl.module.campus.mapper.CampusInfoMapper;
import com.jsyl.module.campus.service.CampusInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CampusInfoServiceImpl implements CampusInfoService {

    @Autowired
    private CampusInfoMapper campusInfoMapper;

    @Override
    public List<CampusInfo> list() {
        return campusInfoMapper.list();
    }
}
