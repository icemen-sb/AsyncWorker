package ru.relastic.asyncworker.presenter;


import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.relastic.asyncworker.R;

public class ZViewHolderSchedule extends ZViewHolder {






    public ZViewHolderSchedule(@NonNull View itemView) {
        super(itemView);


        //<...>

    }



    @Override
    public void setData(int position, Object item, IPreserterUICallback callback) {
        //<...>



    }
    public static ZViewHolder createInstance(ViewGroup container, int position) {
        int resource = R.layout.activity_list_schedule;
        return new ZViewHolder(LayoutInflater
                .from(container.getContext())
                .inflate(resource,container,false));
    }
}
