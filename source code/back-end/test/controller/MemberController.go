package controller

import (
	"encoding/json"
	"log"
	"net/http"
	"strconv"
	"test/dao"
	"test/model"
	"test/parameter"
	"test/service"
	"test/tool"

	"github.com/gin-gonic/gin"
	"github.com/gin-gonic/gin/binding"
)

// GetSendCodeJSON 解析发送验证码JSON
func GetSendCodeJSON(context *gin.Context) {
	var applyCode = parameter.ApplyCodeRequest{}
	err := context.ShouldBindWith(&applyCode, binding.JSON)
	log.Println(applyCode)
	if err != nil {
		context.JSON(http.StatusOK, parameter.ApplyCodeResponse{
			Code:    tool.CODE_FAILED,
		})
		context.Abort()
	} else {
		context.Set("JSON", applyCode)
	}
}

// SendCode 发送验证码
func SendCode(context *gin.Context) {
	applyCode_, _ := context.Get("JSON")
	applyCode := applyCode_.(parameter.ApplyCodeRequest)
	EmailService := service.UserService{}
	hasSent, _ := EmailService.SendCode(applyCode.Email)//返回值：发送成功标志和验证码
	if hasSent {
		context.Set("RETURN", parameter.ApplyCodeResponse{
			Code:    tool.CODE_SUCCESS,

		})
		return
	}
	context.Set("RETURN", parameter.ApplyCodeResponse{
		Code:    tool.CODE_FAILED,

	})
}

// GetLoginByEmailJSON 解析邮箱登录JSON
func GetLoginByEmailJSON(context *gin.Context) {
	var loginByEmailParameter = parameter.LoginByEmailRequest{}
	err := context.ShouldBindWith(&loginByEmailParameter, binding.JSON)
	log.Println(loginByEmailParameter)
	if err != nil {
		context.Set("RETURN", parameter.LoginResponse{
			Code:    tool.FAILED,
			Data:    parameter.Data{},
		})
		context.Abort()
	} else {
		context.Set("JSON", loginByEmailParameter)
	}
}

// LoginByEmail  用邮箱验证码登录
func LoginByEmail(context *gin.Context) {
	loginByEmailParameter_, _ := context.Get("JSON")
	loginByEmailParameter := loginByEmailParameter_.(parameter.LoginByEmailRequest)

	emailService := service.UserService{}
	user := emailService.LoginByEmail(loginByEmailParameter)
	if user != nil {
		// 将用户信息保存到session
		session, _ := json.Marshal(user)
		err := tool.SetSession(context, "user_"+strconv.Itoa(user.Id), session)
		if err != nil {
			context.Set("RETURN", parameter.LoginResponse{
				Code:    tool.FAILED,
				Data:    parameter.Data{},
			})
			log.Fatal(err.Error())
			return
		}
		context.Set("RETURN", parameter.LoginResponse{
			Code:    tool.SUCCESS,
			Data:    parameter.Data{},
		})

		return
	}
	context.Set("RETURN", parameter.LoginResponse{
		Code:    tool.FAILED,
		Data:    parameter.Data{},
	})



}

// GetLoginByPasswordJSON 解析密码登录JSON
func GetLoginByPasswordJSON(context *gin.Context) {
	var loginByPasswordParameter = parameter.LoginByPasswordRequest{}
	err := context.ShouldBindWith(&loginByPasswordParameter, binding.JSON)
	log.Println(loginByPasswordParameter)
	if err != nil {
		context.Set("RETURN", parameter.LoginResponse{
			Code:    tool.FAILED,
			Data:    parameter.Data{},
		})
		context.Abort()
	} else {
		context.Set("JSON", loginByPasswordParameter)
	}
}

