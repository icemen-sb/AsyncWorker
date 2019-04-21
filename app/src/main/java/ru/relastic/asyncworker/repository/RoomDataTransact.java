package ru.relastic.asyncworker.repository;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import android.arch.persistence.room.Query;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import java.util.List;
import ru.relastic.asyncworker.repository.TransactData.*;
import ru.relastic.asyncworker.repository.TransactData.ResponseData.*;

public class RoomDataTransact extends AsyncTask<AuthData,Integer,TransactData>implements DataTransact {
    public static final String DB_NAME = "database.db";
    public static final int VERSION_DB = 5;

    private final AsyncWorkerDatabase mAsyncWorkerDatabase;
    private IDataTransactCallback mCallback;

    private ClientsDAO mClientsDAO;
    private ScheduleDAO mScheduleDAO;
    private IncomingCallsDAO mIncomingCallDAO;

    public RoomDataTransact(Context context) {
        Log.v("DBManager:","Creatind instatce of DBManagerRoom class ...");
        mAsyncWorkerDatabase = Room.databaseBuilder(context, AsyncWorkerDatabase.class, DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        if (mAsyncWorkerDatabase != null) {
            Log.v("DBManager:","Created instatce of DBManagerRoom class.");
        }else {
            Log.v("DBManager: ERROR","ERROR creating instatce of DBManagerRoom class.");
        }
    }
    private RoomDataTransact(AsyncWorkerDatabase asyncWorkerDatabase, IDataTransactCallback callback, AuthData auth) {
        this.mAsyncWorkerDatabase = asyncWorkerDatabase;
        this.mClientsDAO = mAsyncWorkerDatabase.getClientsDAO();
        this.mScheduleDAO = mAsyncWorkerDatabase.getScheduleDAO();
        this.mIncomingCallDAO = mAsyncWorkerDatabase.getIncomingCallsDAO();
        this.mCallback = callback;

        this.execute(auth);
    }

    @Override
    public void requestData(AuthData data_request, IDataTransactCallback callback) {
        this.enqueue(this.mAsyncWorkerDatabase,callback,data_request);
    }
    @Override
    protected TransactData doInBackground(AuthData... authData) {
        ResponseData data = null;

        List<Client> clients = null;
        List<ScheduleItem> schedule = null;
        List<IncomingCall> calls = null;

        //Обрабарываем запрос и используя DAO подготавливаем данные
        switch (authData[0].getType()) {
            case 200: //все
                clients = mClientsDAO.getClients();
                schedule = mScheduleDAO.getSchedule();
                calls = mIncomingCallDAO.getCalls();
                data = new ResponseData(clients,schedule,calls);
                break;
            case 201: //по id клиента
                clients = mClientsDAO.getClientsById(authData[0].getArg());
                schedule = mScheduleDAO.getScheduleByIdClient(authData[0].getArg());
                calls = mIncomingCallDAO.getCallsByCodeClient(authData[0].getArg());
                data = new ResponseData(clients,schedule,calls);
                break;
            case 202: //с момента последней синхронизации из pref
                clients = mClientsDAO.getClientsFromDate(authData[0].getExtras());
                schedule = mScheduleDAO.getScheduleFromDate(authData[0].getExtras());
                data = new ResponseData(clients,schedule,calls);
                break;
            case 203: //по номеру телефона
                clients = mClientsDAO.getClientsByPhone(authData[0].getExtras());
                data = new ResponseData(clients,null,null);
                break;
            case 204: //вычисляем дату здесь не нужно
                break;
            case 205: //Звонившие клиенты
                clients = mClientsDAO.getClientsNotified();
                data = new ResponseData(clients,null,null);
                break;
            case 206: //Неизвестные звонки
                calls = mIncomingCallDAO.getCallsUnknown();
                data = new ResponseData(null,null,calls);
                break;
            case 300:
                if (authData[0].getBody() != null) {
                    if (authData[0].getBody().getClients() != null) {
                        mClientsDAO.insertData(authData[0].getBody().getClients());
                    }
                    if (authData[0].getBody().getSchedule() != null) {
                        mScheduleDAO.insertData(authData[0].getBody().getSchedule());
                    }
                    if (authData[0].getBody().getCalls() != null) {
                        mIncomingCallDAO.insertData(authData[0].getBody().getCalls());
                    }
                }
                break;
            case 301:
                if (authData[0].getBody() != null) {
                    if (authData[0].getBody().getClients() != null) {
                        mClientsDAO.updateData(authData[0].getBody().getClients());
                    }
                    if (authData[0].getBody().getSchedule() != null) {
                        mScheduleDAO.updateData(authData[0].getBody().getSchedule());
                    }
                    if (authData[0].getBody().getCalls() != null) {
                        mIncomingCallDAO.updateData(authData[0].getBody().getCalls());
                    }
                }
                break;
            case 302:
                if (authData[0].getBody() != null) {
                    if (authData[0].getBody().getClients() != null) {
                        mClientsDAO.deleteData(authData[0].getBody().getClients());
                    }
                    if (authData[0].getBody().getSchedule() != null) {
                        mScheduleDAO.deleteData(authData[0].getBody().getSchedule());
                    }
                    if (authData[0].getBody().getCalls() != null) {
                        mIncomingCallDAO.deleteData(authData[0].getBody().getCalls());
                    }
                }
                break;
            case 400: //обработка неизвестного звонка
                if (authData[0].getBody() != null) {
                    if (authData[0].getBody().getCalls() != null) {
                        mIncomingCallDAO.mergeDataByPhoneUnknown(authData[0]
                                .getBody()
                                .getCalls()
                                .get(0));
                    }
                }
                break;
            case 401: //обработка звонка клиента
                if (authData[0].getBody() != null) {
                    if (authData[0].getBody().getCalls() != null) {
                        mIncomingCallDAO.mergeDataWithNotifyClientByCode(authData[0]
                                .getBody()
                                .getCalls()
                                .get(0));
                    }
                }
                break;
            default:
                System.out.println("UNKNOWN DATA EVENT: "+authData[0].getType());
        }
        ResponseMsg msg = new TransactData.ResponseMsg();
        msg.setCode(0);
        msg.setMessage("ОК");

        return new TransactData(msg, data);
    }

    @Override
    protected void onPostExecute(TransactData transactData) {
        if (this.mCallback != null) {this.mCallback.onResponseData(transactData);}
    }


    @Dao
    public abstract static class ClientsDAO {
        @Query("select * from clients")
        public abstract List<Client> getClients();

        @Query("select * from clients where id = :id")
        public abstract List<Client> getClientsById(int id);

        @Query("select * from clients where id = :id")
        public abstract Client getClientItemById(int id);

        @Query("select * from clients where last_change > :last_change")
        public abstract List<Client> getClientsFromDate(String last_change);

        @Query("select * from clients where phone = :phone")
        public abstract List<Client> getClientsByPhone(String phone);

        @Query("select * from clients where id = 0")
        public abstract List<Client> getClientsNew();

        @Query("select * from clients where updated = 1")
        public abstract List<Client> getClientsUpdated();

        @Query("select * from clients where notified = 1")
        public abstract List<Client> getClientsNotified();

        @Update
        public abstract int updateData(List<Client> data);
        @Update
        public abstract int updateDataItem(Client data);

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        public abstract void insertData(List<Client> data);

        @Delete
        public abstract int deleteData(List<Client> data);
    }

    @Dao
    public abstract static class ScheduleDAO {
        @Query("select * from schedule")
        public abstract List<ScheduleItem> getSchedule();

        @Query("select * from schedule where id_client = :id")
        public abstract List<ScheduleItem> getScheduleByIdClient(int id);

        @Query("select * from schedule where last_change > :last_change")
        public abstract List<ScheduleItem> getScheduleFromDate(String last_change);

        @Query("select * from schedule where updated = 1")
        public abstract List<ScheduleItem> getScheduleUpdated();

        @Query("select * from schedule where id = 0")
        public abstract List<ScheduleItem> getScheduleNew();

        @Update
        public abstract void updateData(List<ScheduleItem> data);

        @Insert
        public abstract void insertData(List<ScheduleItem> data);

        @Delete
        public abstract void deleteData(List<ScheduleItem> data);
    }

    @Dao
    public abstract static class IncomingCallsDAO {

        @Query("select * from calls")
        public abstract List<IncomingCall> getCalls();

        @Query("select * from calls where code_client = :code_client")
        public abstract List<IncomingCall> getCallsByCodeClient(int code_client);

        @Query("select * from calls where code_client = 0")
        public abstract List<IncomingCall> getCallsUnknown();

        @Query("select * from calls where code_client = :code_client")
        public abstract IncomingCall getCallByCodeClient(int code_client);


        @Query("delete from calls where code = :code")
        public abstract void deleteByCode(int code);
        @Query("delete from calls where phone = :phone and code_client = 0")
        public abstract void deleteByPhoneUnknown(String phone);
        @Query("update clients set notified = 1 where code = :code")
        public abstract void updateNotifyToClientByCode(int code);

        @Update
        public abstract void updateData(List<IncomingCall> data);

        @Insert(onConflict = OnConflictStrategy.REPLACE )
        public abstract void insertData(List<IncomingCall> data);

        @Insert
        public abstract void insertDataItem(IncomingCall data);

        @Delete
        public abstract void deleteData(List<IncomingCall> data);

        @Delete
        public abstract void deleteDataItem(IncomingCall data);

        @Transaction
        public void mergeDataByPhoneUnknown(IncomingCall new_call) {
            deleteByPhoneUnknown(new_call.getPhone());
            insertDataItem(new_call);
        }
        @Transaction
        public void mergeDataWithNotifyClientByCode(IncomingCall new_call) {
            deleteByPhoneUnknown(new_call.getPhone());
            insertDataItem(new_call);
            updateNotifyToClientByCode(new_call.getClient().getCode());
        }

    }

    @Database(entities = {Client.class, ScheduleItem.class, IncomingCall.class}, version = VERSION_DB)
    public abstract static class AsyncWorkerDatabase extends RoomDatabase {
        public abstract ClientsDAO getClientsDAO();
        public abstract ScheduleDAO getScheduleDAO();
        public abstract IncomingCallsDAO getIncomingCallsDAO();
    }


    private static void enqueue(AsyncWorkerDatabase asyncWorkerDatabase,
                                IDataTransactCallback callback,
                                AuthData auth) {

        //выполняем на новом экземпляре AsyncTask
        RoomDataTransact newInstance = new RoomDataTransact(asyncWorkerDatabase,callback,auth);
    }
}
