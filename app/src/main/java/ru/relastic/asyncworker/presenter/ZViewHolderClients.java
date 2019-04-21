package ru.relastic.asyncworker.presenter;

import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ru.relastic.asyncworker.R;
import ru.relastic.asyncworker.repository.TransactData.ResponseData.Client;

public class ZViewHolderClients extends ZViewHolder {
    public static int EVENT_BUTTON_SELECT_PERSONE = 1;
    private View mLayout;
    private TextView mTextViewNumeric;
    private TextView mTextViewFIO;
    private TextView mTextViewNote;
    private Button mButtonNext;

    private int mPosition;
    private Client mClient;
    private IPreserterUICallback mCallback;

    public ZViewHolderClients(@NonNull View itemView) {
        super(itemView);
        mLayout = (View) itemView.findViewById(R.id.rv_person_layout);
        mTextViewNumeric = (TextView) itemView.findViewById(R.id.rv_person_textview1);
        mTextViewFIO = (TextView) itemView.findViewById(R.id.rv_person_textview2);
        mTextViewNote = (TextView) itemView.findViewById(R.id.rv_person_textview3);
        mButtonNext = (Button) itemView.findViewById(R.id.rv_person_button1);
    }


    @Override
    public void setData(int position, final Object item, final IPreserterUICallback callback) {
        Client client = (Client)item;
        if (client.getNotified()) {
            mLayout.setBackgroundColor(mLayout.getResources().getColor(R.color.primary_light));
        }else {
            mLayout.setBackgroundColor(mLayout.getResources().getColor(R.color.icons));
        }

        mPosition = position;
        mClient = (Client)item;
        mCallback = callback;
        mTextViewNumeric.setText(String.valueOf(position+1));
        mTextViewFIO.setText(NoNull(client.getLastname()) + " " + NoNull(client.getFirstname()) +" " + NoNull(client.getSurename()));
        mTextViewNote.setText(NoNull(client.getDescription()));
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_UICALLBACK,item);
            }
        });
    }

    public static ZViewHolder createInstance(ViewGroup container) {
        int resource = R.layout.rv_item_person;
        return new ZViewHolderClients (LayoutInflater
                .from(container.getContext())
                .inflate(resource,container,false));
    }

}
