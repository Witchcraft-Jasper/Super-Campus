package dao

import (
	"github.com/prometheus/common/log"
	"test/model"
	"test/tool"
	"time"
)

type UserDao struct {
	*tool.Orm
}

func (u *UserDao) InsertEmailCode(code *model.EmailCode) int64 {
	result,err := u.InsertOne(code)
	if err!=nil{
		log.Error(err.Error())
	}
	return result
}

func (u *UserDao) QueryCodeByEmail(email string)*model.EmailCode {
	var emailCode model.EmailCode
	has,err := u.Where("email = ?",email).Get(&emailCode)
	if err!=nil{
		log.Fatal(err.Error())
	}
	if has{
		return &emailCode
	} else {
		return nil
	}
}
// CleanOutdatedEmailCode 清除过期数据
func (u *UserDao) CleanOutdatedEmailCode(expireTime int64) {
	_, err := u.Where("create_time < ? - ?", time.Now().Unix(), expireTime).Delete(model.EmailCode{})
	if err != nil {
		log.Fatal(err.Error())
	}
}
// ValidateEmailCode 查看是否有此手机验证码数据对在数据库中
func (u *UserDao) ValidateEmailCode(email string, code string) *model.EmailCode {
	var emailCode model.EmailCode
	has, err := u.Where("email = ? AND code = ?", email, code).Get(&emailCode)
	if err != nil {
		log.Fatal("error in validating email code")
		return nil
	}
	if has {
		return &emailCode
	} else {
		return nil
	}

}

// InsertUser 插入新用户
func (u *UserDao) InsertUser(username string, email string, password string,realName string,sex string,isStudent int) (int64, *model.Users) {

	var user = model.Users{
		Name:     	username,
		Email:    	email,
		Password:	password,
		RealName: 	realName,
		Sex:		sex,
		Type: 		isStudent,
	}

	result, err := u.InsertOne(&user)
	if err != nil {
		log.Fatal(err.Error())
		return 0, nil
	}
	return result, &user
}

// QueryByEmail  查看此手机是否被注册
func (u *UserDao) QueryByEmail(email string) *model.Users {
	var user model.Users
	has, err := u.Where("email = ?", email).Get(&user)
	if err != nil {
		log.Fatal(err.Error())
	}
	if has {
		return &user
	} else {
		return nil
	}
}
// ValidatePassword 查看用户名和密码是否正确
func (u *UserDao) ValidatePassword(username string, password string) *model.Users {
	var user model.Users
	has, err := u.Where("name = ? AND password = ?", username, password).Get(&user)
	if err != nil {
		log.Fatal(err.Error())
	}
	if has {
		return &user
	} else {
		return nil
	}
}

// GetAllBlog 取出所有Blog记录
func (u *UserDao)GetAllBlog(messageType int) []*model.Blogs {
	blogs := make([]*model.Blogs, 0)
	err :=u.Where("type = ?", messageType).Find(&blogs)
	if err != nil {
		log.Fatal(err.Error())
	}
	if len(blogs) > 0{
		return blogs
	}else {
		return nil
	}

}

// GetBlogById 取出指定id的所有记录
func (u *UserDao)GetBlogById(userid int) []*model.Blogs {
	blogs :=make([]*model.Blogs,0)
	err :=u.Where("user_id = ?", userid).Find(&blogs)
	if err != nil {
		log.Fatal(err.Error())
	}
	if len(blogs)>0{
		return blogs
	}else {
		return nil
	}

}
func(u *UserDao)GetSchedule(userid int)	[]*model.Schedule{
	schedules := make([]*model.Schedule,0)
	err :=u.Where("user_id=?",userid).Find(&schedules)
	if err != nil {
		log.Fatal(err.Error())
	}
	if len(schedules)>0{
		return schedules
	}else {
		return nil
	}
}
func(u *UserDao)ModifySchedule(scheduleId int,userId int,name string,room string,teacher string,start int,step int,day int,week string)int{
	 schedule := model.Schedule{
		ScheduleId: scheduleId,
		UserId: userId,
		Name: name,
		Room: room,
		Teacher: teacher,
		Start: start,
		Step: step,
		Day: day,
		Week: week,
	}
	_, err := u.Id(scheduleId).Update(&schedule)
	if err != nil {
		log.Fatal(err.Error())
		return 0
	}
	return 1
}
func(u *UserDao)InsertSchedule(userId int,name string,room string,teacher string,start int,step int,day int,week string)int{
	schedule := model.Schedule{
		UserId: userId,
		Name: name,
		Room: room,
		Teacher: teacher,
		Start: start,
		Step: step,
		Day: day,
		Week: week,
	}
	_,err :=u.Insert(&schedule)
	if err != nil {
		log.Fatal(err.Error())
		return 0
	}
	return 1
}
func(u *UserDao)DeleteSchedule(scheduleId int) int{
	var schedule model.Schedule
	_, err := u.Where("schedule_id = ?", scheduleId).Delete(&schedule)
	if err != nil {
		log.Fatal(err.Error())
		return 0
	}
	return 1
}
func (u *UserDao) DeleteBlog(blogId int)  int{
	var blog model.Blogs
	_, err := u.Where("blog_id = ?", blogId).Delete(&blog)
	if err != nil {
		log.Fatal(err.Error())
		return 0
	}
	return 1
}
func(u *UserDao)InsertBlog(userId int,name string,room string,teacher string,start int,step int,day int,week string)int{
	schedule := model.Schedule{
		UserId: userId,
		Name: name,
		Room: room,
		Teacher: teacher,
		Start: start,
		Step: step,
		Day: day,
		Week: week,
	}
	_,err :=u.Insert(&schedule)
	if err != nil {
		log.Fatal(err.Error())
		return 0
	}
	return 1
}

// GetUserById 根据id取出头像
func (u *UserDao) GetUserById(userid int) *model.Users {
	var user model.Users
	has,err :=u.Where("id = ?", userid).Get(&user)
	if err != nil {
		log.Fatal(err.Error())
	}
	if has {
		return &user
	} else {
		return nil
	}

}

func (u *UserDao) ModifyUserPassword(userid int, password string) *model.Users{
	var user model.Users
	user.Password = password
	_, err := u.Where("id = ?", userid).Cols("password").Update(&user)
	if err != nil {
		return nil
	}
	return &user
}

func (u *UserDao) GetBlogsByTag(tag string) []*model.Blogs {
	blogs := make([]*model.Blogs, 0)
	err := u.Where("tag like ?", "%"+tag+"%").Find(&blogs)
	if err != nil {
		return nil
	}
	return blogs
}

func (u *UserDao) GetBlogsByTitle(title string) []*model.Blogs {
	blogs := make([]*model.Blogs, 0)
	err := u.Where("title like ?", "%"+title+"%").Find(&blogs)
	if err != nil {
		return nil
	}
	return blogs
}

func (u *UserDao) GetAllNotification() []*model.Notification {
	notifications := make([]*model.Notification, 0)
	err := u.Find(&notifications)
	if err != nil {
		return nil
	}
	return notifications
}