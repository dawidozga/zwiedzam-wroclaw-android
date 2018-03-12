package com.wrocapp.zwiedzamwroclaw;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedItem extends Fragment {

    private TextView textViewTitle;
    private TextView textViewShortDesc;
    private TextView textViewLongDesc;
    private ImageView imageViewBigImageURL;
    private FloatingActionButton navigateButton;

    private static final String GET_TITLE = "GET_TITLE";
    private static final String GET_SHORT_DESC = "GET_SHORT_DESC";
    private static final String GET_LONG_DESC = "GET_LONG_DESC";
    private static final String GET_BIG_IMAGE_URL = "GET_BIG_IMAGE_URL";
    private static final String GET_SZEROKOSC = "GET_SZEROKOSC";
    private static final String GET_DLUGOSC = "GET_DLUGOSC";

    public SelectedItem() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_selected_item, container, false);

        Bundle bundle = this.getArguments();
        final String title = bundle.getString(GET_TITLE);
        String shortDesc = bundle.getString(GET_SHORT_DESC);
        String longDesc = bundle.getString(GET_LONG_DESC);
        String bigImageURL = bundle.getString(GET_BIG_IMAGE_URL);
        final double szerokosc = bundle.getDouble(GET_SZEROKOSC);
        final double dlugosc = bundle.getDouble(GET_DLUGOSC);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);

        imageViewBigImageURL = (ImageView) view.findViewById(R.id.selectedItemBigImageURL);
        Picasso.with(getActivity()).load(bigImageURL).into(imageViewBigImageURL);

        textViewTitle = (TextView) view.findViewById(R.id.selectedItemTitle);
        textViewTitle.setText(title);

        textViewShortDesc = (TextView) view.findViewById(R.id.selectedItemShortDesc);
        textViewShortDesc.setText(shortDesc);

        textViewLongDesc = (TextView) view.findViewById(R.id.selectedItemLongDesc);
        textViewLongDesc.setText(longDesc);

        navigateButton = (FloatingActionButton) view.findViewById(R.id.selectedItemButton);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle navigateBundle = new Bundle();
                navigateBundle.putString(GET_TITLE, title);
                navigateBundle.putDouble(GET_SZEROKOSC, szerokosc);
                navigateBundle.putDouble(GET_DLUGOSC, dlugosc);
                Navigation fragment = new Navigation();
                fragment.setArguments(navigateBundle);
                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame, fragment, "@string/mapa");
                fragmentTransaction.addToBackStack("@string/selectedItem");
                fragmentTransaction.commit();
            }
        });

        return view;


    }

}
