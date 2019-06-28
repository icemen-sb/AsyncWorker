package ru.relastic.cloudreception.presenter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ru.relastic.cloudreception.R;
import ru.relastic.cloudreception.repository.TransactData.ResponseData.Client;

public class ZViewHolderClients extends ZViewHolder {
    //public static int EVENT_BUTTON_SELECT_PERSONE = 1;
    //private final View mLayout;
    private final TextView mTextViewNumeric;
    private final TextView mTextViewFIO;
    private final TextView mTextViewNote;
    private final TextView[] mTextGroup;//= {mTextViewNumeric,mTextViewFIO,mTextViewNote};
    private final Button mButtonNext;

    //private int mPosition;
    private Client mClient;
    private IPreserterUICallback mCallback;

    public ZViewHolderClients(@NonNull View itemView) {
        super(itemView);
        //mLayout = (View) itemView.findViewById(R.id.rv_person_layout);
        mTextViewNumeric = itemView.findViewById(R.id.rv_person_textview1);
        mTextViewFIO = itemView.findViewById(R.id.rv_person_textview2);
        mTextViewNote = itemView.findViewById(R.id.rv_person_textview3);
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
    }


    @Override
    public void setData(int position, final Object item, final IPreserterUICallback callback) {
        //((Activity)itemView.getContext()).unregisterForContextMenu(itemView);
        mClient = (Client)item;
        mClient.setPosition(position);
        setBold(mTextGroup, mClient.getNotified());
        mCallback = callback;
        mTextViewNumeric.setText(String.valueOf(position+1));
        mTextViewFIO.setText(mClient.getFullName());
        mTextViewNote.setText(NoNull(mClient.getDescription()));
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_UICALLBACK,item);
            }
        });
        itemView.setTag(mClient);
        ((Activity)itemView.getContext()).registerForContextMenu(itemView);
    }

    public static ZViewHolder createInstance(ViewGroup container) {
        int resource = R.layout.rv_item_person;
        return new ZViewHolderClients (LayoutInflater
                .from(container.getContext())
                .inflate(resource,container,false));
    }

}
