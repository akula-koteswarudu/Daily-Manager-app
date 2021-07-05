package com.koti.dailymanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.koti.dailymanager.DashboardActivity.storage;
import static com.koti.dailymanager.DashboardActivity.databaseReference;


public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {

    List<DashboardItem> items;
    LayoutInflater inflater;
    Context context;

    public DashboardAdapter(List<DashboardItem> items , Context context) {
        this.items=items;
        this.context=context;
        this.inflater= LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_dashboard_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(items.get(position).getTitle());
        Picasso.with(context).load(items.get(position).getImageUrl())
                .placeholder(R.drawable.default_image)
                .fit()
                .centerCrop()
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView= itemView.findViewById(R.id.dashboardImage);
            textView= itemView.findViewById(R.id.dashboardTitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent= new Intent(view.getContext(),commonActivity.class);
                    intent.putExtra("title",items.get(getAdapterPosition()).getTitle());
                    context.startActivity(intent);

                }
            });

            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    contextMenu.setHeaderTitle("Select Action");
                    MenuItem delete= contextMenu.add(Menu.NONE,1,1,"delete");
                    delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            new AlertDialog.Builder(context)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setTitle("Are you sure!")
                                    .setMessage("do you want delete permanently?")
                                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            DashboardItem item=items.get(getAdapterPosition());
                                            String key = item.getKey();
                                            StorageReference imageRef=null;

                                            if(!item.getImageUrl().equals("https://acadianakarate.com/wp-content/uploads/2017/04/default-image.jpg")){
                                                imageRef = storage.getReferenceFromUrl(item.getImageUrl());}

                                            if(imageRef!=null){
                                                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        databaseReference.child(key).removeValue();
                                                        Toast.makeText(context,"item deleted successfully",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                            else{
                                                databaseReference.child(key).removeValue();

                                                Toast.makeText(context,"item deleted successfully",Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    })
                                    .setNegativeButton("No",null)
                                    .show();
                            return false;
                        }
                    });
                }
            });
        }
    }


}
