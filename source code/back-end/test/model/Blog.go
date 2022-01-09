package model

type Blogs struct {
	UserId  int		`xorm:"int notnull" json:"user_id"`
	MessageId  int		`xorm:"pk int autoincr" `
	Content string	`xorm:"varchar(255) notnull" `
	Title string `xorm:"varchar(255)" `
	CreateTime string `xorm:"varchar(255)"`
	Tag string `xorm:"varchar(252)"`
	Type int `xorm:"int"`
}
