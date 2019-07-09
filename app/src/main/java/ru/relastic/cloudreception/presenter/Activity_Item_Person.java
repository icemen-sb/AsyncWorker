package ru.relastic.cloudreception.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ru.relastic.cloudreception.R;
import ru.relastic.cloudreception.repository.TransactData.ResponseData.Client;


public class Activity_Item_Person extends AppCompatActivity {
    private View mButtonCancel;
    private View mButtonCommit;
    private View mButtonToSchedule;

    private EditText mEditText_firstname;
    private EditText mEditText_surname;
    private EditText mEditText_lastname;
    private EditText mEditText_phone;
    private EditText mEditText_description;
    private TextView mTitle;

    private Client mClient = new Client();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_person);
        initViews();
        initListeners();
        init();
    }
    private void initViews() {
        mButtonCancel = findViewById(R.id.person_button_top21);
        mButtonCommit = findViewById(R.id.person_button_top22);
        mButtonToSchedule = findViewById(R.id.person_button_bottom21);
        mEditText_firstname = findViewById(R.id.person_editText22);
        mEditText_surname = findViewById(R.id.person_editText23);
        mEditText_lastname = findViewById(R.id.person_editText21);
        mEditText_phone = findViewById(R.id.person_editText24);
        mEditText_description = findViewById(R.id.person_editText25);
        mTitle = findViewById(R.id.person_title2);
    }

    private void initListeners() {
        final Context context = this;
        final View.OnClickListener callback = new View.OnClickListener() {
            Intent result = new Intent();
            @Override
            public void onClick(View v) {
                switch (v.getTag().toString()) {
                    case "top1":
                        if (mClient.getNotified()) {
                            mClient.setNotified(false);
                            Bundle bundle = Client.toBundle(mClient);
                            result.putExtra(IPreserterStarter.INTENT_BUNDLE_KEY,bundle);
                            setResult(IPreserterStarter.INTENT_RESULT_COMMIT, result);
                        } else {
                            setResult(IPreserterStarter.INTENT_RESULT_CANCEL, result);
                        }
                        finish();
                        break;
                    case "top2":
                        if (mEditText_lastname.getText().toString().trim().length()==0 ||
                                mEditText_phone.getText().toString().trim().length()==0) {

                            Toast toast = Toast.makeText(Activity_Item_Person.this,
                                    getResources().getString(R.string.required_fields_has_not_filled),
                                    Toast.LENGTH_LONG);
                            toast.getView().setBackground(createRoundedRectByColor(getResources()
                                    .getColor(R.color.colorAccent),null));
                            ((TextView) toast.getView().findViewById(android.R.id.message))
                                    .setTextColor(getResources().getColor(R.color.background));
                            toast.show();
                        }else {
                            Bundle bundle = getChangedClientByViews();
                            if (bundle!=null) {
                                result.putExtra(IPreserterStarter.INTENT_BUNDLE_KEY,bundle);
                                setResult(IPreserterStarter.INTENT_RESULT_COMMIT, result);
                            }else {
                                setResult(IPreserterStarter.INTENT_RESULT_CANCEL, result);
                            }
                            finish();
                        }
                        break;
                    case "bottom1":
                        //<...> запускаем окно доступных дат расписания
                        /*
                        Intent personScheduleWindow = Activity_AddByPerson
                                .getIntent(context)
                                .putExtra(IPreserterStarter.INTENT_BUNDLE_KEY,Client.toBundle(mClient));
                        startActivity(personScheduleWindow);
                        */
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
            mTitle.setText(getResources().getString(R.string.creating_client));
        } else {
            mClient = Client.fromBundle(bundle);
            mTitle.setText(getResources().getString(R.string.editing_current));
        }
        mEditText_firstname.setText(mClient.getFirstname());
        mEditText_surname.setText(mClient.getSurename());
        mEditText_lastname.setText(mClient.getLastname());
        mEditText_phone.setText(mClient.getPhone());
        mEditText_description.setText(mClient.getDescription());
    }
    private Bundle getChangedClientByViews() {
        Bundle retval = null;
        boolean is_updated = false;
        if (mClient.getNotified()) {
            mClient.setNotified(false);
            is_updated=true;
        }
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

    private static boolean stringEquals(String s1, String s2) {
        if (s1 == null) {s1="";}
        if (s2 == null) {s2="";}
        //return ((s1.trim()).equals(s2.trim()));
        return s1.equals(s2);
    }
    private static ShapeDrawable createRoundedRectByColor(@ColorInt int color, @Nullable Integer r){
        final float rad = (r == null) ? 32f : r.floatValue();
        float [] outR = new float [] {rad, rad, rad, rad, rad, rad, rad, rad};
        ShapeDrawable mBackShape = new ShapeDrawable(new RoundRectShape(outR, null, null));
        mBackShape.getPaint().setColor(color);
        mBackShape.getPaint().setAlpha(192);
        return mBackShape;
    }
    public static Intent getIntent(Context context) {
        return new Intent(context, Activity_Item_Person.class);
    }
}
