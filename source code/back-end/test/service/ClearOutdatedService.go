package service

import (
	"test/dao"
	"test/tool"
)

// ClearOutdatedData 清理过期验证码服务
func ClearOutdatedData(expireTime int64) {
	userDao := dao.UserDao{Orm: tool.DbEngine}
	userDao.CleanOutdatedEmailCode(expireTime)
}
