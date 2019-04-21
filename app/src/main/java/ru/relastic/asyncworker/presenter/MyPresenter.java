package ru.relastic.asyncworker.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import javax.inject.Inject;
import ru.relastic.asyncworker.dagger2.App;
import ru.relastic.asyncworker.domain.IServiceConnectCallback;
import ru.relastic.asyncworker.domain.IServiceProvide;
import ru.relastic.asyncworker.domain.MainServiceConnection;
import ru.relastic.asyncworker.repository.AuthData;
import ru.relastic.asyncworker.repository.IDataTransactCallback;
import ru.relastic.asyncworker.repository.TransactData;
import ru.relastic.asyncworker.repository.TransactData.*;
import ru.relastic.asyncworker.repository.TransactData.ResponseData.*;


public class MyPresenter  implements IPreserterStarter {
    @Inject
    Context mContext;
    @Inject
    public Intent mMainServiceIntent;
    @Inject
    public AuthData mAuthData;

    private ArrayList<IPreserterUICallback> mListeners = new ArrayList<>();

    //Intent homeWindow = Activity_List_Person.getIntent(mContext);
    //Intent scheduleWindow = Activity_List_Schedule.getIntent(mContext);

    //Intent personWindow = Activity_List_Person.getIntent(mContext);
    //Intent dateWindow = Activity_Item_Schedule.getIntent(mContext);

    //Intent personeToDateWindow = Activity_AddByPerson.getIntent(mContext);
    //Intent dateToPersonWindow = Activity_AddByDate.getIntent(mContext);

    //Intent propertiesWindow = ActivityPreferences.getIntent(mContext);

    private IServiceProvide mService;
    private IServiceConnectCallback mIServiceConnectCallback = new IServiceConnectCallback() {
        @Override
        public void onStateChange(boolean isConnected,
                                  IServiceProvide service,
                                  final MainServiceConnection connection) {
            if (isConnected) {
                mService = service;
                mService.addListener(this);
            } else {
                mService = null;
            }
        }
        @Override
        public void onChangeTask(int what, Object value) {
            //Обработка внешнего события
            if (what == IServiceConnectCallback.WHAT_SERVICE_EVENT_UDDATED_DATA) {
                for (IPreserterUICallback callback : mListeners) {
                    callback.onOccurredEvent(IPreserterStarter.EVENT_WHAT_OTHER, value);
                }

            }
        }
    };
    private MainServiceConnection mainServiceConnection = new MainServiceConnection(mIServiceConnectCallback);

