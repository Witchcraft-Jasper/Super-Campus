package model

type Users struct {
	Id       int    `xorm:"pk int(11) autoincr " json:"id"`              // uuid
	Name     string `xorm:"varchar(20) notnull unique" json:"name"` // 用户名
	Email    string `xorm:"varchar(30) notnull unique" json:"email"`     // 电子邮件
	Password string `xorm:"varchar(50) notnull" json:"password"`         // 密码
	Type	 int 	`xorm:"int notnull" json:"type"`
	RealName string	`xorm:"varchar(20) notnull" json:"realname"`
	Sex      string `xorm:"varchar(2) " json:"sex"`
	Avatar   string `xorm:"varchar(255)"`
}
