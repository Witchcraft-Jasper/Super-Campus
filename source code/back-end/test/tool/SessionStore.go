package tool

import (
	"github.com/gin-contrib/sessions"
	"github.com/gin-gonic/gin"
)

func SetSession(context *gin.Context,key interface{},value interface{}) error {
	session :=sessions.Default(context)
	if session == nil {
		return nil
	}
	session.Set(key,value)
	return session.Save()
	
}
func GetSession(context *gin.Context,key interface{})interface{}{
	session :=sessions.Default(context)
	return session.Get(key)
}
