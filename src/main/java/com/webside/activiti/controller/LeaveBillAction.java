package com.webside.activiti.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.webside.activiti.model.LeaveBill;
import com.webside.activiti.service.LeaveBillService;
import com.webside.base.basecontroller.BaseController;

@Controller//@Controller 负责注册一个bean 到spring 上下文中，bean 的ID 默认为类名称开头字母小写 
@Scope("prototype")//@Scope定义一个Bean 的作用范围,prototype:定义bean可以被多次实例化（使用一次就创建一次）
@RequestMapping("/leaveBill/")//@RequestMapping 映射路径，可以声明到类或方法上
public class LeaveBillAction extends BaseController {
	
	private LeaveBillService leaveBillService;

	public void setLeaveBillService(LeaveBillService leaveBillService) {
		this.leaveBillService = leaveBillService;
	}

	/**
	 * 请假管理首页显示
	 * @return
	 */
	public String home(Model model){
		//1：查询所有的请假信息（对应a_leavebill），返回List<LeaveBill>
		List<LeaveBill> list = leaveBillService.findLeaveBillList(); 
		//放置到上下文对象中
		model.addAttribute("list", list);
		return "home";
	}
	
	/**
	 * 添加请假申请
	 * @return
	 */
	public String input(Model model){
		//1：获取请假单ID
//		Long id = leaveBill.getId();
		Long id = (long) 0;
		//修改
		if(id!=null){
			//2：使用请假单ID，查询请假单信息，
			LeaveBill bill = leaveBillService.findLeaveBillById(id);
			//3：将请假单信息放置到栈顶，页面使用struts2的标签，支持表单回显
			model.addAttribute("bill", bill);
		}
		//新增
		return "input";
	}
	
	/**
	 * 保存/更新，请假申请
	 * 
	 * */
	public String insert(HttpServletRequest req) {
		LeaveBill leaveBill = new LeaveBill();
		//执行保存
		leaveBillService.insertLeaveBill(req,leaveBill);
		return "save";
	}
	
	/**
	 * 删除，请假申请
	 * 
	 * */
	public String delete(){
		//1：获取请假单ID
//		Long id = leaveBill.getId();
		Long id = (long) 0;
		//执行删除
		leaveBillService.deleteLeaveBillById(id);
		return "save";
	}
	
}
