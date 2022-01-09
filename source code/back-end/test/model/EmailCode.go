package model

type EmailCode struct {
	Id         int64  `xorm:"pk autoincr" json:"id"`     // 自增ID
	Email      string `xorm:"varchar(255)" json:"email"`  // 邮箱
	Code       string `xorm:"varchar(6)" json:"code"`    // 验证码
	CreateTime int64  `xorm:"bigint" json:"create_time"` // 生成时间，用来清除过期验证码
}

