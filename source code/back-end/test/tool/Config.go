package tool

import (
	"bufio"
	"encoding/json"
	"log"
	"os"
)

type Config struct {
	AppName       string         `json:"app_name"`
	AppMode       string         `json:"app_mode"`
	AppHost       string         `json:"app_host"`
	AppPort       string         `json:"app_port"`
	Database      DatabaseConfig `json:"database"`
	SessionBuffer string         `json:"session_buffer"`
	Redis         RedisConfig    `json:"redis"`
}

type DatabaseConfig struct {
	Driver   string `json:"driver"`
	User     string `json:"user"`
	Password string `json:"password"`
	Host     string `json:"host"`
	Port     string `json:"port"`
	DbName   string `json:"db_name"`
	Charset  string `json:"charset"`
	ShowSql  bool   `json:"show_sql"`
}

type RedisConfig struct {
	Password string `json:"password"`
	Host     string `json:"host"`
	Port     string `json:"port"`
}

var _cfg *Config = nil

// ParseConfig 解析app.json的数据
func ParseConfig(path string) (*Config, error) {
	file, err := os.Open(path)
	if err != nil {
		panic(err)
	}
	defer func(file *os.File) {
		err := file.Close()
		if err != nil {
			log.Fatal(err.Error())
		}
	}(file)

	reader := bufio.NewReader(file)
	decoder := json.NewDecoder(reader)
	err = decoder.Decode(&_cfg)
	if err != nil {
		return nil, err
	}
	return _cfg, nil
}
