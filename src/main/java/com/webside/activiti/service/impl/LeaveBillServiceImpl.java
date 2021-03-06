package com.webside.activiti.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webside.activiti.mapper.LeaveBillMapper;
import com.webside.activiti.model.LeaveBill;
import com.webside.activiti.service.LeaveBillService;
import com.webside.base.baseservice.impl.AbstractService;
import com.webside.user.model.UserEntity;

@Service("leaveBillService")//@Service 向spring 上下文中注册服务bean
public class LeaveBillServiceImpl extends AbstractService<LeaveBill, Long> implements LeaveBillService {

	@Autowired
	private LeaveBillMapper leaveBillMapper;
	
	//这句必须要加上。不然会报空指针异常，因为在实际调用的时候不是BaseMapper调用，而是具体的mapper，这里为userMapper
	@Autowired
	public void setBaseMapper() {
		super.setBaseMapper(leaveBillMapper);
	}
	
	/**查询自己的请假单的信息*/
	@Override
	public List<LeaveBill> findLeaveBillList(long userid) {
		List<LeaveBill> list = leaveBillMapper.findLeaveBillList(userid);
		return list;
	}
	
	/**保存请假单*/
	@Override
	public void insertLeaveBill(LeaveBill leaveBill,long userid) {
		//获取请假单ID
		Long id = leaveBill.getId();
		/**新增保存*/
		if(id==null){
			//保存请假单表，添加一条数据
			leaveBillMapper.insertLeaveBill(leaveBill);
			
			Map<String, Object> parameter = new HashMap<String, Object>();
			parameter.put("leaveBillId", leaveBill.getId());
			parameter.put("userId", userid);
			leaveBillMapper.insertLeaveBillUser(parameter);
		}
		/**更新保存*/
		else{
			//1：执行update的操作，完成更新
			leaveBillMapper.updateLeaveBill(leaveBill);
			leaveBillMapper.updateLeaveBillUser(leaveBill);
		}
		
	}
	
	/**使用请假单ID，查询请假单的对象*/
	@Override
	public LeaveBill findLeaveBillById(Long id) {
		LeaveBill bill = leaveBillMapper.findLeaveBillById(id);
		return bill;
	}
	
	/**使用请假单ID，删除请假单*/
	@Override
	public void deleteLeaveBillById(Long id) {
		leaveBillMapper.deleteLeaveBillUserById(id);
		leaveBillMapper.deleteLeaveBillById(id);
	}

}
