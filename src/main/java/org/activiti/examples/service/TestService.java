package org.activiti.examples.service;

import org.activiti.examples.entity.WordsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TestService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public  List<Map<String, Object>> findRUTask(int startIndex,int endIndex){
        String sql = "SELECT ID_,EXECUTION_ID_,PROC_INST_ID_,PARENT_TASK_ID_,NAME_,CREATE_TIME_,ASSIGNEE_,DESCRIPTION_,PROC_DEF_ID_ FROM act_ru_task limit ?,?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, startIndex, endIndex);
        return maps;
    }


    /**
     * 插入 words 表
     */
    public boolean wordsInsertInto(WordsEntity wordsEntity){
        String sql  = "insert into words(id,name,sex,age,createBy,proccessId) values(uuid(),?,?,?,?,?)";
        int update = jdbcTemplate.update(sql, wordsEntity.getName(), wordsEntity.getSex(), wordsEntity.getAge(), wordsEntity.getCreateBy(), wordsEntity.getProccessId());
        return update>0;
    }





}
