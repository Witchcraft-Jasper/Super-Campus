package async
import (

	"github.com/robfig/cron"
	"log"
	"test/service"
)

// AsynchronousLoop 设置异步循环任务
func AsynchronousLoop() {
	c := cron.New()
	err := c.AddFunc("* * * * * *", func() {
		// 定时为每秒执行一次
		// 所有的异步循环回调服务都丢这个地方
		service.ClearOutdatedData(600) //为了调试方便可以设置短一点的时间
	})

	if err != nil {
		log.Fatal(err.Error())
	}
	c.Start()
}
