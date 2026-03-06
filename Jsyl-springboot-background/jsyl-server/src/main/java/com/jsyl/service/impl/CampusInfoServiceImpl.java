package com.jsyl.service.impl;

import com.jsyl.entity.CampusInfo;
import com.jsyl.mapper.CampusInfoMapper;
import com.jsyl.service.CampusInfoService;
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
