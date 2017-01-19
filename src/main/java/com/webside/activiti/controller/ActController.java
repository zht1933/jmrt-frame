package com.webside.activiti.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.webside.activiti.service.WorkflowService;
import com.webside.base.basecontroller.BaseController;
import com.webside.common.Common;
import com.webside.exception.AjaxException;
import com.webside.exception.ServiceException;
import com.webside.shiro.ShiroAuthenticationManager;
import com.webside.user.model.UserEntity;
import com.webside.user.model.UserInfoEntity;
import com.webside.util.EndecryptUtils;

@Controller//@Controller 负责注册一个bean 到spring 上下文中，bean 的ID 默认为类名称开头字母小写 
@Scope("prototype")//@Scope定义一个Bean 的作用范围,prototype:定义bean可以被多次实例化（使用一次就创建一次）
@RequestMapping("/act/")//@RequestMapping 映射路径，可以声明到类或方法上
public class ActController extends BaseController {
	
	@Autowired
	private WorkflowService workflowService;
	
	/**
	 * 流程申请管理
	 * @return
	 */
	@RequestMapping("actMain.html")
	public String actMain() {

		return Common.BACKGROUND_PATH + "/workFlow/actMain";
		
	}
	
	/**
	 * 流程任务管理
	 * @return
	 */
	@RequestMapping("actTask.html")
	public String actTask() {

		return Common.BACKGROUND_PATH + "/workFlow/actTask";
		
	}
	
	/**
	 * 流程部署管理
	 * @param model
	 * @return
	 */
	@RequestMapping("actDeploy.html")
	public String actDeploy(Model model) {

		//1:查询部署对象信息，对应表（act_re_deployment）
		List<Deployment> depList = workflowService.findDeploymentList();
		//2:查询流程定义的信息，对应表（act_re_procdef）
		List<ProcessDefinition> pdList = workflowService.findProcessDefinitionList();
		//放置到上下文对象中
		model.addAttribute("depList", depList);
		model.addAttribute("pdList", pdList);

		return Common.BACKGROUND_PATH + "/workFlow/actDeploy";
		
	}

	/**
	 * 发布流程
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "newdeploy.html", method = RequestMethod.POST)
	@ResponseBody
	public Object newdeploy(HttpServletRequest request) {

		// 获取页面传递的值
		// 1：获取页面上传递的zip格式的文件，格式是File类型
		// 这个地方能正常转换，没有报错
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile file = multipartRequest.getFile("file");

		// 文件名称
		String filename = request.getParameter("filename").toString();
		// 完成部署
		workflowService.insertNewDeploye(file, filename);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);

		return result;
	}
	
}