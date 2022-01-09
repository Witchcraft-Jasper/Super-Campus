package service

import (
	"fmt"
	"github.com/jordan-wright/email"
	"log"
	"math/rand"
	"net/smtp"
	"strconv"
	"test/dao"
	"test/model"
	"test/parameter"
	"test/tool"
	"time"
)

// UserService 和用户相关的服务
type UserService struct {
}

// SendCode 发送验证码
func (s *UserService) SendCode(email string) (bool, string) {
	userDao := dao.UserDao{Orm: tool.DbEngine}
	codeByPhone := userDao.QueryCodeByEmail(email)
	if codeByPhone != nil {
		return false, ""
	}
	// 生成随机验证码
	code := fmt.Sprintf("%06v", rand.New(rand.NewSource(time.Now().UnixNano())).Int31n(1000000))
	go s.SendEmail(email,code)

	// 数据库记录验证码
	emailCode := model.EmailCode{Email: email, Code: code, CreateTime: time.Now().Unix()}
	result := userDao.InsertEmailCode(&emailCode)

	return result > 0, code
}

// SendEmail qq邮箱发验证码
func(s *UserService)SendEmail(Email string, code string){
	// 简单设置 log 参数
	log.SetFlags(log.Lshortfile | log.LstdFlags)

	em := email.NewEmail()
	// 设置 sender 发送方 的邮箱 ， 此处可以填写自己的邮箱
	em.From = "superCampus<1501496964@qq.com>"

	// 设置 receiver 接收方 的邮箱  此处也可以填写自己的邮箱， 就是自己发邮件给自己
	em.To = []string{Email}

	// 设置主题
	em.Subject = "验证码"

	// 简单设置文件发送的内容，暂时设置成纯文本
	em.Text = []byte("验证码，code="+code)

	//设置服务器相关的配置
	err := em.Send("smtp.qq.com:25", smtp.PlainAuth("", "1501496964@qq.com", "aqfocxmjeociijah", "smtp.qq.com"))
	if err != nil {
		log.Fatal(err)
	}
	log.Println("send successfully ... ")
}

////调用腾讯云API发送邮箱验证码
//func (s *UserService) SendCode(email string)  {
//	// 必要步骤：
//	// 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
//	// 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
//	// 以免泄露密钥对危及你的财产安全。
//	credential := common.NewCredential(
//		"AKIDIYlxynOltoX4NTaenphMnzyjFWXJp6Ol",
//		"LZeWAZjfMoDRSEm8G9nCLHzSgfPu98rK",
//	)
//	// 非必要步骤
//	// 实例化一个客户端配置对象，可以指定超时时间等配置
//	cpf := profile.NewClientProfile()
//	//指定域名
//	cpf.HttpProfile.Endpoint = "domain.tencentcloudapi.com"
//	//实例化要请求产品(以cvm为例)的client对象
//	client, _ := domain.NewClient(credential, "", cpf)
//	// 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
//	request := domain.NewCreatePhoneEmailRequest()
//	//请求所需要的参数
//	request.Code = common.StringPtr(email)
//	request.Type = common.Uint64Ptr(2)//1是手机2是邮箱
//	//request.VerifyCode = common.StringPtr("123456")
//	// 通过client对象调用想要访问的接口，需要传入请求对象
//	response, err := client.CreatePhoneEmail(request)
//	if _, ok := err.(*errors.TencentCloudSDKError); ok {
//		fmt.Printf("An API error has returned: %s", err)
//		return
//	}
//	if err != nil {
//		panic(err)
//	}
//	fmt.Printf("%s", response.ToJsonString())
//}

// LoginByEmail  邮箱验证码登录
func (s *UserService) LoginByEmail(loginParam parameter.LoginByEmailRequest) *model.Users {
	// 查询验证码表里是否有此记录，返回code
	userDao := dao.UserDao{Orm: tool.DbEngine}
	emailCode := userDao.ValidateEmailCode(loginParam.Email, loginParam.Code)
	//验证码表中找不到
	if emailCode.Id == 0 {
		return nil
	}

	//查询此用户是否已经注册，因为有没注册但是有验证码的情况
	//通过email查找用户，返回用户
	result1 := userDao.QueryByEmail(loginParam.Email)
	if result1 == nil {
		return nil
	}
	//result2 := userDao.UpdateLoggedDeviceNumber(1, result1.Name)
	//if result2 == nil {
	//	return nil
	//}
	return result1
}

// LoginByPassword 密码登录
func (s *UserService) LoginByPassword(loginParam parameter.LoginByPasswordRequest) *model.Users {
	userDao := dao.UserDao{Orm: tool.DbEngine}
	result1 := userDao.ValidatePassword(loginParam.Name, loginParam.Password)
	if result1 == nil {
		return nil
	}
	//result2 := userDao.UpdateLoggedDeviceNumber(1, result1.Name)
	//if result2 == nil {
	//	return nil
	//}
	return result1
}

