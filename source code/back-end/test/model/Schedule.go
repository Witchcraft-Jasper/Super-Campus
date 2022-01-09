package model

type Schedule struct {
	ScheduleId	int				`xorm:"pk int autoincr" json:"scheduleid"`
	UserId		int				`xorm:"int notnull" json:"userid"`
	Name 		string			`xorm:"varchar(20) notnull" json:"name"`
	Room 		string			`xorm:"varchar(20) notnull" json:"room"`
	Teacher     string  		`xorm:"varchar(20) notnull" json:"teacher"`
	Start		int				`xorm:"int notnull" json:"start"`
	Step		int				`xorm:"int notnull" json:"step"`
	Day			int				`xorm:"int notnull" json:"day"`
	Week		string			`xorm:"varchar(255) notnull" json:"week"`
}