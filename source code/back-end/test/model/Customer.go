package model
type Customer struct {
	Id           	int 	`xorm:"pk int autoincr " json:"id"`                   // uuid
	Name     		string 	`xorm:"varchar(40) notnull unique" json:"user_name"` // 用户名
	Email        	string 	`xorm:"varchar(30) notnull unique" json:"email"`     // 电子邮件
	Password    	string 	`xorm:"varchar(50) notnull" json:"password"`        // 密码
	Address 		string	`xorm:""`
	Tel 			string
	Fax 			string
	Code 			string
	Bank			string
	account 		string
}
