package org.activiti.examples.rest;

import org.activiti.api.process.model.ProcessDefinition;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.model.builders.StartProcessPayloadBuilder;
import org.activiti.api.process.model.payloads.StartProcessPayload;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.runtime.shared.query.Page;
import org.activiti.api.runtime.shared.query.Pageable;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.examples.SecurityUtil;
import org.activiti.examples.start.StartListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/")
public class LoginController {
    private Logger logger = LoggerFactory.getLogger(StartListener.class);

    private SecurityUtil securityUtil;
    private ProcessRuntime processRuntime;
    private TaskService taskService;

    public LoginController(SecurityUtil securityUtil, ProcessRuntime processRuntime, TaskService taskService) {
        this.securityUtil = securityUtil;
        this.processRuntime = processRuntime;
        this.taskService = taskService;
    }
    //目前 三个问题，指向，分页，嵌入更新
    /**
     * 获取分页信息
     */
    @GetMapping("")
    public Page<ProcessDefinition> login(){
        try {
            Pageable  pageable = Pageable.of(0, 10);
            Page<ProcessDefinition> processInstancePage = processRuntime.processDefinitions(pageable);
            System.out.println("processInstancePage = " + processInstancePage);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 插入启动子流程
     */
    @GetMapping("/insertProcess")
    public void insertProcess(String key){
        try {
//            String taskId, String processInstance, String message
            ProcessInstance myProcess_1 = processRuntime.start(ProcessPayloadBuilder
                    .start().withProcessDefinitionKey("myProcess_1")
                    .withVariable("userId","root")
                    .build());


            completeEmployeeTask(myProcess_1.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //提交申请
    public void completeEmployeeTask(String id){
        boolean fool=false;
        try {
            fool = processRuntime.processInstance(id) != null;
        } catch (org.activiti.api.runtime.shared.NotFoundException e) {
            logger.debug("流程已执行完");
            fool=false;
        }
            while(fool){
                ProcessInstance myProcess=null;
                try {
                    myProcess = processRuntime.processInstance(id);
                } catch (org.activiti.api.runtime.shared.NotFoundException e) {
                    logger.debug("流程已执行完");
                    return;
                }
                Task task = taskService.createTaskQuery()//创建查询对象
                        .processInstanceId(myProcess.getId())//通过流程实例id来查询当前任务
                        .singleResult();//获取单个查询结果
                //获取任务id
                String taskId = task.getId();
                String taskName = task.getName();
                if(taskName.equals("_3")){
                    //填写完之后，给下个审批人赋值
                    Map<String,Object> val=new HashMap<>();
                    val.put("reson","请假去探亲");
                    val.put("auditorId","TOM");
                    taskService.complete(taskId,val);
                    System.out.println(task.getAssignee()+"制药开始");
                }
                if(taskName.equals("审批")){
                    //进行审批
                    completeLeaderTask(taskId);

                    System.out.println(task.getAssignee()+"已经审批......."+myProcess.getStatus());
                }
            }



    }

    //领导审批
    public void completeLeaderTask(String taskId){
        //请假审批意见
        Map<String, Object> variables = new HashMap<String, Object>();
        //variables.put("day",4);
        variables.put("result", "同意");
        variables.put("fool", true);
        //完成任务
        taskService.complete(taskId, variables);
        System.out.println("领导审核完毕........");
    }
    private void processText() {

        securityUtil.logInAs("system");

        String content = pickRandomString();

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy HH:mm:ss");

        logger.info("> Processing content: " + content + " at " + formatter.format(new Date()));
        StartProcessPayloadBuilder start = ProcessPayloadBuilder
                .start();
        StartProcessPayload build = start
                .withProcessDefinitionKey("categorizeProcess")
                .withProcessInstanceName("Processing Content: " + content)
                .withVariable("content", content)
                .build();
        ProcessInstance processInstance = processRuntime.start(build);

        String name = processInstance.getStatus().name();
        System.out.println("name = " + name);
        logger.info(">>> Created Process Instance: " + processInstance);
    }

    private String pickRandomString() {
        String[] texts = {"hello from london", "Hi there from activiti!", "all good news over here.", "I've tweeted about activiti today.",
                "other boring projects.", "activiti cloud - Cloud Native Java BPM"};
        return texts[new Random().nextInt(texts.length)];
    }

}
