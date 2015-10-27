package com.example.mayank.meterforautorickshaw.fragment_two;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mayank.meterforautorickshaw.R;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends android.support.v4.app.Fragment {

        private List<Rides> rides ;
        private RecyclerView rv;

        public SecondFragment() {
        // Required empty public constructor
    }

        @Override
        public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_two, container, false);

        rv = (RecyclerView) rootView.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(rootView.getContext());
        rv.setLayoutManager(llm);


        initializeData() ;
        initializeAdapter();

        return rootView ;
    }

    private void initializeData(){
        rides = new ArrayList<>();
        rides.add(new Rides("B1 : Chatarpur", "40 Rs", R.raw.icon));
        rides.add(new Rides("Rithala : DTU ", "20 Rs", R.raw.icon));
        rides.add(new Rides("Chatarpur : B1 ", "50 Rs", R.raw.icon));
    }

    private void initializeAdapter() {
        RecycleViewAdapter adapter = new RecycleViewAdapter(rides);
        rv.setAdapter(adapter);
    }

}
