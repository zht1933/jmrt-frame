package com.webside.activiti.util;

import javax.servlet.http.HttpServletRequest;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.webside.user.model.UserEntity;

/**
 * 办理人任务分配
 *
 */
@SuppressWarnings("serial")
public class ManagerTaskHandler implements TaskListener {
	
	@Autowired  
	private HttpServletRequest request; 
	
	@Override
	public void notify(DelegateTask delegateTask) {
		/**从新查询当前用户，再获取当前用户对应的领导*/
		// 通过session回话获取当前登录用户的信息
		UserEntity userEntity = (UserEntity) request.getSession().getAttribute("defUserEntity");
		//设置个人任务的办理人
		delegateTask.setAssignee(userEntity.getMgrId().toString());
	}

}
