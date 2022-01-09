package model


type Files struct {
	FileId		string	`xorm:"pk varchar(255)  " json:"file_id"`
	Ownername	string	`xorm:"varchar(255) notnull " json:"owner_name"`
	Filename	string	`xorm:"varchar(255) notnull " json:"filename"`
}