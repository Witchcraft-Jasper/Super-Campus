package model

type Contract struct {
	Id			int64	`xorm:"pk autoincr"`
	Name		string	`xorm:"varchar(40)"`
	CustomerId	int64	`xorm:"int"`
	BeginTime	string	`xorm:"date"`
	EndTime		string	`xorm:"date"`
	Content		string	`xorm:"text"`
	UserId		int		`xorm:"int"`
}