// LoginByPassword 用密码登录
func LoginByPassword(context *gin.Context) {
	loginByPasswordParameter_, _ := context.Get("JSON")
	loginByPasswordParameter := loginByPasswordParameter_.(parameter.LoginByPasswordRequest)

	userService := service.UserService{}
	user := userService.LoginByPassword(loginByPasswordParameter)
	if user != nil {
		// 将用户信息保存到session
		session, _ := json.Marshal(user)
		err := tool.SetSession(context, "user_"+strconv.Itoa(user.Id), session)
		if err != nil {
			context.Set("RETURN", parameter.LoginResponse{
				Code:    tool.SIGN_IN_FAILED,
			})
			log.Fatal(err.Error())
			return
		}
		context.Set("RETURN", parameter.LoginResponse{
			Code:    tool.SIGN_IN_SUCCESS,
			Data:    parameter.Data{User: parameter.User{
				Name:     user.Name,
				Password: user.Password,
				Email:    user.Email,
				Id:       user.Id,
				Type:     user.Type,
				RealName: user.RealName,
				Sex:      user.Sex,
				Avatar: user.Avatar,

			},
			},
		})
		return

	}
	context.Set("RETURN", parameter.LoginResponse{
		Code:    tool.SIGN_IN_FAILED,

	})
}
//

// GetRegisterJSON 解析注册JSON
func GetRegisterJSON(context *gin.Context) {
	var registerParameter = parameter.RegisterRequest{}
	err := context.ShouldBindWith(&registerParameter, binding.JSON)
	log.Println(registerParameter)
	if err != nil {
		context.Set("RETURN", parameter.RegisterResponse{
			Code:    tool.SIGN_UP_FAILED,
			Data:    parameter.Data{},
		})
		context.Abort()
	} else {
		context.Set("JSON", registerParameter)
	}
}
func GetBlogById(context *gin.Context){
	userid,err := strconv.Atoi(context.Query("userid"))
	if err != nil {
		context.Set("RETURN", map[string]interface{}{
			"code":tool.QUERY_FAILED,
			"data":map[string]interface{}{
			},
		})
	}
	userService :=service.UserService{}
	code,blogs :=userService.GetBlogById(userid)
	if blogs != nil {
		blogResponses := make([]gin.H, 0)
		for _, blog := range blogs {
			list, _ := tool.Manager.GetPictureList(blog.MessageId)
			userDao := dao.UserDao{Orm: tool.DbEngine}
			comments := make([]*model.Comment, 0)
			userDao.Where("message_id = ?", blog.MessageId).Find(&comments)
			hs := make([]gin.H, 0)
			for _, comment := range comments {
				commentUser := model.Users{}
				userDao.Where("id = ?", comment.CommentUserId).Get(&commentUser)
				replyUser := model.Users{}
				userDao.Where("id = ?", comment.ReplyUserId).Get(&replyUser)
				h := gin.H{
					"commentId":       comment.CommentId,
					"createTime":      comment.CreateTime,
					"commentContent":  comment.CommentContent,
					"messageId":       comment.MessageId,
					"commentUserId":   comment.CommentUserId,
					"commentUserName": commentUser.RealName,
					"replyUserId":     comment.ReplyUserId,
					"replyUserName":   replyUser.RealName,
				}
				hs = append(hs, h)
			}
			user := userDao.GetUserById(blog.UserId)
			response := gin.H {
				"userId":     blog.UserId,
				"messageId":  blog.MessageId,
				"messageContent":    blog.Content,
				"messageTitle":        blog.Title,
				"createTime": blog.CreateTime,
				"images": list,
				"comments":hs,
				"user": gin.H {
					"id": user.Id,
					"name": user.Name,
					"email": user.Email,
					"password": user.Password,
					"type": user.Type,
					"realname": user.RealName,
					"sex": user.Sex,
					"imageurl": user.Avatar,
				},
			}
			blogResponses = append(blogResponses, response)
		}
		context.Set("RETURN", map[string]interface{}{
			"code":code,
			"data":map[string]interface{}{
				"blogList":blogResponses,
			},
		})
		return
	}
	context.Set("RETURN",map[string]interface{}{
		"code":code,
		"data":map[string]interface{}{
		},
	})
}

