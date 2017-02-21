package com.webside.excel.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webside.base.baseservice.impl.AbstractService;
import com.webside.excel.mapper.ExcelMapper;
import com.webside.excel.model.DbEntity;
import com.webside.excel.service.ExcelService;

@Service("excelService")//@Service 向spring 上下文中注册服务bean
public class ExcelServiceImpl extends AbstractService<DbEntity, Long> implements ExcelService{

	@Autowired
	private ExcelMapper excelMapper;
	
	//这句必须要加上。不然会报空指针异常，因为在实际调用的时候不是BaseMapper调用，而是具体的mapper，这里为userMapper
	@Autowired
	public void setBaseMapper() {
		super.setBaseMapper(excelMapper);
	}

	@Override
	public List<DbEntity> queryAllDb() {
		List<DbEntity> list = excelMapper.queryAllDb();
		return list;
	}

	@Override
	public int deleteAll() {
		excelMapper.deleteAll();
		return 0;
	}
}
