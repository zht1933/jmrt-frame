package com.webside.activiti.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.activiti.engine.impl.identity.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.lang.StringUtils;

import com.webside.activiti.model.LeaveBill;
import com.webside.activiti.model.WorkflowBean;
import com.webside.activiti.service.LeaveBillService;
import com.webside.activiti.service.WorkflowService;
import com.webside.base.baseservice.impl.AbstractService;
import com.webside.user.model.UserEntity;

@Service("workflowService") // @Service 向spring 上下文中注册服务bean
public class WorkflowServiceImpl extends AbstractService<WorkflowBean, Long> implements WorkflowService {

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private LeaveBillService leaveBillService;

	/** 部署流程定义 */
	@Override
	public void insertNewDeploye(MultipartFile file, String filename) {
		try {
			// 2：将File类型的文件转化成ZipInputStream流
			ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream());
			repositoryService.createDeployment()// 创建部署对象
					.name(filename)// 添加部署名称
					.addZipInputStream(zipInputStream)//
					.deploy();// 完成部署
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 查询部署对象信息，对应表（act_re_deployment） */
	@Override
	public List<Deployment> findDeploymentList() {
		List<Deployment> list = repositoryService.createDeploymentQuery()// 创建部署对象查询
				.orderByDeploymenTime().desc()//
				.list();
		return list;
	}

	/** 查询流程定义的信息，对应表（act_re_procdef） */
	@Override
	public List<ProcessDefinition> findProcessDefinitionList() {
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()// 创建流程定义查询
				.orderByProcessDefinitionVersion().desc()//
				.list();
		return list;
	}

	/** 使用部署对象ID和资源图片名称，获取图片的输入流 */
	@Override
	public InputStream findImageInputStream(String deploymentId, String imageName) {
		return repositoryService.getResourceAsStream(deploymentId, imageName);
	}

	/** 使用部署对象ID，删除流程定义 */
	@Override
	public void deleteProcessDefinitionByDeploymentId(String deploymentId) {
		repositoryService.deleteDeployment(deploymentId, true);
	}

	@Override
	public void saveStartProcess(Long billId, Long userId) {

		// 1：获取请假单ID，使用请假单ID，查询请假单的对象LeaveBill
		Long id = billId;
		LeaveBill leaveBill = leaveBillService.findLeaveBillById(id);
		// 2：更新请假单的请假状态从0变成1（初始录入-->审核中）
		leaveBill.setState(1);
		leaveBillService.insertLeaveBill(leaveBill, userId);
		// 3：使用当前对象获取到流程定义的key（对象的名称就是流程定义的key）
		String key = leaveBill.getClass().getSimpleName();
		/**
		 * 4：从Session中获取当前任务的办理人，使用流程变量设置下一个任务的办理人 inputUser是流程变量的名称，
		 * 获取的办理人是流程变量的值
		 */
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("inputUser", userId);// 表示惟一用户
		/**
		 * 5： (1)使用流程变量设置字符串（格式：LeaveBill.id的形式），通过设置，让启动的流程（流程实例）关联业务
		 * (2)使用正在执行对象表中的一个字段BUSINESS_KEY（Activiti提供的一个字段），让启动的流程（流程实例）关联业务
		 */
		// 格式：LeaveBill.id的形式（使用流程变量），业务类的名称.业务ID
		String objId = key + "." + id;
		variables.put("objId", objId);
		// 6：使用流程定义的key，启动流程实例，同时设置流程变量，同时向正在执行的执行对象表中的字段BUSINESS_KEY添加业务数据，同时让流程关联业务
		runtimeService.startProcessInstanceByKey(key, objId, variables);

	}

	@Override
	public List<Task> findTaskListByUserId(Long userId) {
		List<Task> list = taskService.createTaskQuery()//
				.taskAssignee(userId.toString())// 指定个人任务查询
				.orderByTaskCreateTime().desc()//
				.list();
		return list;
	}

	@Override
	public String findTaskFormKeyByTaskId(String taskId) {
		TaskFormData formData = formService.getTaskFormData(taskId);
		// 获取Form key的值
		String url = formData.getFormKey();
		return url;
	}

	/** 一：使用任务ID，查找请假单ID，从而获取请假单信息 */
	@Override
	public LeaveBill findLeaveBillByTaskId(String taskId) {
		// 1：使用任务ID，查询任务对象Task
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 2：使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 3：使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 4：使用流程实例对象获取BUSINESS_KEY
		String buniness_key = pi.getBusinessKey();
		// 5：获取BUSINESS_KEY对应的主键ID，使用主键ID，查询请假单对象（LeaveBill.1）
		String id = "";
		if (StringUtils.isNotBlank(buniness_key)) {
			// 截取字符串，取buniness_key小数点的第2个值
			id = buniness_key.split("\\.")[1];
		}
		// 查询请假单对象
		LeaveBill leaveBill = leaveBillService.findLeaveBillById(Long.parseLong(id));
		return leaveBill;
	}

	/**
	 * 二：已知任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中
	 */
	@Override
	public List<String> findOutComeListByTaskId(String taskId) {
		// 返回存放连线的名称集合
		List<String> list = new ArrayList<String>();
		// 1:使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 2：获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 3：查询ProcessDefinitionEntiy对象
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);
		// 使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 获取当前活动的id
		String activityId = pi.getActivityId();
		// 4：获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		// 5：获取当前活动完成之后连线的名称
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		if (pvmList != null && pvmList.size() > 0) {
			for (PvmTransition pvm : pvmList) {
				String name = (String) pvm.getProperty("name");
				if (StringUtils.isNotBlank(name)) {
					list.add(name);
				} else {
					list.add("默认提交");
				}
			}
		}
		return list;
	}

