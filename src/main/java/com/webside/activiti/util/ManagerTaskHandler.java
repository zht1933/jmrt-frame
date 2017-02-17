package com.webside.activiti.util;

import javax.servlet.http.HttpSession;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.webside.user.model.UserEntity;

/**
 * 办理人任务分配
 *
 */
@SuppressWarnings("serial")
public class ManagerTaskHandler implements TaskListener {
	
	@Override
	public void notify(DelegateTask delegateTask) {
		/**从新查询当前用户，再获取当前用户对应的领导*/
		// 通过session回话获取当前登录用户的信息
		HttpSession session = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
		UserEntity userEntity = (UserEntity)session.getAttribute("defUserEntity");
		
		//设置个人任务的办理人
		delegateTask.setAssignee(userEntity.getMgrId().toString());
	}
	

}
