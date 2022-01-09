package parameter

// CodeData 验证码及相关信息
type CodeData struct {
	Code   string `json:"code"`
	ExpireTime   int    `json:"expire_time"`
	DecisionType int    `json:"decision_type"`
}

// Data	响应时携带数据
type Data struct {
	User `json:"user"`
}

// ApplyCodeRequest 验证码请求
type ApplyCodeRequest struct {
	Email string `json:"email"`
}

// ApplyCodeResponse 验证码响应
type ApplyCodeResponse struct {
	Code     int    `json:"code"`

}

// RegisterRequest  注册请求
type RegisterRequest struct {
	Name    	string 	`json:"name"`
	Password    string 	`json:"password"`
	Email 		string 	`json:"email"`
	Code  		string 	`json:"code"`
	Type 		string	`json:"type"`
	RealName	string 	`json:"realname"`
	Sex 		string 	`json:"sex"`
}

// RegisterResponse 注册响应
type RegisterResponse struct {
	Code	int		`json:"code"`
	Data			`json:"data"`
}



// LoginByPasswordRequest 密码登录请求
type LoginByPasswordRequest struct {
	Name     string `json:"name"`
	Password string `json:"password"`

}

// LoginByEmailRequest  邮件验证码登录请求
type LoginByEmailRequest struct {
	Email string `json:"email"`
	Code  string `json:"code"`

}

// LoginResponse 登录响应
type LoginResponse struct {
	Code    int  	`json:"code"`
	Data    		`json:"data"`
}

// LogoutRequest 登出/注销请求
type LogoutRequest struct {
	ActionType  int `json:"action_type"`

}

// LogoutResponse 登出/注销响应
type LogoutResponse struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
}

// User 用户信息
type User struct {
	Name     	string 	`json:"name"`
	Password 	string 	`json:"password"`
	Email 		string 	`json:"email"`
	Id 			int    	`json:"id"`
	Type 		int		`json:"type"`
	RealName	string	`json:"realname"`
	Sex 		string	`json:"sex"`
	Avatar    	string	`json:"imageurl"`
}

// Blog 博客信息
type Blog struct {
	UserId    string    `json:"user_id"`
	Content   string `json:"content"`
	Title      string `json:"title"`
	CreateTime string    `json:"create_time"`
	Tag    		string	`json:"tag"`
	Type        string `json:"type"`
}

type BlogResponse struct {
	UserId    int    `json:"userId"`
	MessageId int    `json:"messageId"`
	Content   string `json:"messageContent"`
	Title      string `json:"messageTitle"`
	CreateTime string    `json:"createTime"`
	Tag    		string	`json:"tag"`
	Type       string   `json:"type"`
}
type Schedule struct {
	ScheduleId	int	`json:"scheduleId"`
	UserId		int	`json:"userId"`
	Name 		string	`json:"name"`
	Room 		string	`json:"room"`
	Teacher     string  `json:"teacher"`
	Start		int		`json:"start"`
	Step		int		`json:"step"`
	Day			int		`json:"day"`
	Week		string	`json:"week"`
}

// ScheduleRequest schedule请求
type ScheduleRequest struct {
	ScheduleId	string	`json:"scheduleId"`
	UserId		string	`json:"userId"`
	Name 		string	`json:"name"`
	Room 		string	`json:"room"`
	Teacher     string  `json:"teacher"`
	Start		string		`json:"start"`
	Step		string		`json:"step"`
	Day			string		`json:"day"`
	Week		string	`json:"week"`
}
type ScheduleResponse struct {
	Code	int	`json:"code"`
}

type ImageRequest struct {
	Id 		int 	`json:"id"`
}
type ImageResponse struct {
	Code			int		`json:"code"`
	ImageUrl		string	`json:"imageurl"`
}

type CommentRequest struct {
	//CommentId int `json:"commentId"`
	CreateTime string `json:"createTime"`
	CommentContent string `json:"commentContent"`
	MessageId int `json:"messageId"`
	CommentUserId int `json:"commentUserId"`
	ReplyUserId int `json:"replyUserId"`
}

