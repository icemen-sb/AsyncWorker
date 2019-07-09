package ru.relastic.cloudreception.repository;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TransactData {
    private ResponseMsg response_msg;
    private ResponseData response_data;

    public TransactData() {}
    public TransactData(ResponseMsg response_msg, ResponseData response_data) {
        this.response_msg = response_msg;
        this.response_data = response_data;
    }

    public ResponseMsg getResponse_msg() {
        return this.response_msg;
    }
    public void setResponse_msg(ResponseMsg response_msg) {
        this.response_msg = response_msg;
    }

    public ResponseData getResponse_data() {
        return this.response_data;
    }
    public void setResponse_data(ResponseData response_data) {
        this.response_data = response_data;
    }

    public static String parseDigit(@Nullable String pattern) {
        StringBuilder retVal= new StringBuilder();
        if (pattern!=null) {
            for (char c : pattern.toCharArray()) {
                if (Character.isDigit(c)) {
                    retVal.append(c);
                }
            }
        }

        return retVal.toString();
    }
    public static long parseDigitInt(String pattern) {
        String retval="";
        for (char c : pattern.toCharArray()) {
            if (Character.isDigit(c)) {
                retval+=c;
            }
        }
        return Long.parseLong(retval);
    }
    public static String NoNull(String text) {
        return ((text!=null) ? text : "").trim();
    }


    public static class ResponseMsg {
        private int code;
        private String message;
        private int arg;
        private String timestamp;
        private String extras;
        private String session_id;

        public ResponseMsg () {}
        public ResponseMsg (int code,
                            String message,
                            int arg,
                            String timestamp,
                            String extras,
                            String session_id) {
            this.code = code;
            this.message = message;
            this.arg = arg;
            this.timestamp = timestamp;
            this.extras = extras;
            this.session_id = session_id;
        }

        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }

        public int getArg() {
            return arg;
        }
        public void setArg(int arg) {
            this.arg = arg;
        }

        public String getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getExtras() {
            return extras;
        }
        public void setExtras(String extras) {
            this.extras = extras;
        }

        public String getSession_id() {
            return session_id;
        }
        public void setSession_id(String session_id) {
            this.session_id = session_id;
        }
    }

    public static class ResponseData {
        private List<Client> clients;
        private List<ScheduleItem> schedule;
        @Expose
        private List<IncomingCall> calls;

        public ResponseData() {}
        public ResponseData(@Nullable List<Client> clients,
                            @Nullable List<ScheduleItem> schedule,
                            @Nullable List<IncomingCall> incoming_calls) {
            this.clients = clients;
            this.schedule = schedule;
            this.calls = incoming_calls;
        }
        public ResponseData(@Nullable Client client,
                            @Nullable ScheduleItem schedule_item,
                            @Nullable IncomingCall incoming_call) {
            if (client != null) {
                this.clients = new ArrayList<>();
                this.clients.add(client);
            }
            if (schedule_item != null) {
                this.schedule = new ArrayList<>();
                this.schedule.add(schedule_item);
            }
            if (incoming_call != null) {
                this.calls = new ArrayList<>();
                this.calls.add(incoming_call);
            }
        }
        public ResponseData (@NonNull List list) {
            if (list.size()>0) {
                if (list.get(0).getClass().equals(Client.class)) {
                    this.clients = list;
                }else if (list.get(0).getClass().equals(ScheduleItem.class)) {
                    this.schedule = list;
                }else if (list.get(0).getClass().equals(IncomingCall.class)) {
                    this.calls = list;
                }
            }
        }
        public ResponseData (@NonNull Object object) {
            if (object.getClass().equals(Client.class)) {
                ArrayList<Client> list = new ArrayList<>();
                list.add((Client)object);
                this.clients = list;
            }else if(object.getClass().equals(ScheduleItem.class)) {
                ArrayList<ScheduleItem> list = new ArrayList<>();
                list.add((ScheduleItem)object);
                this.schedule = list;
            }else if (object.getClass().equals(IncomingCall.class)) {
                ArrayList<IncomingCall> list = new ArrayList<>();
                list.add((IncomingCall)object);
                this.calls = list;
            }
        }

        //public ResponseData(List<IncomingCall> calls) {
        //
        //}
        //public ResponseData(IncomingCall incomingCall) {
        //    ArrayList<IncomingCall> list = new ArrayList<>();
        //    list.add(incomingCall);
        //    this.calls = list;
        //}

        public List<Client> getClients() {
            return clients;
        }
        public void setClients(List<Client> clients) {
            this.clients = clients;
        }

        public List<ScheduleItem> getSchedule() {
            return schedule;
        }
        public void setSchedule(List<ScheduleItem> schedule) {
            this.schedule = schedule;
        }

        public List<IncomingCall> getCalls() {
            return calls;
        }
        public void setCalls(List<IncomingCall> calls) {
            this.calls = calls;
        }


        @Entity(tableName = "clients", indices = {@Index("code")})
        public static class Client {
            @Expose
            @PrimaryKey(autoGenerate = true)
            private int code;
            //
            private int id;
            private int id_company;
            private int id_parent;
            private String last_change;
            private String phone;
            private String firstname;
            private String surename;
            private String lastname;
            private String date_of_birth;
            private String documents;
            private String description;
            //
            @Expose
            private boolean updated;
            @Expose
            private boolean notified;
            @Expose
            private String date_calling;
            ////
            @Expose
            @Ignore
            private int position;

            public Client() {
                id_company = 0;
                updated = true;
            }
            @Ignore
            public Client(Bundle bundle) {
                code = bundle.getInt("code");
                id = bundle.getInt("id");
                id_company = bundle.getInt("id_company");
                id_parent = bundle.getInt("id_parent");
                last_change = bundle.getString("last_change");
                phone = bundle.getString("phone");
                firstname = bundle.getString("firstname");
                surename = bundle.getString("surename");
                lastname = bundle.getString("lastname");
                date_of_birth = bundle.getString("date_of_birth");
                documents = bundle.getString("documents");
                description = bundle.getString("description");
                updated = bundle.getBoolean("updated");
                notified = bundle.getBoolean("notified");
                date_calling = bundle.getString("date_calling");
                position = bundle.getInt("position");
            }

            public int getCode() {
                return code;
            }
            public void setCode(int code) {
                this.code = code;
            }

            public int getId() {
                return id;
            }
            public void setId(int id) {
                this.id = id;
            }

            public int getId_company() {
                return id_company;
            }
            public void setId_company(int id_company) {
                this.id_company = id_company;
            }

            public int getId_parent() {
                return id_parent;
            }
            public void setId_parent(int id_parent) {
                this.id_parent = id_parent;
            }

            public String getLast_change() {
                return last_change;
            }
            public void setLast_change(String last_change) {
                this.last_change = last_change;
            }

            public String getPhone() {
                return phone;
            }
            public void setPhone(String phone) {
                this.phone = parseDigit(phone);
            }

            public String getFirstname() {
                return firstname;
            }
            public void setFirstname(String firstname) {
                this.firstname = firstname;
            }

            public String getSurename() {
                return surename;
            }
            public void setSurename(String surename) {
                this.surename = surename;
            }

            public String getLastname() {
                return lastname;
            }
            public void setLastname(String lastname) {
                this.lastname = lastname;
            }

            public String getDate_of_birth() {
                return date_of_birth;
            }
            public void setDate_of_birth(String date_of_birth) {
                this.date_of_birth = date_of_birth;
            }

            public String getDocuments() {
                return documents;
            }
            public void setDocuments(String documents) {
                this.documents = documents;
            }

            public String getDescription() {
                return description;
            }
            public void setDescription(String description) {
                this.description = description;
            }

            public boolean getUpdated() {
                return updated;
            }
            public void setUpdated(boolean updated) {
                this.updated = updated;
            }

            public boolean getNotified() {
                return notified;
            }
            public void setNotified(boolean notified) {
                this.notified = notified;
            }

            public String getDate_calling() {
                return date_calling;
            }
            public void setDate_calling(String date_calling) {
                this.date_calling = date_calling;
            }

            public int getPosition() {
                return position;
            }
            public void setPosition(int position) {
                this.position = position;
            }

            public @NonNull String getFullName(){
                return NoNull(lastname)
                        .concat(" ")
                        .concat(NoNull(firstname))
                        .trim()
                        .concat(" ")
                        .concat(NoNull(surename))
                        .trim();
            }

            public static Bundle toBundle(Client client){
                Bundle bundle = new Bundle();

                bundle.putInt("code",client.getCode());
                bundle.putInt("id",client.getId());
                bundle.putInt("id_company",client.getId_company());
                bundle.putInt("id_parent",client.getId_parent());

                bundle.putString("last_change",client.getLast_change());
                bundle.putString("phone",client.getPhone());
                bundle.putString("firstname",client.getFirstname());
                bundle.putString("surename",client.getSurename());
                bundle.putString("lastname",client.getLastname());
                bundle.putString("date_of_birth",client.getDate_of_birth());
                bundle.putString("documents",client.getDocuments());
                bundle.putString("description",client.getDescription());

                bundle.putBoolean("updated",client.getUpdated());
                bundle.putBoolean("notified",client.getNotified());
                bundle.putString("date_calling",client.getDate_calling());

                bundle.putInt("position",client.getPosition());

                return bundle;
            }
            public static Client fromBundle(Bundle data){
                return new Client(data);
            }
        }

        @Entity(tableName = "schedule", indices = {@Index("code")})
        public static class ScheduleItem {
            @Expose
            @PrimaryKey(autoGenerate = true)
            private int code;
            @Expose
            private int code_client = 0;
            //
            private int id;
            private int id_company;
            private int id_client;
            private String last_change;
            private String scheduled;
            private String note;
            //
            @Expose
            private boolean updated;
            ////
            @Expose
            @Ignore
            public int position;

            public ScheduleItem() {}

            public int getCode() {
                return this.code;
            }
            public void setCode(int code) {
                this.code = code;
            }

            public int getCode_client() {
                return code_client;
            }
            public void setCode_client(int code_client) {
                this.code_client = code_client;
            }

            public int getId() {
                return this.id;
            }
            public void setId(int id) {
                this.id = id;
            }

            public int getId_company() {
                return id_company;
            }
            public void setId_company(int id_company) {
                this.id_company = id_company;
            }

            public int getId_client() {
                return id_client;
            }
            public void setId_client(int id_client) {
                this.id_client = id_client;
            }

            public String getLast_change() {
                return last_change;
            }
            public void setLast_change(String last_change) {
                this.last_change = last_change;
            }

            public String getScheduled() {
                return scheduled;
            }
            public void setScheduled(String scheduled) {
                this.scheduled = scheduled;
            }

            public boolean getUpdated() {
                return updated;
            }
            public void setUpdated(boolean updated) {
                this.updated = updated;
            }

            public String getNote() {
                return note;
            }
            public void setNote(String note) {
                this.note = note;
            }

            public int getPosition() {
                return position;
            }
            public void setPosition(int position) {
                this.position = position;
            }
        }

        @Entity(tableName = "calls", indices = {@Index("code")})
        public static class IncomingCall {
            public static final String INCOMING_CALL_CODE_CLIENT = "incoming_call_code_client";
            public static final String INCOMING_CALL_PHONE = "incoming_call_phone";
            public static final String INCOMING_CALL_DATE_CALLING = "incoming_call_date_calling";
            public static final String INCOMING_CALL_NOTE = "incoming_call_note";
            public static final String INCOMING_CALL_CLIENT = "incoming_call_client";
            static final String FORMAT_DEFAULT_DATE = "dd/MM/yyyy";
            static final String FORMAT_DEFAULT_TIME = "HH:mm:ss";
            static final String DEFAULT_TIME_MIDDAY = "12:00:00";
            static final String FORMAT_DEFAULT_DATETIME = FORMAT_DEFAULT_DATE +" "+FORMAT_DEFAULT_TIME;

            @PrimaryKey(autoGenerate = true)
            private int code;
            private int code_client = 0;
            private String phone;
            private String dateCalling;
            private String note;
            @Ignore
            private Client client = null;
            @Ignore
            private int position;

            public IncomingCall() {}
            @Ignore
            public IncomingCall(String phone) {
                this.code_client = 0;
                this.phone = phone;
                this.dateCalling = String.valueOf((new Date()).getTime());
            }
            @Ignore
            public IncomingCall(Client client) {
                code_client = client.getCode();
                phone = client.getPhone();
                dateCalling = String.valueOf((new Date()).getTime());
                this.client = client;
            }
            @Ignore
            public IncomingCall(Bundle bundle) {
                if (bundle != null) {
                    String value = bundle.getString(INCOMING_CALL_CODE_CLIENT);
                    if (value != null) {
                        code_client = Integer.valueOf(value);
                    }
                    if (bundle.containsKey(INCOMING_CALL_CLIENT)) {
                        client = Client.fromBundle(bundle.getBundle(INCOMING_CALL_CLIENT));
                    }
                    phone = bundle.getString(INCOMING_CALL_PHONE);
                    dateCalling = bundle.getString(INCOMING_CALL_DATE_CALLING);
                    note = bundle.getString(INCOMING_CALL_NOTE);
                }
            }

            public Bundle getBundle() {
                Bundle bundle = new Bundle();
                bundle.putString(INCOMING_CALL_CODE_CLIENT, String.valueOf(code_client));
                bundle.putBundle(INCOMING_CALL_CLIENT,Client.toBundle(client));
                bundle.putString(INCOMING_CALL_PHONE, phone);
                bundle.putString(INCOMING_CALL_DATE_CALLING,dateCalling);
                bundle.putString(INCOMING_CALL_NOTE,note);
                return bundle;
            }

            public int getCode() {
                return this.code;
            }
            public void setCode(int code) {
                this.code = code;
            }

            public int getCode_client() {
                return code_client;
            }
            public void setCode_client(int code_client) {
                this.code_client = code_client;
            }

            public String getPhone() {
                return phone;
            }
            public void setPhone(String phone) {
                this.phone = parseDigit(phone);
            }

            public String getDateCalling() {
                return dateCalling;
            }
            public String getDateCallingFormat() {
                return IncomingCall.getDateTimeString(parseDigitInt(dateCalling), null);
            }

            public void setDateCalling(String dataCalling) {
                this.dateCalling = dataCalling;
            }

            public String getNote() {
                return note;
            }
            public void setNote(String note) {
                this.note = note;
            }

            public Client getClient() {
                return client;
            }
            public void setClient(Client client) {
                this.client = client;
            }

            public int getPosition() {
                return position;
            }
            public void setPosition(int position) {
                this.position = position;
            }

            public static Client createClientByPhone(String phone){
                Client retVal = new Client();
                retVal.setPhone(phone);
                return retVal;
            }
            public static String getDateString(long date, @Nullable String date_format){
                if (date_format==null) {date_format = FORMAT_DEFAULT_DATE;}
                SimpleDateFormat sdf = new SimpleDateFormat(date_format);
                sdf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
                return sdf.format(new Date(date));
            }
            public static String getTimeString(long date, @Nullable String time_format){
                if (time_format==null) {time_format = FORMAT_DEFAULT_TIME;}
                SimpleDateFormat sdf = new SimpleDateFormat(time_format);
                sdf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
                return sdf.format(new Date(date));
            }
            public static String getDateTimeString(long date, @Nullable String datetime_format){
                if (datetime_format==null) {datetime_format = FORMAT_DEFAULT_DATETIME;}
                SimpleDateFormat sdf = new SimpleDateFormat(datetime_format);
                sdf.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
                return sdf.format(new Date(date));
            }
        }
    }
}
