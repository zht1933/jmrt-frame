package com.webside.excel.service;

import java.util.List;

import com.webside.excel.model.DbEntity;

public interface ExcelService {

	public int insert(DbEntity dbEntity);
    
    public List<DbEntity> queryAllDb();
    
	public int deleteAll();
    
}