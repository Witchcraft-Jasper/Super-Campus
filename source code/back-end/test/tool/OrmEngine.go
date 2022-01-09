package tool

import (
	_ "github.com/go-sql-driver/mysql"
	"github.com/go-xorm/xorm"
	"test/model"
)

var DbEngine *Orm

type Orm struct {
	*xorm.Engine
}

// OrmEngine 根据配置信息生成ORM对象
func OrmEngine(cfg *Config) (*Orm, error) {
	database := cfg.Database
	conn := database.User + ":" + database.Password + "@tcp(" + database.Host + ":" + database.Port + ")/" + database.DbName + "?charset=" + database.Charset
	engine, err := xorm.NewEngine(database.Driver, conn)
	if err != nil {
		panic(err.Error())
	}
	engine.ShowSQL(database.ShowSql)
	// 两张表，验证码和用户
	// 这条语句只能执行一次
	err = engine.Sync2(new(model.Schedule))
	if err != nil {
		return nil, err
	}
	orm := new(Orm)
	orm.Engine = engine
	DbEngine = orm
	return orm, nil
}
