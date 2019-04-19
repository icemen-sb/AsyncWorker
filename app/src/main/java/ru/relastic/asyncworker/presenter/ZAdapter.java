package ru.relastic.asyncworker.presenter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


public class ZAdapter extends RecyclerView.Adapter<ZViewHolder> {

    private List<Object> mData = new ArrayList<>();
    private final ZViewHolder mViewholder;
    private final Method newInstance;
    private IPreserterUICallback mCallback = null;


    public ZAdapter (ZViewHolder viewholder) {
        this.mData = mData;
        mViewholder = viewholder;
        Method method = null;
        Method[] list = mViewholder.getClass().getDeclaredMethods();
        for (Method m: list) {
            if (m.getName().equals("createInstance")) {
                method = m;
                break;
            }
        }
        newInstance = method;
    }
    public void setData(Object data){
        mData = (List<Object>)data;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ZViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ZViewHolder retVal = null;
        Object[] args = {viewGroup};
        try {
            retVal = (ZViewHolder)newInstance.invoke(mViewholder,args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return retVal;
    }

    @Override
    public void onBindViewHolder(@NonNull ZViewHolder zViewHolder, int i) {
        zViewHolder.setData(i, mData.get(i), mCallback);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setListener(IPreserterUICallback callback){
        mCallback = callback;
    }

    public Class getVHClass()  {
        return mViewholder.getClass();
    }

}
