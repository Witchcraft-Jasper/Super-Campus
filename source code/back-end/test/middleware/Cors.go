package middleware
import (
	"fmt"
	"github.com/gin-gonic/gin"
	"net/http"
	"strings"
)

func Cors() gin.HandlerFunc{
	return func(context *gin.Context) {
		method := context.Request.Method
		origin := context.Request.Header.Get("Origin")
		//参数解析
		var headerKeys []string
		for key , _ := range context.Request.Header {
			headerKeys = append(headerKeys, key)
		}
		//分割
		headerStr := strings.Join(headerKeys, ",")
		if headerStr!= ""{
			headerStr = fmt.Sprintf("access-control-allow-origin, access-control-allow-headers, %s",headerStr)
		}else {
			headerStr = "access-control-allow-origin, access-control-allow-headers"
		}
		if origin!=""{
			context.Writer.Header().Set("Access-Control-Allow-Origin","*")
			context.Header("Access-Control-Allow-Origin","*")//允许访问所有域
			context.Header("Access-Control-Allow-Methods","POST,GET,OPTIONS,PUT,DELETE,UPDATE")
			context.Header("Access-Control-Allow-Headers","Authorization,Content-Length,X-CSRF-Token,Token,Session")
			context.Header("Access-Control-Expose-Headers","content-Length,Access-Control-Allow-Origin,Access-Control-Allow-Methods")
			context.Header("Access-Control-Max-Age","172800")
			context.Header("Access-Control-Allow-Credentials","false")
			context.Set("content-type","application/json")//设置返回格式是json
		}
		if method=="OPTIONS"{
			context.JSON(http.StatusOK,"Options Request!")
		}
		//处理请求
		context.Next()
	}
}
