package com.example.simpletodo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//adapter takes data and puts it in viewholder
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
    //communicate with main activity through interface to be implemented
    //send position info
    public interface OnLongClickListener{
        void onItemLongClicked(int position);
    }
    List<String> items;
    OnLongClickListener longClickListener;
    //constructor()
    public ItemsAdapter(List<String> items, OnLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
        this.items = items;
    }

    @NonNull
    @Override
    //called by OnCreate()
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate view from existing built-in xml file
        //viewHolder  diplays list item using onbindVH()
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(todoView);
    }
    //reflect item at defined position (used for updates)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get item at p
        String item = items.get(position);
        //bind item into ViewHolder
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //container for ez access to row-view for the list
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(android.R.id.text1);
        }
        //update view insider holder with data item
        public void bind(String item) {
            tvItem.setText(item);
            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //notify listener about p of pressed
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