// GetAllBlog 获取所有blog
func GetAllBlog(context *gin.Context){
	query := context.Query("messageType")
	atoi, _ := strconv.Atoi(query)
	userService :=service.UserService{}
	code,blogs :=userService.GetAllBlog(atoi)
	if blogs != nil {
		blogResponses := make([]gin.H, 0)
		for _, blog := range blogs {
			list, _ := tool.Manager.GetPictureList(blog.MessageId)
			userDao := dao.UserDao{Orm: tool.DbEngine}
			comments := make([]*model.Comment, 0)
			userDao.Where("message_id = ?", blog.MessageId).Find(&comments)
			hs := make([]gin.H, 0)
			for _, comment := range comments {
				commentUser := model.Users{}
				userDao.Where("id = ?", comment.CommentUserId).Get(&commentUser)
				replyUser := model.Users{}
				userDao.Where("id = ?", comment.ReplyUserId).Get(&replyUser)
				h := gin.H{
					"commentId":       comment.CommentId,
					"createTime":      comment.CreateTime,
					"commentContent":  comment.CommentContent,
					"messageId":       comment.MessageId,
					"commentUserId":   comment.CommentUserId,
					"commentUserName": commentUser.RealName,
					"replyUserId":     comment.ReplyUserId,
					"replyUserName":   replyUser.RealName,
				}
				hs = append(hs, h)
			}
			user := userDao.GetUserById(blog.UserId)
			response := gin.H {
				"userId":     blog.UserId,
				"messageId":  blog.MessageId,
				"messageContent":    blog.Content,
				"messageTitle":        blog.Title,
				"createTime": blog.CreateTime,
				"images": list,
				"comments":hs,
				"user": gin.H {
					"id": user.Id,
					"name": user.Name,
					"email": user.Email,
					"password": user.Password,
					"type": user.Type,
					"realname": user.RealName,
					"sex": user.Sex,
					"imageurl": user.Avatar,
				},
			}
			blogResponses = append(blogResponses, response)
		}
		context.Set("RETURN", map[string]interface{}{
			"code":code,
			"data":blogResponses,
		})
		return
	}
	context.Set("RETURN",map[string]interface{}{
		"code":code,
		"data":nil,
	})

}

func GetCreateBlogJSON(context *gin.Context) {
	var blogParameter = parameter.Blog{}
	err := context.ShouldBindWith(&blogParameter, binding.JSON)
	log.Println(blogParameter)
	if err != nil {
		context.Set("RETURN", parameter.ScheduleResponse{
			Code: 0,
		})
		context.Abort()
	} else {
		context.Set("JSON", blogParameter)
	}
}

func CreateBlog(context *gin.Context)  {
	blogParameter_, _ := context.Get("JSON")
	blogParameter := blogParameter_.(parameter.Blog)
	userDao := dao.UserDao{Orm: tool.DbEngine}
	atoi, _ := strconv.Atoi(blogParameter.UserId)
	a, _ := strconv.Atoi(blogParameter.Type)
	blog := model.Blogs{
		UserId:     atoi,
		Content:    blogParameter.Content,
		Title:      blogParameter.Title,
		CreateTime: blogParameter.CreateTime,
		Type: a,
	}
	_, err := userDao.InsertOne(&blog)
	if err != nil {
		context.Set("RETURN",parameter.ScheduleResponse{
			Code: 0,
		})
		return
	}
	context.Set("RETURN",parameter.ScheduleResponse{
		Code: blog.MessageId,
	})
}

func ModifySchedule(context *gin.Context){
	scheduleParameter_, _ := context.Get("JSON")
	scheduleParameter := scheduleParameter_.(parameter.ScheduleRequest)

	userService := service.UserService{}
	code :=userService.ModifySchedule(scheduleParameter)
	context.Set("RETURN",parameter.ScheduleResponse{
		Code: code,
	})
}

