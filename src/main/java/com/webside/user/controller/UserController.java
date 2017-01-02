package com.webside.user.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.webside.base.basecontroller.BaseController;
import com.webside.common.Common;
import com.webside.dtgrid.model.Pager;
import com.webside.dtgrid.util.ExportUtils;
import com.webside.exception.AjaxException;
import com.webside.exception.ServiceException;
import com.webside.role.model.RoleEntity;
import com.webside.role.service.RoleService;
import com.webside.shiro.ShiroAuthenticationManager;
import com.webside.user.model.UserEntity;
import com.webside.user.model.UserInfoEntity;
import com.webside.user.service.UserService;
import com.webside.util.EndecryptUtils;
import com.webside.util.PageUtil;
import com.webside.util.RandomUtil;

@Controller//@Controller 负责注册一个bean 到spring 上下文中，bean 的ID 默认为类名称开头字母小写 
@Scope("prototype")//@Scope定义一个Bean 的作用范围,prototype:定义bean可以被多次实例化（使用一次就创建一次）
@RequestMapping("/user/")//@RequestMapping 映射路径，可以声明到类或方法上
public class UserController extends BaseController {

	@Autowired//@Autowired 根据bean 类型从spring 上线文中进行查找，注册类型必须唯一，否则报异常。
	private UserService userService;
	
	@Autowired
	private RoleService roleService;
	
