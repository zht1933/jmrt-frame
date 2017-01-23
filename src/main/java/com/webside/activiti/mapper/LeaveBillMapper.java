package com.webside.activiti.mapper;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.webside.activiti.model.LeaveBill;
import com.webside.base.basemapper.BaseMapper;

@Repository//@Repository 向spring 上下文中注册存储层bean
public interface LeaveBillMapper extends BaseMapper<LeaveBill, Long>{

	List<LeaveBill> findLeaveBillList();

	void insertLeaveBill(LeaveBill leaveBill);

	void updateLeaveBill(LeaveBill leaveBill);

	LeaveBill findLeaveBillById(Long id);

	void deleteLeaveBillById(Long id);
	
}
