package ru.relastic.asyncworker.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import javax.inject.Inject;

import ru.relastic.asyncworker.R;
import ru.relastic.asyncworker.dagger2.App;
import ru.relastic.asyncworker.repository.TransactData.ResponseData.Client;


public class Activity_Item_Person extends AppCompatActivity {
    private Button mButtonCancel;
    private Button mButtonCommit;
    private Button mButtonToSchedule;

    private EditText mEditText_firstname;
    private EditText mEditText_surname;
    private EditText mEditText_lastname;
    private EditText mEditText_phone;
    private EditText mEditText_description;

    private Client mClient = new Client();

    @Inject
    public IPreserterStarter iPreserterStarter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__item__person);
        App.getUIComponent().inject(this);
        initViews();
        initListeners();
        init();
    }
    private void initViews() {
        mButtonCancel = (Button)findViewById(R.id.person_button_top21);
        mButtonCommit = (Button)findViewById(R.id.person_button_top22);
        mButtonToSchedule = (Button)findViewById(R.id.person_button_bottom21);
        mEditText_firstname = (EditText)findViewById(R.id.person_editText22);
        mEditText_surname = (EditText)findViewById(R.id.person_editText23);
        mEditText_lastname = (EditText)findViewById(R.id.person_editText21);
        mEditText_phone = (EditText)findViewById(R.id.person_editText24);
        mEditText_description = (EditText)findViewById(R.id.person_editText25);
    }

    private void initListeners() {
        final Context context = this;
        final View.OnClickListener callback = new View.OnClickListener() {
            Intent result = new Intent();
            @Override
            public void onClick(View v) {
                switch (v.getTag().toString()) {
                    case "top1":
                        setResult(IPreserterStarter.INTENT_RESULT_CANCEL, result);
                        finish();
                        break;
                    case "top2":
                        Bundle bundle = getBundleOfViews();
                        if (bundle!=null) {
                            result.putExtra(IPreserterStarter.INTENT_BUNDLE_KEY,bundle);
                            setResult(IPreserterStarter.INTENT_RESULT_COMMIT, result);
                        } else {
                            setResult(IPreserterStarter.INTENT_RESULT_CANCEL, result);
                        }
                        finish();
                        break;

                    case "bottom1":
                        //<...> запускаем окно доступных дат расписания
                        Intent personScheduleWindow = Activity_AddByPerson
                                .getIntent(context)
                                .putExtra(IPreserterStarter.INTENT_BUNDLE_KEY,Client.toBundle(mClient));
                        startActivity(personScheduleWindow);
                        break;
                }
            }
        };
        mButtonCancel.setOnClickListener(callback);
        mButtonCommit.setOnClickListener(callback);
        mButtonToSchedule.setOnClickListener(callback);
    }

    private void init( ) {
        Bundle bundle = getIntent().getBundleExtra(IPreserterStarter.INTENT_BUNDLE_KEY);
        if (bundle==null) {
            mClient = new Client();
        } else {
            mClient = Client.fromBundle(bundle);
        }
        mEditText_firstname.setText(mClient.getFirstname());
        mEditText_surname.setText(mClient.getSurename());
        mEditText_lastname.setText(mClient.getLastname());
        mEditText_phone.setText(mClient.getPhone());
        mEditText_description.setText(mClient.getDescription());
    }
    private Bundle getBundleOfViews() {
        Bundle retval = null;
        boolean is_updated = false;
        if (!stringEquals(mClient.getFirstname(),mEditText_firstname.getText().toString())) {

            mClient.setFirstname(mEditText_firstname.getText().toString().trim());
            is_updated=true;
        }
        if (!stringEquals(mClient.getSurename(),mEditText_surname.getText().toString())) {
            mClient.setSurename(mEditText_surname.getText().toString().trim());
            is_updated=true;
        }
        if (!stringEquals(mClient.getLastname(),mEditText_lastname.getText().toString())) {
            mClient.setLastname(mEditText_lastname.getText().toString().trim());
            is_updated=true;
        }
        if (!stringEquals(mClient.getPhone(),mEditText_phone.getText().toString())) {
            mClient.setPhone(mEditText_phone.getText().toString().trim());
            is_updated=true;
        }
        if (!stringEquals(mClient.getDescription(),mEditText_description.getText().toString())) {
            mClient.setDescription(mEditText_description.getText().toString().trim());
            is_updated=true;
        }
        if (is_updated) {
            mClient.setUpdated(true);
            retval = Client.toBundle(mClient);
        }
        return retval;
    }

    public static boolean stringEquals(String s1, String s2) {
        if (s1 == null) {s1="";}
        if (s2 == null) {s2="";}
        //return ((s1.trim()).equals(s2.trim()));
        return s1.equals(s2);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, Activity_Item_Person.class);
    }
}
