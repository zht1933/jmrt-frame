package com.webside.activiti.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.webside.activiti.model.LeaveBill;
import com.webside.activiti.service.LeaveBillService;
import com.webside.base.basecontroller.BaseController;
import com.webside.common.Common;
import com.webside.user.model.UserEntity;

@Controller//@Controller 负责注册一个bean 到spring 上下文中，bean 的ID 默认为类名称开头字母小写 
@Scope("prototype")//@Scope定义一个Bean 的作用范围,prototype:定义bean可以被多次实例化（使用一次就创建一次）
@RequestMapping("/leaveBill/")//@RequestMapping 映射路径，可以声明到类或方法上
public class LeaveBillAction extends BaseController {
	
	@Autowired
	private LeaveBillService leaveBillService;

	/**
	 * 请假管理首页显示
	 * @return
	 */
	@RequestMapping("home.html")
	public String home(HttpServletRequest request,Model model){
		// 通过session回话获取当前登录用户的信息
		UserEntity userEntity = (UserEntity) request.getSession().getAttribute("defUserEntity");
		//1：查询所有的请假信息（对应a_leavebill），返回List<LeaveBill>
		List<LeaveBill> list = leaveBillService.findLeaveBillList(userEntity.getId()); 
		//放置到上下文对象中
		model.addAttribute("billList", list);
		return Common.BACKGROUND_PATH + "/workFlow/actMain";
	}
	
	/**
	 * 添加请假申请
	 * @return
	 */
	@RequestMapping("actLeaveBillFormInput.html")
	public String actLeaveBillFormInput(HttpServletRequest req,Model model){
		//1：获取请假单ID
		Long id=null;
		if(req.getParameter("id")!=null && !req.getParameter("id").equals(""))
		id = Long.valueOf((String)req.getParameter("id"));
		//修改
		if(id!=null){
			//2：使用请假单ID，查询请假单信息，
			LeaveBill bill = leaveBillService.findLeaveBillById(id);
			//3：将请假单信息放置到栈顶，页面使用struts2的标签，支持表单回显
			model.addAttribute("bill", bill);
		}
		//新增
		return Common.BACKGROUND_PATH + "/workFlow/actLeaveBillFormInput";
	}
	
	/**
	 * 保存/更新，请假申请
	 * 
	 * */
	@RequestMapping(value = "insertLeaveBill.html", method = RequestMethod.POST)
	@ResponseBody
	public Object insertLeaveBill(HttpServletRequest req,LeaveBill bill) {
		// 通过session回话获取当前登录用户的信息
		UserEntity userEntity = (UserEntity) req.getSession().getAttribute("defUserEntity");
		bill.setUser(userEntity);
		//执行保存
		leaveBillService.insertLeaveBill(bill,userEntity.getId());

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		return result;
	}
	
	/**
	 * 删除，请假申请
	 * 
	 * */
	@RequestMapping(value = "deleteLeaveBill.html", method = RequestMethod.POST)
	@ResponseBody
	public Object deleteLeaveBill(HttpServletRequest req){
		//1：获取请假单ID
		Long id=null;
		if(req.getParameter("id")!=null && !req.getParameter("id").equals(""))
		id = Long.valueOf((String)req.getParameter("id"));
		
		//执行删除
		leaveBillService.deleteLeaveBillById(id);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		return result;
	}
	
}
