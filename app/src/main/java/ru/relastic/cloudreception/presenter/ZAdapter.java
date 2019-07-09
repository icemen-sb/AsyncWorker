package ru.relastic.cloudreception.presenter;

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
    private IPresenterUICallback mCallback = null;


    public ZAdapter (ZViewHolder viewholder) {
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
        if (data==null) {
            mData.clear();
        }else {
            mData = (List<Object>)data;
        }
        this.notifyDataSetChanged();
    }
    public Object getItemByPosition(int pos) {
        return mData.get(pos);
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

    public void setListener(IPresenterUICallback callback){
        mCallback = callback;
    }

    public Class getVHClass()  {
        return mViewholder.getClass();
    }

}