	/** 获取批注信息，传递的是当前任务ID，获取历史任务ID对应的批注 */
	@Override
	public List<Comment> findCommentByTaskId(String taskId) {
		List<Comment> list = new ArrayList<Comment>();
		// 使用当前的任务ID，查询当前流程对应的历史任务ID
		// 使用当前任务ID，获取当前任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();

		list = taskService.getProcessInstanceComments(processInstanceId);
		return list;
	}

	/** 指定连线的名称完成任务 */
	@Override
	public void saveSubmitTask(WorkflowBean workflowBean, UserEntity userEntity) {
		// 获取任务ID
		String taskId = workflowBean.getTaskId();
		// 获取连线的名称
		String outcome = workflowBean.getOutcome();
		// 批注信息
		String message = workflowBean.getComment();
		// 获取请假单ID
		Long id = workflowBean.getId();

		/**
		 * 1：在完成之前，添加一个批注信息，向act_hi_comment表中添加数据，用于记录对当前申请人的一些审核信息
		 */
		// 使用任务ID，查询任务对象，获取流程流程实例ID
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		/**
		 * 注意：添加批注的时候，由于Activiti底层代码是使用： String userId =
		 * Authentication.getAuthenticatedUserId(); CommentEntity comment = new
		 * CommentEntity(); comment.setUserId(userId);
		 * 所有需要从Session中获取当前登录人，作为该任务的办理人（审核人），对应act_hi_comment表中的User_ID的字段，
		 * 不过不添加审核人，该字段为null
		 * 所以要求，添加配置执行使用Authentication.setAuthenticatedUserId();添加当前任务的审核人
		 */
		Authentication.setAuthenticatedUserId(userEntity.getId().toString());
		taskService.addComment(taskId, processInstanceId, message);
		/**
		 * 2：如果连线的名称是“默认提交”，那么就不需要设置，如果不是，就需要设置流程变量 在完成任务之前，设置流程变量，按照连线的名称，去完成任务
		 * 流程变量的名称：outcome 流程变量的值：连线的名称
		 */
		Map<String, Object> variables = new HashMap<String, Object>();
		if (outcome != null && !outcome.equals("默认提交")) {
			variables.put("outcome", outcome);
		}

		// 3：使用任务ID，完成当前人的个人任务，同时流程变量
		taskService.complete(taskId, variables);
		// 4：当任务完成之后，需要指定下一个任务的办理人（使用类）-----已经开发完成
		// 监听类：ManagerTaskHandler.java实现

		/**
		 * 5：在完成任务之后，判断流程是否结束 如果流程结束了，更新请假单表的状态从1变成2（审核中-->审核完成）
		 */
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 流程结束了
		if (pi == null) {
			// 更新请假单表的状态从1变成2（审核中-->审核完成）
			LeaveBill leaveBill = leaveBillService.findLeaveBillById(id);
			leaveBill.setState(2);
			leaveBillService.insertLeaveBill(leaveBill, userEntity.getId());
		}

	}

	/**使用请假单ID，查询历史批注信息*/
	@Override
	public List<Comment> findCommentByLeaveBillId(Long id) {
		//使用请假单ID，查询请假单对象
		LeaveBill leaveBill = leaveBillService.findLeaveBillById(id);
		//获取对象的名称
		String objectName = leaveBill.getClass().getSimpleName();
		//组织流程表中的字段中的值
		String objId = objectName+"."+id;
		
		/**1:使用历史的流程实例查询，返回历史的流程实例对象，获取流程实例ID*/
//		HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()//对应历史的流程实例表
//						.processInstanceBusinessKey(objId)//使用BusinessKey字段查询
//						.singleResult();
//		//流程实例ID
//		String processInstanceId = hpi.getId();
		/**2:使用历史的流程变量查询，返回历史的流程变量的对象，获取流程实例ID*/
		HistoricVariableInstance hvi = historyService.createHistoricVariableInstanceQuery()//对应历史的流程变量表
						.variableValueEquals("objId", objId)//使用流程变量的名称和流程变量的值查询
						.singleResult();
		//流程实例ID
		String processInstanceId = hvi.getProcessInstanceId();
		List<Comment> list = taskService.getProcessInstanceComments(processInstanceId);
		return list;
	}
	
}
