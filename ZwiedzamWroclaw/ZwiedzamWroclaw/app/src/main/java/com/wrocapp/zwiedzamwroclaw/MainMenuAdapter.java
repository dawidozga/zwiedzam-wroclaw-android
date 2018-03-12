package com.wrocapp.zwiedzamwroclaw;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by Dawid on 16.11.2017.
 */

class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;

    private static final String GET_TITLE = "GET_TITLE";
    private static final String GET_SHORT_DESC = "GET_SHORT_DESC";
    private static final String GET_LONG_DESC = "GET_LONG_DESC";
    private static final String GET_BIG_IMAGE_URL = "GET_BIG_IMAGE_URL";
    private static final String GET_LATITUDE = "GET_SZEROKOSC";
    private static final String GET_LONGITUDE = "GET_DLUGOSC";


    MainMenuAdapter(List<ListItem> listItems, FragmentActivity context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_menu_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ListItem listItem = listItems.get(position);

        holder.menuGlowneTitle.setText(listItem.getTitle());
        holder.menuGlowneShortDesc.setText(listItem.getShortDesc());

        Picasso.with(context).load(listItem.getBigImageURL()).into(holder.menuGlowneBigImageURL);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, context.getString(R.string.selected_item) + listItem.getTitle(), Toast.LENGTH_LONG).show();

                Bundle bundle = new Bundle();
                bundle.putString(GET_TITLE, listItem.getTitle());
                bundle.putString(GET_SHORT_DESC, listItem.getShortDesc());
                bundle.putString(GET_LONG_DESC, listItem.getLongDesc());
                bundle.putString(GET_BIG_IMAGE_URL, listItem.getBigImageURL());
                bundle.putDouble(GET_LATITUDE, listItem.getLatitude());
                bundle.putDouble(GET_LONGITUDE, listItem.getLongitude());

                SelectedItem fragment = new SelectedItem();

                fragment.setArguments(bundle);

                android.support.v4.app.FragmentTransaction fragmentTransaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, "@string/selectedItem");
                fragmentTransaction.addToBackStack("@string/main_menu");
                fragmentTransaction.commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView menuGlowneTitle;
        TextView menuGlowneShortDesc;
        ImageView menuGlowneBigImageURL;
        ConstraintLayout constraintLayout;

        ViewHolder(View itemView) {
            super(itemView);

            menuGlowneTitle = (TextView) itemView.findViewById(R.id.menuGlowneTitle);
            menuGlowneShortDesc = (TextView) itemView.findViewById(R.id.menuGlowneShortDesc);
            menuGlowneBigImageURL = (ImageView) itemView.findViewById(R.id.menuGlowneBigImageURL);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraintLayout);
        }
    }
}
