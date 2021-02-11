package com.jumhuang.gatewaychat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mContext;
    private List<Message> list;
    private boolean is_user=false;

    private int TYPE_MY = 0;//我的
    private int TYPE_OTHERS = 1;//他人的

    public MyAdapter(Context context, List<Message> list, boolean is_user)
    {
        this.mContext = context;
        this.list = list;
        this.is_user = is_user;
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (list.get(position).isMine())
        {
            return TYPE_MY;
        }
        return TYPE_OTHERS;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder holder;
        if (viewType == TYPE_MY)
        {
            holder = new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.item_message_my, parent, false));
        }
        else
        {
            holder = new OthersHolder(LayoutInflater.from(mContext).inflate(R.layout.item_message_others, parent, false));
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        holder.setIsRecyclable(false);
        if (holder instanceof MyHolder)
        {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

            final Message message = list.get(position);

            ((MyHolder)holder).item_my_name.setText(message.getClientId());
            ((MyHolder)holder).item_my_content.setText(message.getMessage());
        }
        else if (holder instanceof OthersHolder)
        {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

            final Message message = list.get(position);

            ((OthersHolder)holder).item_others_name.setText(message.getClientId());
            ((OthersHolder)holder).item_others_content.setText(message.getMessage());
        }
    }

    public void setData(ArrayList<Message> dataList)
    {
        this.list = dataList;
        this.notifyDataSetChanged();
    }

    public void addData(Message dataList)
    {
        this.list.add(dataList);
        this.notifyDataSetChanged();
    }

    public void clear()
    {
        this.list.clear();
        this.notifyDataSetChanged();
    }

    public class OthersHolder extends RecyclerView.ViewHolder
    {
        ImageView item_others_avatar;
        TextView item_others_name;
        TextView item_others_content;

        public OthersHolder(final View view)
        {
            super(view);

            item_others_avatar = (ImageView)view.findViewById(R.id.item_message_others_avatar);
            item_others_name = (TextView)view.findViewById(R.id.item_message_others_name);
            item_others_content = (TextView)view.findViewById(R.id.item_message_others_content);

            view.setTag(view);

            view.setOnClickListener(new View.OnClickListener() 
                {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });
        }
    }

    class MyHolder extends RecyclerView.ViewHolder
    {
        ImageView item_my_avatar;
        TextView item_my_name;
        TextView item_my_content;

        public MyHolder(final View view)
        {
            super(view);

            item_my_avatar = (ImageView)view.findViewById(R.id.item_message_my_avatar);
            item_my_name = (TextView)view.findViewById(R.id.item_message_my_name);
            item_my_content = (TextView)view.findViewById(R.id.item_message_my_content);

            view.setTag(view);

            view.setOnClickListener(new View.OnClickListener() 
                {
                    @Override
                    public void onClick(View v)
                    {

                    }
                });
        }
    }
}
