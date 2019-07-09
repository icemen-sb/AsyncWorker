package ru.relastic.cloudreception.presenter;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ru.relastic.cloudreception.R;
import ru.relastic.cloudreception.repository.TransactData.ResponseData.*;


public class ZViewHolderIncomingCalls extends ZViewHolder {
    public static int EVENT_BUTTON_ADD_PERSONE = 1;
    public static int EVENT_BUTTON_REMOVE_ITEM = 2;
    private final View mLayout;
    private final TextView mTextViewNumeric;
    private final TextView mTextViewPhone;
    private final TextView mTextViewDate;
    private final Button mButtonAdd;

    private int mPosition;
    private IncomingCall mIncomingCall;
    private IPresenterUICallback mCallback;

    public ZViewHolderIncomingCalls(@NonNull View itemView) {
        super(itemView);
        mLayout = itemView.findViewById(R.id.rv_calls_layout);
        mTextViewNumeric = itemView.findViewById(R.id.rv_calls_textview1);
        mTextViewPhone = itemView.findViewById(R.id.rv_calls_textview2);
        mTextViewDate = itemView.findViewById(R.id.rv_calls_textview3);
        mButtonAdd = itemView.findViewById(R.id.rv_calls_button1);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateSelectedItem(v);
            }
        });
    }


    @Override
    public void setData(int position, final Object item, final IPresenterUICallback callback) {
        mPosition = position;
        mIncomingCall = (IncomingCall) item;
        mIncomingCall.setPosition(position);
        mCallback = callback;
        mTextViewNumeric.setText(String.valueOf(position+1));
        mTextViewPhone.setText(NoNull(mIncomingCall.getPhone()));
        mTextViewDate.setText(mIncomingCall.getDateCallingFormat());
        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_UICALLBACK,item);
            }
        });
        itemView.setTag(mIncomingCall);
        ((Activity)mLayout.getContext()).registerForContextMenu(mLayout);
    }
    public static ZViewHolder createInstance(ViewGroup container) {
        int resource = R.layout.rv_item_incoming_call;
        return new ZViewHolderIncomingCalls(LayoutInflater
                .from(container.getContext())
                .inflate(resource,container,false));
    }
}
