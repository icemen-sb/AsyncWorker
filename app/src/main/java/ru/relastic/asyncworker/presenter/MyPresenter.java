package ru.relastic.asyncworker.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    //Intent homeWindow = Activity_List_Person.getIntent(mContext);
    //Intent scheduleWindow = Activity_List_Schedule.getIntent(mContext);

    //Intent personWindow = Activity_List_Person.getIntent(mContext);
    //Intent dateWindow = Activity_Item_Schedule.getIntent(mContext);

    //Intent personeToDateWindow = Activity_AddByPerson.getIntent(mContext);
    //Intent dateToPersonWindow = Activity_AddByDate.getIntent(mContext);

    //Intent propertiesWindow = ActivityPreferences.getIntent(mContext);



    public MyPresenter (){
        App.getComponent().inject(this);
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
    public void populateData(@NonNull final ZAdapter zAdapter, int what, Object arg){

        switch (what) {
            case IPreserterStarter.WHAT_POUPULATE_PERSONE:
                mContext.bindService(mMainServiceIntent,
                        new MainServiceConnection(new IServiceConnectCallback() {
                    @Override
                    public void onStateChange(boolean isConnected,
                                              IServiceProvide service,
                                              final MainServiceConnection connection) {
                        service.requestData(mAuthData.next().setType(AuthData.AUTH_TYPE_GETALL),
                                new IDataTransactCallback() {
                            @Override
                            public void onResponseData(TransactData response) {
                                System.out.println("************* IncomingCount: "+response.getResponse_data().getCalls().size());
                                if (response != null && response.getResponse_data() != null
                                        && response.getResponse_data().getClients() != null) {
                                    zAdapter.setData(response.getResponse_data().getClients());
                                }
                                mContext.unbindService(connection);
                            }
                        },
                        true);
                    }
                    @Override
                    public void onChangeTask(int What, Object value) {}
                        }),
                        Context.BIND_AUTO_CREATE
                );
                break;
            case IPreserterStarter.WHAT_POUPULATE_NOTIFIES:

                break;
            case IPreserterStarter.WHAT_POUPULATE_NEW:

                break;
        }
    }

    @Override
    public void updateDataItem(@Nullable final ZAdapter zAdapter, int what, final Object arg) {
        switch (what) {
            case IPreserterStarter.WHAT_UPDATE_CLIENT:
                final ResponseData response_data = new ResponseData((Client)arg,
                        null,
                        null);
                mContext.bindService(mMainServiceIntent,
                        new MainServiceConnection(new IServiceConnectCallback() {
                            @Override
                            public void onStateChange(boolean isConnected,
                                                      IServiceProvide service,
                                                      final MainServiceConnection connection) {
                                service.requestData(mAuthData.next()
                                                .setType(AuthData.AUTH_TYPE_UPDATE)
                                                .setBody(response_data),
                                        new IDataTransactCallback() {
                                            @Override
                                            public void onResponseData(TransactData response) {
                                                if (zAdapter != null) {
                                                    MyPresenter.this.populateData(
                                                            zAdapter,
                                                            IPreserterStarter.WHAT_POUPULATE_PERSONE, null
                                                    );
                                                    mContext.unbindService(connection);
                                                }
                                            }
                                        },
                                        true);
                                if (zAdapter == null) {
                                    mContext.unbindService(connection);
                                }

                            }

                            @Override
                            public void onChangeTask(int What, Object value) {}
                        }),
                        Context.BIND_AUTO_CREATE
                );
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
                mContext.bindService(mMainServiceIntent,
                        new MainServiceConnection(new IServiceConnectCallback() {
                            @Override
                            public void onStateChange(boolean isConnected,
                                                      IServiceProvide service,
                                                      final MainServiceConnection connection) {
                                service.requestData(mAuthData.next().setType(AuthData.AUTH_TYPE_INSERT).setBody(response_data),
                                        new IDataTransactCallback() {
                                            @Override
                                            public void onResponseData(TransactData response) {
                                                if (zAdapter != null) {
                                                    MyPresenter.this.populateData(
                                                            zAdapter,
                                                            IPreserterStarter.WHAT_POUPULATE_PERSONE, null
                                                    );
                                                    mContext.unbindService(connection);
                                                }
                                            }
                                        },
                                        true);
                                if (zAdapter == null) {
                                    mContext.unbindService(connection);
                                }
                            }
                            @Override
                            public void onChangeTask(int What, Object value) {}
                        }),
                        Context.BIND_AUTO_CREATE
                );
                break;
            case IPreserterStarter.WHAT_UPDATE_SCHEDULE_ITEM:

                break;
            case IPreserterStarter.WHAT_UPDATE_NEWCALLS:

                break;
        }
    }
}
