package ru.relastic.cloudreception.presenter;


import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.relastic.cloudreception.R;

public class ZViewHolderSchedule extends ZViewHolder {

    public ZViewHolderSchedule(@NonNull View itemView) {
        super(itemView);


        //<...>

    }



    @Override
    public void setData(int position, Object item, IPresenterUICallback callback) {
        //<...>



    }
    public static ZViewHolder createInstance(ViewGroup container, int position) {
        int resource = R.layout.activity_list_schedule;
        return new ZViewHolder(LayoutInflater
                .from(container.getContext())
                .inflate(resource,container,false));
    }
}
