package ru.relastic.asyncworker.presenter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class ZViewHolder extends RecyclerView.ViewHolder {


    ZViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setData(int position, Object item, IPreserterUICallback callback){  }

    public static ZViewHolder createInstance(ViewGroup container) {
        return new ZViewHolder(new View(container.getContext()));
    }
    public static String NoNull(String text) {
        return (text!=null) ? text : "" ;
    }
}
