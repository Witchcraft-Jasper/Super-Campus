package main

import (
	"github.com/gin-contrib/sessions"
	"github.com/gin-contrib/sessions/redis"
	"log"
	"test/async"
	"test/router"
	"test/tool"
)

func main() {
	//解析app.json
	cfg, err := tool.ParseConfig("./config/app.json")
	if err != nil {
		panic(err.Error())
	}
	//配置数据库
	_, err = tool.OrmEngine(cfg)
	if err != nil {
		log.Printf(err.Error())
		return
	}
	// 配置Session管理器
	store,_:= redis.NewStore(10,"tcp",cfg.Redis.Host+":"+cfg.Redis.Port,cfg.Redis.Password, []byte("secret"))
	store.Options(sessions.Options{MaxAge: 3600})//过期时间设置
	// 配置路由
	app := router.SetRoute(store)
	// 设置定时任务
	async.AsynchronousLoop()
	err = tool.Init("./config/fileconfig.conf")
	if err != nil {
		return
	}

	// GO!
	err = app.Run(cfg.AppHost + ":" + cfg.AppPort)
	if err != nil {
		log.Printf(err.Error())
		return
	}


}