func (s UserService) DeleteBlogById(blogId int) int {
	userDao:=dao.UserDao{Orm: tool.DbEngine}
	result:=userDao.DeleteBlog(blogId)
	if result==0 {
		return tool.DELETE_FAILED

	}
	return tool.DELETE_SUCCESS
}

// DeleteScheduleById 根据id删除课程
func (s *UserService)DeleteScheduleById(scheduleId int) int {
	userDao :=dao.UserDao{Orm: tool.DbEngine}
	result	:=userDao.DeleteSchedule(scheduleId)
	if result==0 {
		return tool.DELETE_FAILED
	}
	return tool.DELETE_SUCCESS
}

// ModifySchedule 修改课程表
func (s *UserService)ModifySchedule(scheduleParam parameter.ScheduleRequest) int {
	userDao := dao.UserDao{Orm: tool.DbEngine}
	scheduleId, err := strconv.Atoi(scheduleParam.ScheduleId)
	userId, err := strconv.Atoi(scheduleParam.UserId)
	start, err := strconv.Atoi(scheduleParam.Start)
	step, err := strconv.Atoi(scheduleParam.Step)
	day, err := strconv.Atoi(scheduleParam.Day)

	if err != nil {
		log.Fatal(err.Error())
	}
	if scheduleId == 0 {
		result :=userDao.InsertSchedule(userId,scheduleParam.Name,scheduleParam.Room,scheduleParam.Teacher,start,step,day,scheduleParam.Week)
		if result==0{
			return tool.UPDATE_FAILED
		}
		return tool.UPDATE_SUCCESS
	}else {
		result :=userDao.ModifySchedule(scheduleId,userId,scheduleParam.Name,scheduleParam.Room,scheduleParam.Teacher,start,step,day,scheduleParam.Week)
		if result==0{
			return tool.UPDATE_FAILED
		}
		return tool.UPDATE_SUCCESS
	}

}

// Register 注册
func (s *UserService) Register(registerParam parameter.RegisterRequest) (int ,*model.Users) {
	userDao := dao.UserDao{Orm: tool.DbEngine}
	emailCode := userDao.ValidateEmailCode(registerParam.Email, registerParam.Code)
	if emailCode==nil {//验证码错误
		return tool.WRONG_CODE,nil
	}
	t,err :=strconv.Atoi(registerParam.Type)
	if err != nil {
		log.Fatal(err.Error())
	}
	result, user := userDao.InsertUser(registerParam.Name,registerParam.Email,registerParam.Password,registerParam.RealName,registerParam.Sex,t)
	if result == 0 {//已注册
		return tool.SIGN_UP_FAILED,nil
	}
	return tool.SIGN_UP_SUCCESS,user
}

// GetAllBlog  获取所有blog
func(s *UserService) GetAllBlog(messageType int)(int,[]*model.Blogs){
	userDao :=dao.UserDao{Orm: tool.DbEngine}
	blogs :=userDao.GetAllBlog(messageType)
	if blogs ==nil{
		return tool.LOAD_BLOG_FAILED,blogs
	}
	return tool.LOAD_BLOG_SUCCESS,blogs
}

// GetBlogById  通过userid获取blog
func (s *UserService) GetBlogById(userid int) (int,[]*model.Blogs){
	userDao := dao.UserDao{Orm:	tool.DbEngine}
	blogs :=userDao.GetBlogById(userid)
	if blogs==nil{
		return tool.QUERY_FAILED,blogs
	}
	return tool.QUERY_SUCCESS,blogs
}

func (s *UserService)GetScheduleById(userid int)(int,[]*model.Schedule)  {
	userDao := dao.UserDao{Orm:	tool.DbEngine}
	schedules :=userDao.GetSchedule(userid)
	if schedules==nil{
		return tool.QUERY_FAILED,schedules
	}
	return tool.QUERY_SUCCESS,schedules

}

// GetUsernameByEmail email获取username
func (s *UserService) GetUsernameByEmail(loginParam parameter.LoginByEmailRequest) string {
	userDao := dao.UserDao{Orm: tool.DbEngine}
	user := userDao.QueryByEmail(loginParam.Email)
	if user != nil {
		return user.Name
	}
	return ""
}

// GetImageById 根据id获取头像
func (s *UserService) GetImageById(userid int) (int,string) {
	userDao := dao.UserDao{Orm: tool.DbEngine}
	user := userDao.GetUserById(userid)
	if user == nil {
		return tool.QUERY_FAILED,""
	}
	return tool.QUERY_SUCCESS,user.Avatar
}




//func (s *UserService) LogoffUser(username string) error {
//	userDao := dao.UserDao{Orm: tool.DbEngine}
//	err := userDao.DeleteUserByUsername(username)
//	return err
//}