    public MyPresenter (){
        App.getComponent().inject(this);
        //Подключаемся к сервису
        mContext.bindService(mMainServiceIntent,mainServiceConnection,Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void finalize() throws Throwable {
        mService.removeListener(mIServiceConnectCallback);
    }

    @Override
    public void startUI(IncomingCall incomingCall) {
        //Синхронизируем...
        //<...>
        if (false) {
        mContext.bindService(mMainServiceIntent, new MainServiceConnection(new IServiceConnectCallback() {
            @Override
            public void onStateChange(boolean isConnected, final IServiceProvide service, MainServiceConnection connection) {
                service.requestData(mAuthData.next().setType(200), new IDataTransactCallback() {
                    @Override
                    public void onResponseData(TransactData response) {
                        System.out.println("************Данные c cthdthf:"+response.getResponse_data().getClients().size());
                        if (response.getResponse_msg().getCode()==0) {
                            service.requestData(mAuthData.setType(AuthData.AUTH_TYPE_UPDATE).setBody(response.getResponse_data()),
                                    new IDataTransactCallback() {
                                @Override
                                public void onResponseData(TransactData response) {
                                    System.out.println("************Данные скачаны");
                                }
                            },true) ;
                        }
                    }
                },false);
            }

            @Override
            public void onChangeTask(int What, Object value) {

            }
        }),Context.BIND_AUTO_CREATE);
        }


        Intent homeActivity = Activity_List_Person.getIntent(mContext);
        mContext.startActivity(homeActivity);
    }

    @Override
    public void populateData(@NonNull final ZAdapter adapter, int what, Object arg){
        switch (what) {
            case IPreserterStarter.WHAT_POUPULATE_PERSONE:
                mService.requestData(
                        mAuthData.next().setType(AuthData.AUTH_TYPE_GETALL),
                        new IDataTransactCallback() {
                            @Override
                            public void onResponseData(TransactData response) {
                                if (response != null && response.getResponse_data() != null
                                                    && response.getResponse_data().getClients() != null) {
                                    adapter.setData(response.getResponse_data().getClients());
                                }
                            }},
                        true);
                break;
            case IPreserterStarter.WHAT_POUPULATE_NOTIFIES:
                mService.requestData(
                        mAuthData.next().setType(AuthData.AUTH_TYPE_GETALL_NITUFIED),
                        new IDataTransactCallback() {
                            @Override
                            public void onResponseData(TransactData response) {
                                if (response != null && response.getResponse_data() != null
                                        && response.getResponse_data().getClients() != null) {
                                    adapter.setData(response.getResponse_data().getClients());
                                }
                            }},
                        true);
                break;
            case IPreserterStarter.WHAT_POUPULATE_NEW:
                mService.requestData(
                        mAuthData.next().setType(AuthData.AUTH_TYPE_GETALL_NEWES),
                        new IDataTransactCallback() {
                            @Override
                            public void onResponseData(TransactData response) {
                                if (response != null && response.getResponse_data() != null
                                        && response.getResponse_data().getCalls() != null) {
                                    adapter.setData(response.getResponse_data().getCalls());
                                }
                            }},
                        true);
                break;
        }
    }

    @Override
    public void updateDataItem(@Nullable final ZAdapter adapter, int what, final Object arg) {
        switch (what) {
            case IPreserterStarter.WHAT_UPDATE_CLIENT:
                final ResponseData response_data = new ResponseData((Client)arg,
                        null,
                        null);
                mService.requestData(mAuthData.next().setType(
                        AuthData.AUTH_TYPE_UPDATE).setBody(response_data),
                        null,
                        true);
                /*
                mService.requestData(mAuthData.next().setType(
                        AuthData.AUTH_TYPE_UPDATE).setBody(response_data),
                        new IDataTransactCallback() {
                            @Override
                            public void onResponseData(TransactData response) {
                                if (zAdapter != null) {
                                    MyPresenter.this.populateData(zAdapter,
                                            IPreserterStarter.WHAT_POUPULATE_PERSONE,
                                            null);
                                }
                            }
                        },
                        true);
                */
                break;
            case IPreserterStarter.WHAT_UPDATE_SCHEDULE_ITEM:

                break;
            case IPreserterStarter.WHAT_UPDATE_NEWCALLS:

                break;
        }
    }

    @Override
    public void insertDataItem(@Nullable final ZAdapter zAdapter, int what, final Object arg) {
        switch (what) {
            case IPreserterStarter.WHAT_INSERT_CLIENT:
                final ResponseData response_data = new ResponseData((Client)arg,
                        null,
                        null);
                mService.requestData(
                        mAuthData.next().setType(AuthData.AUTH_TYPE_INSERT).setBody(response_data),
                        null,
                        true);
                /*
                mService.requestData(
                        mAuthData.next().setType(AuthData.AUTH_TYPE_INSERT).setBody(response_data),
                        new IDataTransactCallback() {
                            @Override
                            public void onResponseData(TransactData response) {
                                if (zAdapter != null) {
                                    MyPresenter.this.populateData(
                                            zAdapter,
                                            IPreserterStarter.WHAT_POUPULATE_PERSONE,
                                            null);
                                }
                            }
                        },
                        true);
                */
                break;
            case IPreserterStarter.WHAT_UPDATE_SCHEDULE_ITEM:

                break;
            case IPreserterStarter.WHAT_UPDATE_NEWCALLS:

                break;
        }
    }


    @Override
    public void addListener(IPreserterUICallback iPreserterUICallback) {
        if (!mListeners.contains(iPreserterUICallback)) {mListeners.add(iPreserterUICallback);}
    }
    @Override
    public void removeListener(IPreserterUICallback iPreserterUICallback) {
        mListeners.remove(iPreserterUICallback);
    }
}
