package com.webside.excel.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.webside.base.basemapper.BaseMapper;
import com.webside.excel.model.DbEntity;

@Repository//@Repository 向spring 上下文中注册存储层bean
public interface ExcelMapper extends BaseMapper<DbEntity, Long>{
	
	public List<DbEntity> queryAllDb();
	public int deleteAll();
}
