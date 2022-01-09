package middleware

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

func ReturnJSON(c *gin.Context) {
	c.Next()
	// 因为没有封装返回的脚手架，所以中间件直接用GetSet设置返回的JSON
	value, exists := c.Get("RETURN")
	if exists {
		c.JSON(http.StatusOK, value)
	}
}