// Register 注册
func Register(context *gin.Context) {
	registerParameter_, _ := context.Get("JSON")
	registerParameter := registerParameter_.(parameter.RegisterRequest)

	userService := service.UserService{}
	code,register := userService.Register(registerParameter)
	if register != nil {
		context.Set("RETURN", parameter.RegisterResponse{
			Code:    code,
			Data:    parameter.Data{},
		})
		return
	}
	context.Set("RETURN", parameter.RegisterResponse{
		Code:    code,
		Data:    parameter.Data{},
	})
	//context.Set("isLogin", false)
	//context.Set("username", registerParameter.Name)
}


func DeleteScheduleById(context *gin.Context){
	scheduleId,err :=strconv.Atoi(context.Query("scheduleId"))
	if err!=nil{
		context.Set("RETURN",parameter.ScheduleResponse{
			Code: tool.DELETE_FAILED,
		})

	}
	userService :=service.UserService{}
	code:=userService.DeleteScheduleById(scheduleId)
	context.Set("RETURN",parameter.ScheduleResponse{
		Code: code,
	})
}
// GetScheduleById 根据id查找课程表
func GetScheduleById(context *gin.Context){
	userid,err := strconv.Atoi(context.Query("userId"))
	if err != nil {
		context.Set("RETURN", map[string]interface{}{
			"code":tool.QUERY_FAILED,
			"data":map[string]interface{}{
			},
		})
	}
	userService :=service.UserService{}
	code, schedules :=userService.GetScheduleById(userid)
	if schedules != nil {
		scheduleResponses := make([]parameter.Schedule, 0)
		for _, schedule := range schedules {
			response := parameter.Schedule{
				ScheduleId: schedule.ScheduleId,
				UserId: 	schedule.UserId,
				Name: 		schedule.Name,
				Room:		schedule.Room,
				Teacher: 	schedule.Teacher,
				Start: 		schedule.Start,
				Step:  		schedule.Step,
				Day:  		schedule.Day,
				Week: 		schedule.Week,

			}
			scheduleResponses = append(scheduleResponses, response)
		}
		context.Set("RETURN", map[string]interface{}{
			"code":code,
			"data":map[string]interface{}{
				"scheduleList": scheduleResponses,
			},
		})
		return
	}
	context.Set("RETURN",map[string]interface{}{
		"code":code,
		"data":map[string]interface{}{
		},
	})
}

// GetScheduleJSON  解析课程表json
func GetScheduleJSON(context *gin.Context) {
	var scheduleParameter = parameter.ScheduleRequest{}
	err := context.ShouldBindWith(&scheduleParameter, binding.JSON)
	log.Println(scheduleParameter)
	if err != nil {
		context.Set("RETURN", parameter.RegisterResponse{
			Code:    tool.SIGN_UP_FAILED,
			Data:    parameter.Data{},
		})
		context.Abort()
	} else {
		context.Set("JSON", scheduleParameter)
	}
}

func UpdateAvatar(context *gin.Context)  {
	header := context.GetHeader("userId")
	file, err := context.FormFile("file")
	filename := file.Filename
	err = context.SaveUploadedFile(file, filename)
	if err != nil {
		context.Set("RETURN", gin.H{
			"code":tool.CODE_FAILED,
		})
		return
	}
	userId, _ := strconv.Atoi(header)
	err = tool.Manager.UpdateAvatar(userId, filename)
	if err != nil {
		context.Set("RETURN", gin.H{
			"code":tool.CODE_FAILED,
		})
		return
	}
	context.Set("RETURN",parameter.ScheduleResponse{
		Code: 1,
	})
}

func GetImageById(context *gin.Context){
	userid,err := strconv.Atoi(context.GetHeader("userid"))
	if err != nil {
		context.Set("RETURN",parameter.ImageResponse{
			Code: tool.QUERY_FAILED,

		})
	}
	userService :=service.UserService{}
	code,imageUrl :=userService.GetImageById(userid)
	if imageUrl != "" {

		context.Set("RETURN",parameter.ImageResponse{
			Code: code,
			ImageUrl: imageUrl,
		})
		return
	}
	context.Set("RETURN",parameter.ImageResponse{
		Code: code,
	})
}

