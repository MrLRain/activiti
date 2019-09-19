package org.activiti.examples.rest;

import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.RemoveProcessVariablesPayload;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.examples.SecurityUtil;
import org.activiti.examples.service.TestService;
import org.activiti.examples.util.EmailUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.GeneralSecurityException;
import java.util.*;

/**
 * 护士制药流程
 *
 * @remark 先登录才能使用
 */
@RestController
public class TestController {

    private ProcessRuntime processRuntime;
    private TaskService taskService;
    private SecurityUtil securityUtil;
    private UserDetailsService userDetailsService;
    private TestService testService;

    public TestController(ProcessRuntime processRuntime, TaskService taskService, SecurityUtil securityUtil, @Qualifier(value = "myUserDetailsService") UserDetailsService userDetailsService, TestService testService) {
        this.processRuntime = processRuntime;
        this.taskService = taskService;
        this.securityUtil = securityUtil;
        this.userDetailsService = userDetailsService;
        this.testService = testService;
    }

    /**
     * 首先先展示流程 和 任务，每个 护士制药都属于一个流程，展示各个流程中的任务的状态 为了审批的时候用
     *
     * @param startIndex 开始位置
     * @param endIndex   结束位置
     */
    @GetMapping("/processTaskPage")
    public List<Map<String, Object>> processTaskPage(int startIndex, int endIndex) {
        return testService.findRUTask(startIndex,endIndex);
    }

    /**
     * 启动流程
     */
    @GetMapping("/startUpProcess")
    public void startUpProcess() throws GeneralSecurityException {
         processRuntime.start(ProcessPayloadBuilder
                .start().withProcessDefinitionKey("myProcess_1")
                .withVariable("userId", "root")
                .build());
        EmailUtil.sendMessage("护士可以开始制药了");
    }

    /**
     * 删除流程
     */
    @GetMapping("/removeProcess")
    public void removeProcess(String processId){
        RemoveProcessVariablesPayload removeProcessVariablesPayload = new RemoveProcessVariablesPayload();
        removeProcessVariablesPayload.setProcessInstanceId(processId);
        processRuntime.removeVariables(removeProcessVariablesPayload);
    }

    /**
     * 护士制药
     * @param taskId
     * @param name
     * @throws GeneralSecurityException
     */
    @GetMapping("/sendForm")
    public void sendForm(String taskId,String name) throws GeneralSecurityException {

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        System.out.println("登录用户名 = " + userDetails.getUsername());
        ProcessInstance start = processRuntime.start(ProcessPayloadBuilder
                .start().withProcessDefinitionKey("myProcess_1")
                .withVariable("userId", "root")
                .build());
        String id = start.getId();

        Task task = taskService.createTaskQuery()//创建查询对象
                .processDefinitionId(id)//通过流程实例id来查询当前任务
                .singleResult();//获取单个查询结果

        //填写完之后，给下个审批人赋值
        Map<String, Object> val = new HashMap<>();
        val.put("reson", "制" + name + "药");
        val.put("auditorId", userDetails.getUsername());
        //执行任务
        taskService.complete(task.getId(), val);
        EmailUtil.sendMessage("护士制药了，请护士长审批药物");
        //可以设置成写死的角色id  可给该角色下的所有用户发送邮件 审批   由于 我写的内存验证，就省略了 可以写成查表的
        //String assignee = task.getAssignee();
        //更改任务的状态
    }


    /**
     * 护士长校验
     */
    @GetMapping("/validOne")
        public void validHSZ(String taskId,boolean fool) throws GeneralSecurityException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Task task = taskService.createTaskQuery()//创建查询对象
                .taskId(taskId)//通过流程实例id来查询当前任务
                .singleResult();//获取单个查询结果
        Map<String, Object> val = new HashMap<>();
        val.put("fool", fool);
        val.put("auditorId", userDetails.getUsername());
        if(fool){
            EmailUtil.sendMessage("护士长审批同意，请医生审批药物");
            val.put("reson","护士长"+userDetails.getUsername()+"审批成功");
        }else{
            EmailUtil.sendMessage("护士长审批不同意，请护士检查一下所配置药物");
            val.put("reson","护士长"+userDetails.getUsername()+"审批失败");
        }
        taskService.complete(task.getId(), val);
    }

    /**
     * 医生审批
     */
    @GetMapping("/validTwo")
    public void validHSZTwo(String taskId,boolean fool) throws GeneralSecurityException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Task task = taskService.createTaskQuery()//创建查询对象
                .taskId(taskId)//通过流程实例id来查询当前任务
                .singleResult();//获取单个查询结果
        Map<String, Object> val = new HashMap<>();
        val.put("bool", fool);
        val.put("auditorId", userDetails.getUsername());
        if(fool){
            EmailUtil.sendMessage("医生审批同意，请科长审批药物");
            val.put("reson","医生"+userDetails.getUsername()+"审批成功");
        }else{
            EmailUtil.sendMessage("医生审批不同意，请护士检查一下所配置药物");
            val.put("reson","医生"+userDetails.getUsername()+"审批失败");
        }
        taskService.complete(task.getId(), val);
    }


    /**
     * 科长审批
     */
    @GetMapping("/valid")
    public void valid(String taskId,boolean fool) throws GeneralSecurityException {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        Task task = taskService.createTaskQuery()//创建查询对象
                .taskId(taskId)//通过流程实例id来查询当前任务
                .singleResult();//获取单个查询结果
        Map<String, Object> val = new HashMap<>();
        val.put("zool", fool);
        val.put("auditorId", userDetails.getUsername());
        if(fool){
            EmailUtil.sendMessage("药物审批同意，请护士给患者实施");
            val.put("reson","科长"+userDetails.getUsername()+"审批成功");
        }else{
            EmailUtil.sendMessage("科长审批不同意，请护士检查一下所配置药物");
            val.put("reson","科长"+userDetails.getUsername()+"审批失败");
        }
        taskService.complete(task.getId(), val);
    }

}
