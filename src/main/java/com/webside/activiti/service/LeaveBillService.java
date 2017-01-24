package com.webside.activiti.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.webside.activiti.model.LeaveBill;

public interface LeaveBillService {

	List<LeaveBill> findLeaveBillList(long userid);

	void insertLeaveBill(LeaveBill leaveBill,long userid);

	LeaveBill findLeaveBillById(Long id);

	void deleteLeaveBillById(Long id);

}
