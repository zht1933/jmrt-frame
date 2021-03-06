package com.webside.activiti.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.webside.activiti.model.LeaveBill;
import com.webside.activiti.model.WorkflowBean;
import com.webside.activiti.service.LeaveBillService;
import com.webside.activiti.service.WorkflowService;
import com.webside.base.basecontroller.BaseController;
import com.webside.common.Common;
import com.webside.user.model.UserEntity;
import com.webside.user.service.UserService;

@Controller // @Controller 负责注册一个bean 到spring 上下文中，bean 的ID 默认为类名称开头字母小写
@Scope("prototype") // @Scope定义一个Bean 的作用范围,prototype:定义bean可以被多次实例化（使用一次就创建一次）
@RequestMapping("/act/") // @RequestMapping 映射路径，可以声明到类或方法上
public class ActController extends BaseController {

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private UserService userService;

	@Autowired
	private LeaveBillService leaveBillService;

	/**
	 * 流程申请管理
	 * 
	 * @return
	 */
	@RequestMapping("actMain.html")
	public String actMain() {

		return Common.BACKGROUND_PATH + "/workFlow/actMain";

	}

	/**
	 * 流程任务管理
	 * 
	 * @return
	 */
	@RequestMapping("actTask.html")
	public String actTask(HttpServletRequest req, Model model) {

		// 通过session回话获取当前登录用户的信息
		UserEntity userEntity = (UserEntity) req.getSession().getAttribute("defUserEntity");

		// 2：使用当前用户名查询正在执行的任务表，获取当前任务的集合List<Task>
		List<Task> list = workflowService.findTaskListByUserId(userEntity.getId());

		// 将办理人通过ID转换为姓名
		List<Task> taskList = new ArrayList<Task>();
		for (int i = 0; i < list.size(); i++) {
			Task task = list.get(i);
			UserEntity user = userService.findById(Long.parseLong(task.getAssignee()));
			task.setAssignee(user.getUserName());
			taskList.add(task);
		}

		model.addAttribute("list", taskList);

		return Common.BACKGROUND_PATH + "/workFlow/actTask";

	}

	/**
	 * 流程部署管理
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("actDeploy.html")
	public String actDeploy(Model model) {

		// 1:查询部署对象信息，对应表（act_re_deployment）
		List<Deployment> depList = workflowService.findDeploymentList();
		// 2:查询流程定义的信息，对应表（act_re_procdef）
		List<ProcessDefinition> pdList = workflowService.findProcessDefinitionList();
		// 放置到上下文对象中
		model.addAttribute("depList", depList);
		model.addAttribute("pdList", pdList);

		return Common.BACKGROUND_PATH + "/workFlow/actDeploy";

	}

	/**
	 * 发布流程
	 * 
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

	/**
	 * 删除部署信息
	 */
	@RequestMapping(value = "delDeployment.html", method = RequestMethod.POST)
	@ResponseBody
	public Object delDeployment(HttpServletRequest request) {
		// 1：获取部署对象ID
		String deploymentId = request.getParameter("deploymentId").toString();
		// 2：使用部署对象ID，删除流程定义
		workflowService.deleteProcessDefinitionByDeploymentId(deploymentId);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		return result;
	}

