package ru.relastic.asyncworker.presenter;


import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ru.relastic.asyncworker.R;
import ru.relastic.asyncworker.repository.TransactData.ResponseData.*;


public class ZViewHolderIncomingCalls extends ZViewHolder {
    public static int EVENT_BUTTON_ADD_PERSONE = 1;
    public static int EVENT_BUTTON_REMOVE_ITEM = 2;
    private TextView mTextViewNumeric;
    private TextView mTextViewPhone;
    private TextView mTextViewDate;
    private Button mButtonAdd;

    private int mPosition;
    private IncomingCall mIncomingCall;
    private IPreserterUICallback mCallback;

    public ZViewHolderIncomingCalls(@NonNull View itemView) {
        super(itemView);
        mTextViewNumeric = (TextView) itemView.findViewById(R.id.rv_calls_textview1);
        mTextViewPhone = (TextView) itemView.findViewById(R.id.rv_calls_textview2);
        mTextViewDate = (TextView) itemView.findViewById(R.id.rv_calls_textview3);
        mButtonAdd = (Button) itemView.findViewById(R.id.rv_calls_button1);
    }


    @Override
    public void setData(int position, final Object item, final IPreserterUICallback callback) {
        IncomingCall call = (IncomingCall)item;
        mPosition = position;
        mIncomingCall = (IncomingCall) item;
        mCallback = callback;
        mTextViewNumeric.setText(String.valueOf(position+1));
        mTextViewPhone.setText(NoNull(call.getPhone()));
        mTextViewDate.setText(call.getDateCallingFormat());
        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_UICALLBACK,item);
            }
        });
    }
    public static ZViewHolder createInstance(ViewGroup container) {
        int resource = R.layout.rv_item_incoming_call;
        return new ZViewHolderIncomingCalls(LayoutInflater
                .from(container.getContext())
                .inflate(resource,container,false));
    }
}
