package router

import (
	"github.com/gin-contrib/sessions"
	"github.com/gin-contrib/sessions/redis"
	"github.com/gin-gonic/gin"
	"test/controller"
	"test/middleware"
)

func SetRoute(store redis.Store) *gin.Engine {
	engine := gin.New()
	/*
		middleware.ReturnJSON 用于统一返回Json
		gin.Recovery 发生panic时恢复服务器状态
		gin.Logger 记录请求
	*/
	engine.Use(sessions.Sessions("mySession",store),middleware.ReturnJSON,middleware.Cors(), gin.Recovery(), gin.Logger())
	/*
		controller.GetSendCodeJSON 解析客户端携带的Json
		controller.SendCode 发送验证码的控制器
	*/
	applyCode := engine.Group("/code") // 发送验证码
	{
		applyCode.POST("", controller.GetSendCodeJSON, controller.SendCode)
	}
	/*
		controller.GetRegisterJSON 解析客户端携带的Json
		controller.Register 管理注册的控制器
	*/
	register := engine.Group("/register") // 注册相关的功能
	{
		register.POST("", controller.GetRegisterJSON, controller.Register)
	}
	/*
		controller.GetLoginByPhoneJSON 解析客户端携带的Json
		controller.GetLoginByPasswordJSON 解析客户端携带的Json
		controller.LoginByPhone 管理登录的控制器
		controller.LoginByPassword 管理登录的控制器
	*/
	login := engine.Group("/login") // 登录相关的功能
	{
		login.POST("/sms", controller.GetLoginByEmailJSON, controller.LoginByEmail)
		login.POST("/word", controller.GetLoginByPasswordJSON,  controller.LoginByPassword)
	}
	/*
		GetALLBlog	获取所有blog
		GetBlogById 根据id获取blog
	*/
	blog := engine.Group("/blog")
	{
		blog.GET("/all",controller.GetAllBlog)
		blog.GET("/userid",controller.GetBlogById)
		blog.GET("/title",controller.GetBlogByTitle)
		blog.GET("/tag",controller.GetBlogByTag)
		blog.POST("/update",controller.GetCreateBlogJSON, controller.CreateBlog)
		blog.POST("/picture",controller.InsertPicture)
	}

	schedule :=engine.Group("/schedule")
	{
		schedule.GET("/GetScheduleByUserId",controller.GetScheduleById)
		schedule.POST("/AddSchedule",controller.GetScheduleJSON,controller.ModifySchedule)
		schedule.GET("/DeleteScheduleById",controller.DeleteScheduleById)
	}

	uploadFile := engine.Group("/upload")
	{
		uploadFile.POST("", controller.UpdateAvatar)
	}
	user :=engine.Group("/user")
	{

		user.GET("/image",controller.GetImageById)
		user.GET("/all",controller.GetAllUser)
		user.POST("/password", controller.ModifyPassword)
	}
	notification := engine.Group("/notification")
	{
		notification.GET("/all", controller.GetAllNotification)
	}
	comment := engine.Group("/comment")
	{
		comment.POST("", controller.UpdateComment)
		comment.GET("/id", controller.GetCommentById)
	}
	///*
	//	controller.GetLogoutJSON 解析客户端携带的Json
	//	middleware.SessionController 用于统一生成Session
	//	controller.Logout 管理登出的控制器
	//*/
	//cancel := engine.Group("/logout") // 登出相关的功能
	//{
	//	cancel.POST("/", controller.GetLogoutJSON, middleware.SessionController, controller.Logout)
	//}

	return engine
}