	/**
	 * 查看流程图
	 * 
	 * @throws Exception
	 */
	@RequestMapping("viewImage.html")
	public String viewImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 1：获取页面传递的部署对象ID和资源图片名称
		// 部署对象ID
		String deploymentId = request.getParameter("deploymentId").toString();
		// 资源图片名称
		String imageName = request.getParameter("imageName").toString();
		// 2：获取资源文件表（act_ge_bytearray）中资源图片输入流InputStream
		InputStream in = workflowService.findImageInputStream(deploymentId, imageName);
		// 3：从response对象获取输出流
		OutputStream out = response.getOutputStream();
		// 4：将输入流中的数据读取出来，写到输出流中
		for (int b = -1; (b = in.read()) != -1;) {
			out.write(b);
		}
		out.close();
		in.close();
		// 将图写到页面上，用输出流写
		return null;
	}

	/**
	 * 启动流程
	 * 
	 * @throws Exception
	 */
	@RequestMapping("startProcess.html")
	public String startProcess(HttpServletRequest req, Model model) {
		// 更新请假状态，启动流程实例，让启动的流程实例关联业务
		// 1：获取请假单ID
		Long id = null;
		if (req.getParameter("id") != null && !req.getParameter("id").equals(""))
			id = Long.valueOf((String) req.getParameter("id"));

		// 通过session回话获取当前登录用户的信息
		UserEntity userEntity = (UserEntity) req.getSession().getAttribute("defUserEntity");

		workflowService.saveStartProcess(id, userEntity.getId());

		return actTask(req, model);
	}

	/**
	 * 打开任务表单
	 */
	@RequestMapping("viewTaskForm.html")
	public String viewTaskForm(HttpServletRequest req, Model model) {
		// 任务ID
		String taskId = null;
		if (req.getParameter("taskId") != null && !req.getParameter("taskId").equals(""))
			taskId = (String) req.getParameter("taskId");
		// 获取任务表单中任务节点的url连接“FormKey”
		String url = workflowService.findTaskFormKeyByTaskId(taskId);
		url += "?taskId=" + taskId;

		// 根据工作流设计中设定的URL进行controller重定向
		return "redirect:" + url;// 重定向到：/act/audit.html
	}

	// 准备表单数据 /act/audit.html 由工作流设计中form key指定的controller处理方法
	@RequestMapping("audit.html")
	public String audit(HttpServletRequest req, Model model) {
		// 获取任务ID
		String taskId = null;
		if (req.getParameter("taskId") != null && !req.getParameter("taskId").equals(""))
			taskId = (String) req.getParameter("taskId");
		/** 一：使用任务ID，查找请假单ID，从而获取请假单信息 */
		LeaveBill leaveBill = workflowService.findLeaveBillByTaskId(taskId);
		model.addAttribute("leaveBill", leaveBill);
		model.addAttribute("taskId", taskId);
		/**
		 * 二：已知任务ID，查询ProcessDefinitionEntiy对象即.bmpn文件，从而获取当前任务完成之后的连线名称，
		 * 并放置到List <String>集合中
		 */
		List<String> outcomeList = workflowService.findOutComeListByTaskId(taskId);
		model.addAttribute("outcomeList", outcomeList);
		/** 三：查询所有历史审核人的审核信息，帮助当前人完成审核，返回List<Comment> */
		List<Comment> commentList = workflowService.findCommentByTaskId(taskId);
		List<UserEntity> userList = new ArrayList<UserEntity>();

		for (Comment comment : commentList) {
			comment.getUserId();
			UserEntity u = userService.findById(Long.valueOf(comment.getUserId()));
			userList.add(u);
		}

		model.addAttribute("userList", userList);
		model.addAttribute("commentList", commentList);
		return Common.BACKGROUND_PATH + "/workFlow/taskForm";
	}

	/**
	 * 提交任务
	 */
	@RequestMapping(value = "submitTask.html", method = RequestMethod.POST)
	@ResponseBody
	public Object submitTask(HttpServletRequest req, Model model, WorkflowBean workflowBean) {

		// 获取任务ID
		String outcome = null;
		if (req.getParameter("outcome") != null && !req.getParameter("outcome").equals(""))
			outcome = (String) req.getParameter("outcome");
		workflowBean.setOutcome(outcome);

		// 通过session回话获取当前登录用户的信息
		UserEntity userEntity = (UserEntity) req.getSession().getAttribute("defUserEntity");

		workflowService.saveSubmitTask(workflowBean, userEntity);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		return result;
	}

	// 查看历史的批注信息
	@RequestMapping("viewHisComment.html")
	public String viewHisComment(HttpServletRequest req, Model model) {
		// 获取清单ID
		Long id = null;
		if (req.getParameter("id") != null && !req.getParameter("id").equals(""))
			id = Long.valueOf(req.getParameter("id").toString());

		// 1：使用请假单ID，查询请假单对象，将对象放置到栈顶，支持表单回显
		LeaveBill leaveBill = leaveBillService.findLeaveBillById(id);
		model.addAttribute("leaveBill", leaveBill);
		// 2：使用请假单ID，查询历史的批注信息
		List<Comment> commentList = workflowService.findCommentByLeaveBillId(id);
		model.addAttribute("commentList", commentList);

		List<UserEntity> userList = new ArrayList<UserEntity>();
		for (Comment comment : commentList) {
			comment.getUserId();
			UserEntity u = userService.findById(Long.valueOf(comment.getUserId()));
			userList.add(u);
		}

		model.addAttribute("userList", userList);

		return Common.BACKGROUND_PATH + "/workFlow/taskFormHis";
	}

	/**
	 * 查看当前流程图（查看当前活动节点，并使用红色的框标注）
	 */
	@RequestMapping("viewCurrentImage.html")
	public String viewCurrentImage(HttpServletRequest req, Model model) {
		// 任务ID
		String taskId = null;
		if (req.getParameter("taskId") != null && !req.getParameter("taskId").equals(""))
			taskId = (String) req.getParameter("taskId");
		/** 一：查看流程图 */
		// 1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
		ProcessDefinition pd = workflowService.findProcessDefinitionByTaskId(taskId);
		// workflowAction_viewImage?deploymentId=<s:property
		// value='#deploymentId'/>&imageName=<s:property value='#imageName'/>
		model.addAttribute("deploymentId", pd.getDeploymentId());
		model.addAttribute("imageName", pd.getDiagramResourceName());
		/** 二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中 */
		Map<String, Object> map = workflowService.findCoordingByTask(taskId);
		model.addAttribute("acs", map);
		return Common.BACKGROUND_PATH + "/workFlow/image";
	}

	@RequestMapping("viewFlowtImage.html")
	public String viewFlowtImage(HttpServletRequest req, Model model) {
		// 获取清单ID
		Long id = null;
		if (req.getParameter("id") != null && !req.getParameter("id").equals(""))
			id = Long.valueOf(req.getParameter("id").toString());
		// 通过业务ID查询当前任务
		Task task = workflowService.findCurrentTaskByBillId(id);

		// 任务ID
		String taskId = task.getId();
		if (req.getParameter("taskId") != null && !req.getParameter("taskId").equals(""))
			taskId = (String) req.getParameter("taskId");
		/** 一：查看流程图 */
		// 1：获取任务ID，获取任务对象，使用任务对象获取流程定义ID，查询流程定义对象
		ProcessDefinition pd = workflowService.findProcessDefinitionByTaskId(taskId);
		// workflowAction_viewImage?deploymentId=<s:property
		// value='#deploymentId'/>&imageName=<s:property value='#imageName'/>
		model.addAttribute("deploymentId", pd.getDeploymentId());
		model.addAttribute("imageName", pd.getDiagramResourceName());
		/** 二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中 */
		Map<String, Object> map = workflowService.findCoordingByTask(taskId);
		model.addAttribute("acs", map);
		return Common.BACKGROUND_PATH + "/workFlow/image2";
	}
}
