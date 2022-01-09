package com.example.campusapp.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.campusapp.R;
import com.example.campusapp.activity.MyMessageActivity;
import com.example.campusapp.entity.User;
import com.fashare.stack_layout.StackLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class CardAdapter extends StackLayout.Adapter<CardAdapter.H> {
    static List<Integer> sRandomColors = new ArrayList<>();
    Context context;
    Uri phone = Uri.parse("tel:13579151375");
    static {
        for (int i = 0; i < 100; i++) {
            sRandomColors.add(new Random().nextInt() | 0xff000000);
        }
    }

    public List<User> mdata;

    public CardAdapter(Context context, List<User> data) {
        this.context = context;
        this.mdata = data;
    }

    @Override
    public H onCreateViewHolder(ViewGroup parent, int position) {
        return new H(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false));
    }

    @Override
    public void onBindViewHolder(H holder, int position) {
        Glide.with(context)
                .load(mdata.get(position).getImageurl())
                .error(R.mipmap.default_avatar)
                .into(holder.imageView);
        holder.mTextView.setText(mdata.get(position).getRealname());
        holder.itemView.findViewById(R.id.layout_content).setBackgroundColor(sRandomColors.get(position % sRandomColors.size()));
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent t = new Intent(context, MyMessageActivity.class);
                t.putExtra("userid", mdata.get(position).getId());
                t.putExtra("type", 2);
                context.startActivity(t);
                Toast.makeText(v.getContext(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });
        holder.contButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(phone);
                context.startActivity(dialIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    public class H extends StackLayout.ViewHolder {
        ImageView imageView;
        TextView mTextView;
        Button button;
        Button contButton;

        public H(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            mTextView = (TextView) itemView.findViewById(R.id.tv);
            button = (Button) itemView.findViewById(R.id.buttonGetBlog);
            contButton = (Button)itemView.findViewById(R.id.buttonContact);
        }
    }
}