	@RequestMapping("listUI.html")
	public String listUI(Model model, HttpServletRequest request) {
		try
		{
			PageUtil page = new PageUtil();
			if(request.getParameterMap().containsKey("page")){
				page.setPageNum(Integer.valueOf(request.getParameter("page")));
				page.setPageSize(Integer.valueOf(request.getParameter("rows")));
				page.setOrderByColumn(request.getParameter("sidx"));
				page.setOrderByType(request.getParameter("sord"));
			}
			model.addAttribute("page", page);
			return Common.BACKGROUND_PATH + "/user/list";
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
	}
	
	
	/**
	 * ajax分页动态加载模式
	 * @param dtGridPager Pager对象
	 * @throws Exception
	 */
	@RequestMapping(value = "/list.html", method = RequestMethod.POST)
	@ResponseBody//将Controller的方法返回的对象，通过适当的HttpMessageConverter转换为指定格式后，写入到Response对象的body数据区。返回的数据不是html标签的页面，而是其他某种格式的数据时（如json、xml等）使用；
	public Object list(String gridPager, HttpServletResponse response) throws Exception{
		Map<String, Object> parameters = null;
		//1、映射Pager对象
		Pager pager = JSON.parseObject(gridPager, Pager.class);
		//2、设置查询参数
		parameters = pager.getParameters();
		parameters.put("creatorName", ShiroAuthenticationManager.getUserAccountName());
		if (parameters.size() < 0) {
			parameters.put("userName", null);
		}
		//3、判断是否是导出操作
		if(pager.getIsExport())
		{
			if(pager.getExportAllData())
			{
				//3.1、导出全部数据
				List<UserEntity> list = userService.queryListByPage(parameters);
				ExportUtils.exportAll(response, pager, list);
				return null;
			}else
			{
				//3.2、导出当前页数据
				ExportUtils.export(response, pager);
				return null;
			}
		}else
		{
			//设置分页，page里面包含了分页信息
			Page<Object> page = PageHelper.startPage(pager.getNowPage(),pager.getPageSize(), "u_id DESC");
			List<UserEntity> list = userService.queryListByPage(parameters);
			parameters.clear();
			parameters.put("isSuccess", Boolean.TRUE);
			parameters.put("nowPage", pager.getNowPage());
			parameters.put("pageSize", pager.getPageSize());
			parameters.put("pageCount", page.getPages());
			parameters.put("recordCount", page.getTotal());
			parameters.put("startRecord", page.getStartRow());
			//列表展示数据
			parameters.put("exhibitDatas", list);
			return parameters;
		}
	}
	
	
	
	@RequestMapping("addUI.html")
	public String addUI(Model model) {
		try
		{
			List<RoleEntity> list = roleService.queryListByPage(new HashMap<String, Object>());
			model.addAttribute("roleList", list);
			return Common.BACKGROUND_PATH + "/user/form";
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		
	}
	
	@RequestMapping("add.html")
	@ResponseBody
	public Object add(UserEntity userEntity) throws AjaxException
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			String password = userEntity.getPassword();
			// 加密用户输入的密码，得到密码和加密盐，保存到数据库
			UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), userEntity.getPassword(), 2);
			//设置添加用户的密码和加密盐
			userEntity.setPassword(user.getPassword());
			userEntity.setCredentialsSalt(user.getCredentialsSalt());
			//设置创建者姓名
			userEntity.setCreatorName(ShiroAuthenticationManager.getUserAccountName());
			userEntity.setCreateTime(new Date(System.currentTimeMillis()));
			//设置锁定状态：未锁定；删除状态：未删除
			userEntity.setLocked(0);
			userEntity.setDeleteStatus(0);
			UserInfoEntity userInfo = new UserInfoEntity();
			userEntity.setUserInfo(userInfo);
			int result = userService.insert(userEntity, password);
			if(result == 1)
			{
				map.put("success", Boolean.TRUE);
				map.put("data", null);
				map.put("message", "添加成功");
			}else
			{
				map.put("success", Boolean.FALSE);
				map.put("data", null);
				map.put("message", "添加失败");
			}
		}catch(ServiceException e)
		{
			throw new AjaxException(e);
		}
		return map;
	}
	
	
	@RequestMapping("editUI.html")
	public String editUI(Model model, HttpServletRequest request, Long id) {
		try
		{
			UserEntity userEntity = userService.findById(id);
			PageUtil page = new PageUtil();
			page.setPageNum(Integer.valueOf(request.getParameter("page")));
			page.setPageSize(Integer.valueOf(request.getParameter("rows")));
			page.setOrderByColumn(request.getParameter("sidx"));
			page.setOrderByType(request.getParameter("sord"));
			
			List<RoleEntity> list = roleService.queryListByPage(new HashMap<String, Object>());
			
			model.addAttribute("page", page);
			model.addAttribute("userEntity", userEntity);
			model.addAttribute("roleList", list);
			return Common.BACKGROUND_PATH + "/user/form";
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
	}
	
	@RequestMapping("edit.html")
	@ResponseBody
	public Object update(UserEntity userEntity) throws AjaxException
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			//设置创建者姓名
			userEntity.setCreatorName(ShiroAuthenticationManager.getUserAccountName());
			int result = userService.update(userEntity);
			if(result == 1)
			{
				map.put("success", Boolean.TRUE);
				map.put("data", null);
				map.put("message", "编辑成功");
			}else
			{
				map.put("success", Boolean.FALSE);
				map.put("data", null);
				map.put("message", "编辑失败");
			}
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return map;
	}
	
	
	@RequestMapping("deleteBatch.html")
	@ResponseBody
	public Object deleteBatch(String ids){
		Map<String, Object> result = new HashMap<String, Object>();
		try
		{
			String[] userIds = ids.split(",");
			List<Long> list = new ArrayList<Long>();
			for (String string : userIds) {
				list.add(Long.valueOf(string));
			}
			int cnt = userService.deleteBatchById(list);
			if(cnt == list.size())
			{
				result.put("success", true);
				result.put("data", null);
				result.put("message", "删除成功");
			}else
			{
				result.put("success", false);
				result.put("data", null);
				result.put("message", "删除失败");
			}
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return result;
	}
	
	@RequestMapping("resetPassword.html")
	@ResponseBody
	public Object resetPassword(UserEntity userEntity){
		Map<String, Object> result = new HashMap<String, Object>();
		try
		{
			//生成随机密码
			String password = RandomUtil.generateString(6);
			
			//加密用户输入的密码，得到密码和加密盐，保存到数据库
			UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), password, 2);
			//设置添加用户的密码和加密盐
			userEntity.setPassword(user.getPassword());
			userEntity.setCredentialsSalt(user.getCredentialsSalt());
			if(userEntity.getId() == null)
			{
				user = null;
				user = userService.findByName(userEntity.getAccountName());
				if(user != null)
				{
					userEntity.setId(user.getId());
					userEntity.setUserName(user.getUserName());
					int cnt = userService.updateOnly(userEntity, password);
					if(cnt > 0)
					{
						result.put("success", true);
						result.put("data", null);
						result.put("message", "密码重置成功");
					}else
					{
						result.put("success", false);
						result.put("data", null);
						result.put("message", "密码重置失败");
					}
				}else
				{
					result.put("success", false);
					result.put("data", null);
					result.put("message", "账户不存在");
				}
			}else
			{
				int cnt = userService.updateOnly(userEntity, password);
				if(cnt > 0)
				{
					result.put("success", true);
					result.put("data", null);
					result.put("message", "密码重置成功");
				}else
				{
					result.put("success", false);
					result.put("data", null);
					result.put("message", "密码重置失败");
				}
			}
			
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return result;
	}
	
	@RequestMapping("withoutAuth/resetPassWithoutAuthc.html")
	@ResponseBody
	public Object resetPassWithoutAuthc(UserEntity userEntity){
		Map<String, Object> result = new HashMap<String, Object>();
		try
		{
			//生成随机密码
			String password = RandomUtil.generateString(6);
			//加密用户输入的密码，得到密码和加密盐，保存到数据库
			UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), password, 2);
			//设置添加用户的密码和加密盐
			userEntity.setPassword(user.getPassword());
			userEntity.setCredentialsSalt(user.getCredentialsSalt());
			
			user = null;
			user = userService.findByName(userEntity.getAccountName());
			if(user != null)
			{
				userEntity.setId(user.getId());
				userEntity.setUserName(user.getUserName());
				int cnt = userService.updateOnly(userEntity, password);
				if(cnt > 0)
				{
					result.put("success", true);
					result.put("data", null);
					result.put("message", "密码重置成功");
				}else
				{
					result.put("success", false);
					result.put("data", null);
					result.put("message", "密码重置失败");
				}
			}else
			{
				result.put("success", false);
				result.put("data", null);
				result.put("message", "账户不存在");
			}
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return result;
	}
	
	
	@RequestMapping("infoUI.html")
	public String infoUI(Model model, Long id) {
		try
		{
			UserEntity userEntity = userService.findById(id);
			model.addAttribute("userEntity", userEntity);
			return Common.BACKGROUND_PATH + "/user/info";
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
	}
	
	@RequestMapping("info.html")
	@ResponseBody
	public Object info(UserEntity userEntity)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			int result = userService.update(userEntity);
			if(result == 1)
			{
				map.put("success", Boolean.TRUE);
				map.put("data", null);
				map.put("message", "编辑成功");
			}else
			{
				map.put("success", Boolean.FALSE);
				map.put("data", null);
				map.put("message", "编辑失败");
			}
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return map;
	}
	
	
	@RequestMapping("passwordUI.html")
	public String passwordUI(Model model, UserEntity userEntity) {
		try
		{
			model.addAttribute("userEntity", userEntity);
			return Common.BACKGROUND_PATH + "/user/password";
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
	}
	
	
	@RequestMapping("password.html")
	@ResponseBody
	public Object password(UserEntity userEntity){
		Map<String, Object> result = new HashMap<String, Object>();
		try
		{
			String password = userEntity.getPassword();
			userEntity.setUserName(new String(userEntity.getUserName().getBytes("iso-8859-1"),"utf-8"));
			//加密用户输入的密码，得到密码和加密盐，保存到数据库
			UserEntity user = EndecryptUtils.md5Password(userEntity.getAccountName(), userEntity.getPassword(), 2);
			//设置添加用户的密码和加密盐
			userEntity.setPassword(user.getPassword());
			userEntity.setCredentialsSalt(user.getCredentialsSalt());
			int cnt = userService.updateOnly(userEntity, password);
			if(cnt > 0)
			{
				result.put("success", true);
				result.put("data", null);
				result.put("message", "密码修改成功");
			}else
			{
				result.put("success", false);
				result.put("data", null);
				result.put("message", "密码修改失败");
			}
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
		return result;
	}
	
	
	@RequestMapping("withoutAuth/validateAccountName.html")
	@ResponseBody
	public Object validateAccount(String accountName){
		try
		{
			UserEntity userEntity = userService.findByName(accountName);
			if(userEntity == null)
			{
				return true;
			}else
			{
				return false;
			}
		}catch(Exception e)
		{
			throw new AjaxException(e);
		}
	}
	
	/**
	 * ECharts跳转页面
	 * @param model
	 * @return
	 */
	@RequestMapping("echarts.html")
	public String echartsUI(Model model) {
			return "/echarts";
	}
	
	/**
	 * Ajax 动态加载数据
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "getEchars.html", method = RequestMethod.POST)
	@ResponseBody
	public Object echartsData(HttpServletResponse response) {
			List<UserEntity> data = userService.queryAllUser();
			List<String> name = new ArrayList<String>();
			List<String> count = new ArrayList<String>();
			
			for (UserEntity u : data) {
				name.add(u.getUserName());
				count.add(u.getId().toString());
			}
			
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			map.put("name", name);
			map.put("count", count);
			
			return map;
	}
	
	/**
	 * EXTJS跳转页面
	 * @param model
	 * @return
	 */
	@RequestMapping("extjs.html")
	public String extjsUI(Model model) {
			return "extjs";
	}
	
	/**
	 * 文件管理器跳转页面
	 * @param model
	 * @return
	 */
	@RequestMapping("filemanger.html")
	public String elFinderUI(Model model) {
			return "fileManger";
	}
}
