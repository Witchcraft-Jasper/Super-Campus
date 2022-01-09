package model

type Comment struct {
	CommentId int `xorm:"pk autoincr" json:"commentId"`
	CreateTime string `json:"createTime"`
	CommentContent string `json:"commentContent"`
	MessageId int `json:"messageId"`
	CommentUserId int `json:"commentUserId"`
	ReplyUserId int `json:"replyUserId"`
}
