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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * Created by Dawid on 07.11.2017.
 */

class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;

    private static final String GET_TITLE = "GET_TITLE";
    private static final String GET_SHORT_DESC = "GET_SHORT_DESC";
    private static final String GET_LONG_DESC = "GET_LONG_DESC";
    private static final String GET_BIG_IMAGE_URL = "GET_BIG_IMAGE_URL";
    private static final String GET_LATITUDE = "GET_SZEROKOSC";
    private static final String GET_LONGITUDE = "GET_DLUGOSC";


    CategoryAdapter(List<ListItem> listItems, FragmentActivity context) {
        this.listItems = listItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ListItem listItem = listItems.get(position);

        holder.textViewHead.setText(listItem.getTitle());
        holder.textViewDesc.setText(listItem.getShortDesc());

        Picasso.with(context).load(listItem.getSmallImageURL()).into(holder.imageViewImg);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
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
                fragmentTransaction.addToBackStack("@string/menu_glowne");
                fragmentTransaction.commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textViewHead;
        TextView textViewDesc;
        ImageView imageViewImg;
        LinearLayout linearLayout;

        ViewHolder(View itemView) {
            super(itemView);

            textViewHead = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewDesc = (TextView) itemView.findViewById(R.id.textViewDesc);
            imageViewImg = (ImageView) itemView.findViewById(R.id.imageViewImg);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }
    }
}
