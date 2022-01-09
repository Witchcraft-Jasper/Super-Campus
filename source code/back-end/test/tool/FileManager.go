package tool

import (
	"bufio"
	"github.com/tedcy/fdfs_client"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
	"io"
	"log"
	"os"
	"runtime"
	"strings"
	"test/model"
)
var Manager FileManager

type FileManager struct {
	*gorm.DB
	configFilePath string
}

type File struct {
	FileId string
	OwnerName string	`gorm:"primaryKey"`
	Filename string		`gorm:"primaryKey"`
}

func Init(configFilePath string) error {
	f, err := os.Open(configFilePath)
	if err != nil {
		return err
	}
	splitFlag := "\n"
	if runtime.GOOS == "windows" {
		splitFlag = "\r\n"
	}
	reader := bufio.NewReader(f)
	var dbUsername string
	var dbPassword string
	var dbHost string
	var dbPort string
	var dbName string
	for {
		line, err := reader.ReadString('\n')
		line = strings.TrimSuffix(line, splitFlag)
		str := strings.SplitN(line, "=", 2)
		switch str[0] {
		case "tracker_server":
			continue
		case "maxConns":
			continue
		case "dbUsername":
			dbUsername = str[1]
		case "dbPassword":
			dbPassword = str[1]
		case "dbHost":
			dbHost = str[1]
		case "dbPort":
			dbPort = str[1]
		case "dbName":
			dbName = str[1]
		default:
		}
		if err != nil {
			if err == io.EOF {
				conn := dbUsername+":"+dbPassword+"@tcp("+dbHost+":"+dbPort+")/"+dbName+"?charset=utf8mb4&parseTime=True&loc=Local"
				Manager.configFilePath = configFilePath
				Manager.DB, err = gorm.Open(mysql.Open(conn))
				if err != nil {
					return err
				}
				return nil
			}
			return err
		}
	}
}

func (fm *FileManager) UpdateAvatar(userId int, filename string) error {
	client, err := fdfs_client.NewClientWithConfig(fm.configFilePath)
	defer client.Destory()
	if err != nil {
		log.Fatal(err.Error())
		return err
	}
	fileID, err := client.UploadByFilename(filename)
	fm.DB.Model(&model.Users{}).Where("id = ?", userId).Update("avatar", "http://120.53.118.249/" + fileID)
	if err != nil {
		log.Println(err.Error())
		return err
	}
	return nil
}

func (fm *FileManager) InsertPicture(messageId int, filename string) error {
	client, err := fdfs_client.NewClientWithConfig(fm.configFilePath)
	defer client.Destory()
	if err != nil {
		log.Fatal(err.Error())
		return err
	}
	fileID, err := client.UploadByFilename(filename)
	fm.DB.Create(&model.Picture{
		MessageId: messageId,
		Url:       "http://120.53.118.249/" + fileID,
	})
	if err != nil {
		log.Println(err.Error())
		return err
	}
	return nil
}

func (fm *FileManager) GetPictureList(massageId int) ([]string,error) {
	var urls []model.Picture
	fm.DB.Where("message_id = ?", massageId).Find(&urls)
	var filenames []string
	for _, url := range urls {
		filenames = append(filenames, url.Url)
	}
	return filenames, nil
}

func (fm *FileManager) GetDownloadFileURL(OwnerName string, filename string) (string, error) {
	client, err := fdfs_client.NewClientWithConfig(fm.configFilePath)
	if err != nil {
		log.Fatal(err.Error())
		return "", err
	}
	defer client.Destory()
	var file File
	fm.DB.Where("owner_name = ? AND filename = ?", OwnerName, filename).First(&file)
	if file.FileId == "" {
		return "", nil
	}
	return "http://120.53.118.249/" + file.FileId, nil
}

func (fm *FileManager) DeleteFile(OwnerName string, filename string) error {
	client, err := fdfs_client.NewClientWithConfig(fm.configFilePath)
	defer client.Destory()
	if err != nil {
		log.Fatal(err.Error())
		return err
	}
	var file File
	fm.DB.Where("owner_name = ? AND filename = ?", OwnerName, filename).First(&file)
	fm.DB.Where("owner_name = ? AND filename = ?", OwnerName, filename).Delete(&file)
	log.Println(file.FileId)
	if file.FileId == "" {
		log.Fatal("No such file!")
		return nil
	}
	err = client.DeleteFile(file.FileId)
	if err != nil {
		return err
	}
	return nil
}