package ru.relastic.cloudreception.presenter;

import android.app.Activity;
import android.graphics.Color;
import android.icu.util.Measure;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.relastic.cloudreception.R;
import ru.relastic.cloudreception.repository.TransactData.ResponseData.Client;

public class ZViewHolderClients extends ZViewHolder {
    //public static int EVENT_BUTTON_SELECT_PERSON = 1;
    //private final View mLayout;
    private final static int DEFAULT_DISTANCE_DP = 4;

    private final TextView mTextViewNumeric;
    private final TextView mTextViewFIO;
    private final TextView mTextViewNote;
    private final TextView[] mTextGroup;//= {mTextViewNumeric,mTextViewFIO,mTextViewNote};
    private final Button mButtonNext;
    private final float k_dp_px;
    private final int mDistance;
    private final int mHeight;

    //private int mPosition;
    private Client mClient;
    private IPresenterUICallback mCallback;

    public ZViewHolderClients(@NonNull View itemView) {
        super(itemView);
        k_dp_px = itemView.getContext().getResources().getDisplayMetrics().density;
        mDistance = (int)(DEFAULT_DISTANCE_DP * k_dp_px);
        //mLayout = (View) itemView.findViewById(R.id.rv_person_layout);
        mTextViewNumeric = itemView.findViewById(R.id.rv_person_textview1);
        mTextViewFIO = itemView.findViewById(R.id.rv_person_textview2);
        mTextViewNote = itemView.findViewById(R.id.rv_person_textview3);
        //mHeight = mTextViewFIO.getHeight()+mTextViewNote.getHeight()+mDistance;
        mButtonNext = itemView.findViewById(R.id.rv_person_button1);
        mTextGroup = new TextView[]{mTextViewNumeric,mTextViewFIO,mTextViewNote};
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateSelectedItem(v);
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                animateSelectedItem(v);
                return false;
            }
        });
        itemView.measure(View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.UNSPECIFIED));
        //itemView.setBackgroundColor(Color.YELLOW);
        mHeight = itemView.getMeasuredHeight();
    }


    @Override
    public void setData(int position, final Object item, final IPresenterUICallback callback) {
        //((Activity)itemView.getContext()).unregisterForContextMenu(itemView);
        mClient = (Client)item;
        mClient.setPosition(position);
        setBold(mTextGroup, mClient.getNotified());
        mCallback = callback;
        mTextViewNumeric.setText(String.valueOf(position+1));
        mTextViewFIO.setText(mClient.getFullName());
        mTextViewNote.setText(NoNull(mClient.getDescription()));
        mTextViewNote.setVisibility(
                NoNull(mClient.getDescription()).length()==0 ? View.GONE : View.VISIBLE);
        ((ViewGroup.MarginLayoutParams)mTextViewFIO.getLayoutParams()).bottomMargin = (
                NoNull(mClient.getDescription()).length()==0 ? 0 : (int)(4*k_dp_px));
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_UICALLBACK,item);
            }
        });
        itemView.setTag(mClient);
        ((Activity)itemView.getContext()).registerForContextMenu(itemView);
        //((View)itemView).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
        //        (int)(mHeight2/k_dp_px)));
    }

    public static ZViewHolder createInstance(ViewGroup container) {
        int resource = R.layout.rv_item_person;
        return new ZViewHolderClients (LayoutInflater
                .from(container.getContext())
                .inflate(resource,container,false));
    }

}