func InsertPicture(context *gin.Context) {
	header := context.GetHeader("messageId")
	file, err := context.FormFile("file")
	filename := file.Filename
	err = context.SaveUploadedFile(file, filename)
	if err != nil {
		context.Set("RETURN", gin.H{
			"code":tool.CODE_FAILED,
		})
		return
	}
	messageId, _ := strconv.Atoi(header)
	err = tool.Manager.InsertPicture(messageId, filename)
	if err != nil {
		context.Set("RETURN", gin.H{
			"code":tool.CODE_FAILED,
		})
		return
	}
	context.Set("RETURN",parameter.ScheduleResponse{
		Code: tool.CODE_SUCCESS,
	})
}

func ModifyPassword(context *gin.Context) {
	var user parameter.User
	err := context.ShouldBindWith(&user, binding.JSON)
	if err != nil {
		context.Set("RETURN", gin.H{
			"code":tool.UPDATE_FAILED,
		})
		return
	}
	userDao := dao.UserDao{Orm: tool.DbEngine}
	users := userDao.ModifyUserPassword(user.Id, user.Password)
	if users == nil {
		context.Set("RETURN", gin.H{
			"code":tool.UPDATE_FAILED,
		})
		return
	}
	if users.Password == user.Password {
		context.Set("RETURN", gin.H{
			"code":tool.UPDATE_SUCCESS,
		})
		return
	}
	context.Set("RETURN", gin.H{
		"code":tool.UPDATE_FAILED,
	})
	return
}

func GetBlogByTag(context *gin.Context){
	u := dao.UserDao{Orm: tool.DbEngine}
	query := context.Query("tag")
	blogs := u.GetBlogsByTag(query)
	if blogs != nil {
		blogResponses := make([]gin.H, 0)
		for _, blog := range blogs {
			list, _ := tool.Manager.GetPictureList(blog.MessageId)
			userDao := dao.UserDao{Orm: tool.DbEngine}
			user := userDao.GetUserById(blog.UserId)
			response := gin.H {
				"userId":     blog.UserId,
				"messageId":  blog.MessageId,
				"messageContent":    blog.Content,
				"messageTitle":        blog.Title,
				"createTime": blog.CreateTime,
				"images": list,
				"user": gin.H {
					"id": user.Id,
					"name": user.Name,
					"email": user.Email,
					"password": user.Password,
					"type": user.Type,
					"realname": user.RealName,
					"sex": user.Sex,
					"imageurl": user.Avatar,
				},
			}
			blogResponses = append(blogResponses, response)
		}
		context.Set("RETURN", map[string]interface{}{
			"code":tool.CODE_SUCCESS,
			"data":blogResponses,
		})
		return
	}
	context.Set("RETURN",map[string]interface{}{
		"code":tool.CODE_FAILED,
		"data":nil,
	})

}
func GetBlogByTitle(context *gin.Context){
	u := dao.UserDao{Orm: tool.DbEngine}
	query := context.Query("title")
	blogs := u.GetBlogsByTitle(query)
	if blogs != nil {
		blogResponses := make([]gin.H, 0)
		for _, blog := range blogs {
			list, _ := tool.Manager.GetPictureList(blog.MessageId)
			userDao := dao.UserDao{Orm: tool.DbEngine}
			user := userDao.GetUserById(blog.UserId)
			response := gin.H {
				"userId":     blog.UserId,
				"messageId":  blog.MessageId,
				"messageContent":    blog.Content,
				"messageTitle":        blog.Title,
				"createTime": blog.CreateTime,
				"images": list,
				"user": gin.H {
					"id": user.Id,
					"name": user.Name,
					"email": user.Email,
					"password": user.Password,
					"type": user.Type,
					"realname": user.RealName,
					"sex": user.Sex,
					"imageurl": user.Avatar,
				},
			}
			blogResponses = append(blogResponses, response)
		}
		context.Set("RETURN", map[string]interface{}{
			"code":tool.CODE_SUCCESS,
			"data":blogResponses,
		})
		return
	}
	context.Set("RETURN",map[string]interface{}{
		"code":tool.CODE_FAILED,
		"data":nil,
	})

}

