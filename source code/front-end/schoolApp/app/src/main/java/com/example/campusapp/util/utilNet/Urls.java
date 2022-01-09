package com.example.campusapp.util.utilNet;

public class Urls {


    public static final String BASE= "http://120.53.118.249:8080";
    public static final String BASE_IMG= "http://120.53.118.249:8080/getHeaderImage";
    /**
     * 登录
     */
    public static final String Login=BASE+"/login/word";

    /**
     * 获取邮箱验证码
     */
    public static final String Code=BASE+"/code";

    /**
     * 注册
     */
    public static final String Register=BASE+"/register";

    /**
     * 更换头像
     */
    public static final String ChangeHeadPortrait=BASE+"/upload";

    /**
     * 获取用户头像(暂时用不到)
     */
    public static final String getImgCOOL=BASE+"/user/image";

    /**
     * 修改密码
     */
    public static final String ChangePassword=BASE+"/user/password";

    /**
     * 添加课程
     */
    public static final String AddSchedule=BASE+"/schedule/AddSchedule";

    /**
     * 获取课程
     */
    public static final String GetScheduleByUserId=BASE+"/schedule/GetScheduleByUserId";
    /**
     * 删除课程
     */
    public static final String DeleteScheduleById=BASE+"/schedule/DeleteScheduleById";
    /**
     * 添加图片
     */
    public static final String AddImage=BASE+"/blog/picture";

    /**
     * 添加信息
     */
    public static final String AddMessage=BASE+"/blog/update";


    /**
     * 获取信息
     */
    public static final String GetMessageAll=BASE+"/blog/all";


    /**
     *  按tag查询信息
     */
    public static final String GetMessageTagSearch=BASE+"/blog/tag";

    /**
     *  按title查询信息
     */
    public static final String GetMessageTitleSearch=BASE+"/blog/title";


    /**
     * 添加评论
     */
    public static final String AddComment=BASE+"/comment";

    /**
     * 删除评论
     */
    public static final String DeleteCommentById=BASE+"/DeleteCommentById";

    /**
     * 删除信息
     */
    public static final String DeleteMessageById=BASE+"/DeleteMessageById";

    /**
     * 获取我的评论
     */
    public static final String GetMessageByCommentUserId=BASE+"/comment/id";

    /**
     * 获取我发布的信息
     */
    public static final String GetMessageByUserId=BASE+"/blog/userid";

    /**
     * 获取公告
     */
    public static final String GetNotification=BASE+"/notification/all";


    public static final String GetFriends = BASE + "/user/all";

}

