package ru.relastic.cloudreception.repository;

import android.os.Bundle;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class AuthData {
    public static final int AUTH_TYPE_HELO            = 0;          //Запрос данных сервера
    public static final int AUTH_TYPE_RESQUE          = 1;          //Запрос смены пароля
    public static final int AUTH_TYPE_COMMIT          = 2;          //Подтверждение смены пароля
    public static final int AUTH_TYPE_GET_SESSION_ID  = 3;          //Запрос одноразового открытого ключа:
                                                                    //если в запросе присутствует логин,
                                                                    //одноразовый открытый ключ всегда
                                                                    //добавляется в ответ сервера,
                                                                    //а старый ключ аннулируется
    public static final int AUTH_TYPE_GETALL          = 200;
    public static final int AUTH_TYPE_GET_BY_ID       = 201;        //arg='id'
    public static final int AUTH_TYPE_GET_SYNC        = 202;        //extras='текущий last sync'
    public static final int AUTH_TYPE_GET_BY_PHONE    = 203;        //extras='входящий номер телефона (только цифры)
    public static final int AUTH_TYPE_GET_UPDATE_TS   = 204;        //extras ответа будет содержать актуальную дату обновления бд сервера
    public static final int AUTH_TYPE_GETALL_NITUFIED = 205;        //Запрос клиентов с уведомлением
    public static final int AUTH_TYPE_GETALL_NEWES    = 206;        //Запрос неидентифиципрванных звонков
    public static final int AUTH_TYPE_INSERT          = 300;        //новые записи
    public static final int AUTH_TYPE_UPDATE          = 400;        //измененные записи
    public static final int AUTH_TYPE_UPDATE_NOTIFIES_ALL      = 401;   //установка всех флагов false
    public static final int AUTH_TYPE_DELETE          = 500;        //удаляемые записи
    public static final int AUTH_TYPE_DELETE_INCOMING_BY_PHONE = 501;   //удалить запись IncomingCall по номеру телефона в экземпляре IncomingCall
    public static final int AUTH_TYPE_DELETE_INCOMING_ALL      = 502;   //удалить все неизвестные вызовы
    public static final int AUTH_TYPE_TR_UNKNOWN      = 601;        //обработка неизвестного вызова
    public static final int AUTH_TYPE_TR_NOTIFY       = 602;        //обработка звонка клиента
    //public static final int AUTH_TYPE_PUT_BY_ID       = 300;

    private boolean is_ready= false;        //No auto Init
    private String login = null;            //No auto Init
    private String secret_key;              //No auto Init

    private int type;                       //Auto Init
    private int arg;                        //Auto Init
    private String extras;                  //Auto Init
    private TransactData.ResponseData body; //Auto Init

    private volatile String session_id;     //No Scope
    private volatile String token;          //No Scope


    public AuthData(){init();}
    private void init () {
        type = 0;
        arg = -1;
        extras = null;
        body = null;
    }
    private AuthData(String login, String key) {
        init();
        this.login = login;
        this.secret_key = key;
        this.is_ready = true;
        //this.session_id = genNewKey();                      //генерируем значение 5 знаков
        //String token_clean = this.secret_key + "-" + this.session_id;
        //this.token = getToken(token_clean);                 //вычисляем значение
    }
    public void updateREG(Bundle bundle) {
        init();
        if (bundle != null) {
            init();
            this.login = bundle.getString(PreferencesTransact.PREF_NAME_LOGIN);
            this.secret_key = bundle.getString(PreferencesTransact.PREF_NAME_SECRET_KEY);
        }
    }
    public void updateREG(String login, String key) {
        init();
        this.login = login;
        this.secret_key = key;
    }
    public AuthData next() {
        AuthData retVal = new AuthData(this.login, this.secret_key);
        retVal.session_id = this.session_id;
        retVal.token = this.token;
        this.session_id = null;
        this.token = null;
        return retVal;
    }
    public AuthData clone() {
        AuthData retVal = new AuthData();
        retVal.is_ready = this.is_ready;
        retVal.login = this.login;
        retVal.secret_key = this.secret_key;
        retVal.type = this.type;
        retVal.arg = this.arg;
        retVal.extras = this.extras;
        retVal.body = this.body;
        retVal.session_id = this.session_id;
        retVal.token = this.token;
        return retVal;
    }


    public int getType() {
        return type;
    }
    public AuthData setType(int type) {
        this.type = type;
        return this;
    }

    public String getLogin() {
        return login;
    }
    public String getSession_id() {
        return session_id;
    }
    public void setSession_id(String session_id) {
        this.session_id = session_id;
        String token_clean = this.secret_key + "-" + this.session_id;
        this.token = genToken(token_clean);                 //вычисляем значение
    }
    public String getToken() {
        return token;
    }

    public int getArg() {
        return arg;
    }
    public AuthData setArg(int arg) {
        this.arg = arg;
        return this;
    }

    public String getExtras() {
        return extras;
    }
    public AuthData setExtras(String extras) {
        this.extras = extras;
        return this;
    }

    public TransactData.ResponseData getBody() {
        return body;
    }
    public AuthData setBody(TransactData.ResponseData body) {
        this.body = body;
        return this;
    }

    public boolean isReady() {
        return (this.is_ready);
    }



    private static String genToken(String s) {
        String $token = "";
        try {
            MessageDigest m = null;
            m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(),0,s.length());
            $token = new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
        }
        return $token;
    }
    private static String genNewKey(){
        Random r = new Random();
        return String.valueOf(r.nextInt(89999999)+10000000);
    }
    public static AuthData newInstance(String login, String secret_key) {
        return new AuthData(login, secret_key);
    }

}
