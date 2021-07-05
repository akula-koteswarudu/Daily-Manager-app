package com.koti.dailymanager;

import android.content.Context;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<Upload>  {

    //1. constructor
    //2. View holder inner class
    //3. override 2 methods getView() and getCount()
    private ArrayList<Upload> items;
    private Context context;
    private int layout;
    private OnItemClickListener mlistener;


    public MyAdapter(Context context, int layout, ArrayList<Upload> items){
        super(context,layout,items);
        this.items=items;
        this.context=context;
        this.layout=layout;
    }

    public void update(ArrayList<Upload> results){
        items= new ArrayList<Upload>();
        items.addAll(results);
        notifyDataSetChanged();
    }

    public class ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {
        TextView name;
        TextView quantity;
        TextView note;
        TextView expiry;
        ImageView image;
        ImageButton delete;
        ImageButton edit;
        ImageButton share;
        int position;

        public void setOnClickListeners(){
            delete.setOnClickListener(this);
            edit.setOnCreateContextMenuListener(this);
            edit.setOnClickListener(this);
            share.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.waste){
                mlistener.deleteItemClick(position);
            }
            else if(view.getId()==R.id.edit){
                edit.performLongClick();
            }
            else if(view.getId()==R.id.share){
                mlistener.shareItemClick(position);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select Action");
            MenuItem editQuantity= contextMenu.add(Menu.NONE,1,1,"change quantity");
            editQuantity.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            mlistener.optionsItemClick(position);
            return false;
        }
    }



    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        row= convertView;
        ViewHolder viewHolder;


        if(row==null){
            LayoutInflater layoutInflater= LayoutInflater.from(context);
            row= layoutInflater.inflate(layout,parent,false);
            viewHolder= new ViewHolder();
            viewHolder.name= row.findViewById(R.id.name);
            viewHolder.quantity = row.findViewById(R.id.itemQuantity);
            viewHolder.note = row.findViewById(R.id.note);
            viewHolder.expiry = row.findViewById(R.id.date);
            viewHolder.delete = row.findViewById(R.id.waste);
            viewHolder.edit = row.findViewById(R.id.edit);
            viewHolder.image = row.findViewById(R.id.image);
            viewHolder.share= row.findViewById(R.id.share);

            row.setTag(viewHolder);
        }
        else{
            viewHolder= (ViewHolder) row.getTag();
        }
        viewHolder.name.setText(items.get(position).getName());
        viewHolder.quantity.setText(items.get(position).getQuantity());
        viewHolder.note.setText(items.get(position).getNote());
        viewHolder.expiry.setText(items.get(position).getExpiryDate());
        Picasso.with(context).load(items.get(position).getImageUrl())
                .placeholder(R.drawable.default_image)
                .fit()
                .centerCrop()
                .into(viewHolder.image);
        viewHolder.setOnClickListeners();
        viewHolder.position=position;


        return  row;

    }
    public interface OnItemClickListener{
        void deleteItemClick(int position);
        void optionsItemClick(int position);
        void shareItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mlistener = listener;
    }

}
