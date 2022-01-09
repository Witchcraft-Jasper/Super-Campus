package com.example.campusapp.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.campusapp.R;
import com.example.campusapp.entity.Comment;
import com.example.campusapp.entity.User;

import java.util.List;


public class CommentListViewAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<Comment> comments;
    MessageListViewAdpater.OnClickListener onClickListener;
    int messagePosition;
    User user;

    public CommentListViewAdapter(User user, int messagePosition, Context context, List<Comment> comments, MessageListViewAdpater.OnClickListener onClickListener) {
        this.context = context;
        this.comments = comments;
        layoutInflater = LayoutInflater.from(context);
        this.onClickListener = onClickListener;
        this.messagePosition = messagePosition;
        this.user = user;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.comment_item, null);
        TextView tv_comment = (TextView) convertView.findViewById(R.id.tv_comment);
        TextView tv_comment_delete = (TextView) convertView.findViewById(R.id.tv_comment_delete);
        final Comment comment = comments.get(position);
        String commentStr = "";
        if (comment.getReplyUserId() != 0) {
            commentStr = commentStr + comment.getReply_name() + " 回复 ";
            if (comment.getComment_name() != null) {
                commentStr = commentStr + comment.getComment_name() + "：";
            }
        } else {
            if (comment.getComment_name() != null) {
                commentStr = commentStr + comment.getComment_name() + "：";
            }else{
                commentStr = commentStr + "用户A：";
            }
        }
        commentStr = commentStr + comment.getCommentContent();
        tv_comment.setText(commentStr);
        if (comment.getCommentUserId() == user.getId()) {
            tv_comment_delete.setVisibility(View.VISIBLE);
        } else {
            tv_comment_delete.setVisibility(View.INVISIBLE);
        }
        tv_comment_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onDeleteCommentClick(comment.getCommentId(), messagePosition, position);
            }
        });
        return convertView;
    }

}
