package com.webside.activiti.service;

import java.io.InputStream;
import java.util.List;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.web.multipart.MultipartFile;

import com.webside.activiti.model.LeaveBill;
import com.webside.activiti.model.WorkflowBean;
import com.webside.user.model.UserEntity;


public interface WorkflowService {

	void insertNewDeploye(MultipartFile file, String filename);

	List<Deployment> findDeploymentList();

	List<ProcessDefinition> findProcessDefinitionList();

	InputStream findImageInputStream(String deploymentId, String imageName);
	
	void deleteProcessDefinitionByDeploymentId(String deploymentId);

	void saveStartProcess(Long billId,Long userId);

	List<Task> findTaskListByUserId(Long userId);

	String findTaskFormKeyByTaskId(String taskId);

	LeaveBill findLeaveBillByTaskId(String taskId);

	List<String> findOutComeListByTaskId(String taskId);

	List<Comment> findCommentByTaskId(String taskId);

	void saveSubmitTask(WorkflowBean workflowBean,UserEntity userEntity);

}
