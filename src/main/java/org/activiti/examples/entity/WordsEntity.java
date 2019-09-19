package org.activiti.examples.entity;

/**
 * 业务实体类
 */
public class WordsEntity {
    /**
     * id 序号
     */
    private String id;
    /**
     * name 姓名
     */
    private String name;
    /**
     * sex 性别
     */
    private String sex;
    /**
     * age 年龄
     */
    private int age;
    /**
     *创建人
     */
    private String createBy;
    /**
     * 流程id
     */
    private  String proccessId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getProccessId() {
        return proccessId;
    }

    public void setProccessId(String proccessId) {
        this.proccessId = proccessId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }
}