func GetAllNotification(context *gin.Context) {
	userDao := dao.UserDao{Orm: tool.DbEngine}
	notifications := userDao.GetAllNotification()
	hs := make([]gin.H, 0)
	for _, notification := range notifications {
		h := gin.H{
			"notificationId":      notification.Id,
			"createTime":          notification.CreateTime,
			"notificationTitle":   notification.Title,
			"notificationContent": notification.Content,
		}
		hs = append(hs, h)
	}
	context.Set("RETURN", hs)
}

func UpdateComment(context *gin.Context) {
	var commentRequest parameter.CommentRequest
	err := context.ShouldBindWith(&commentRequest, binding.JSON)
	if err != nil {
		context.Set("RETURN",map[string]interface{}{
			"code":tool.UPDATE_FAILED,
			"data":nil,
		})
		return
	}
	comment := model.Comment{
		CreateTime:     commentRequest.CreateTime,
		CommentContent: commentRequest.CommentContent,
		MessageId:      commentRequest.MessageId,
		CommentUserId:  commentRequest.CommentUserId,
		ReplyUserId:    commentRequest.ReplyUserId,
	}
	userDao := dao.UserDao{Orm: tool.DbEngine}
	_, err = userDao.InsertOne(&comment)
	if err != nil {
		context.Set("RETURN",map[string]interface{}{
			"code":tool.UPDATE_FAILED,
			"data":nil,
		})
		return
	}
	context.Set("RETURN",map[string]interface{}{
		"code":tool.UPDATE_FAILED,
		"data":comment.CommentId,
	})
	return
}

func GetCommentById(context *gin.Context)  {
	query := context.Query("userid")
	userDao := dao.UserDao{Orm: tool.DbEngine}
	comments := make([]*model.Comment, 0)
	err := userDao.Where("comment_user_id = ?", query).Find(&comments)
	if err != nil {
		context.Set("RETURN", gin.H{
			"code":tool.QUERY_SUCCESS,
			"data":nil,
		})
	}
	hs := make([]gin.H, 0)
	for _, comment := range comments {
		commentUser := model.Users{}
		userDao.Where("id = ?", comment.CommentUserId).Get(&commentUser)
		replyUser := model.Users{}
		userDao.Where("id = ?", comment.ReplyUserId).Get(&replyUser)
		h := gin.H{
			"commentId":       comment.CommentId,
			"createTime":      comment.CreateTime,
			"commentContent":  comment.CommentContent,
			"messageId":       comment.MessageId,
			"commentUserId":   comment.CommentUserId,
			"commentUserName": commentUser.RealName,
			"replyUserId":     comment.ReplyUserId,
			"replyUserName":   replyUser.RealName,
		}
		hs = append(hs, h)
	}
	context.Set("RETURN", gin.H{
		"code":tool.QUERY_SUCCESS,
		"data":hs,
	})
}

func GetAllUser(context *gin.Context)  {
	query := context.Query("userid")
	userDao := dao.UserDao{Orm: tool.DbEngine}
	var user = make([]*model.Users, 0)
	userDao.Where("id != ?", query).Find(&user)
	users := make([]parameter.User, 0)
	for _, u := range user {
		u2 := parameter.User{
			Name:     u.Name,
			Password: u.Password,
			Email:    u.Email,
			Id:       u.Id,
			Type:     u.Type,
			RealName: u.RealName,
			Sex:      u.Sex,
			Avatar:   u.Avatar,
		}
		users = append(users, u2)
	}
	context.Set("RETURN", users)
}