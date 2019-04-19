package ru.relastic.asyncworker.repository;

import android.os.Bundle;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class AuthData {
    public static int AUTH_TYPE_GETALL          = 200;
    public static int AUTH_TYPE_GET_BY_ID       = 201;      //arg='id'
    public static int AUTH_TYPE_GET_SYNC        = 202;      //extras='текущий last sync'
    public static int AUTH_TYPE_GET_BY_PHONE    = 203;      //extras='входящий номер телефона (только цифры)
    public static int AUTH_TYPE_GET_UPDATE_TS   = 204;      //extras ответа будет содержать актуальную дату обновления бд сервера
    public static int AUTH_TYPE_INSERT          = 300;      //новые записи
    public static int AUTH_TYPE_UPDATE          = 301;      //измененные записи
    public static int AUTH_TYPE_DELETE          = 302;      //удаляемые записи
    public static int AUTH_TYPE_TR_UNKNOWN      = 400;      //обработка неизвестного звонка
    public static int AUTH_TYPE_TR_NOTIFI       = 401;      //обработка звонка клиента
    //public static int AUTH_TYPE_PUT_BY_ID       = 300;

    private boolean mIsReady= false;
    private int type = 0;
    private String login;
    private String secret_key;
    private String session_id;
    private String token;
    private int arg = -1;
    private String extras = null;
    private TransactData.ResponseData body = null;

    public AuthData(){}
    private AuthData(String login, String key) {
        this.login = login;
        this.secret_key = key;
        this.session_id = genNewKey();                      //генерируем значение 5 знаков
        String token_clean = this.secret_key + "-" + this.session_id;
        this.token = getToken(token_clean);                 //вычисляем значение
        this.mIsReady = true;
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
    public String getSecret_key(){return secret_key;}
    public String getSession_id() {
        return session_id;
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


    private static String getToken(String s) {
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

    public void update(Bundle bundle) {
        if (bundle != null) {
            this.login = bundle.getString(PreferencesTransact.PREF_NAME_LOGIN);
            this.secret_key = bundle.getString(PreferencesTransact.PREF_NAME_SECRET_KEY);
        }
    }
    public void update(String login, String key) {
        this.login = login;
        this.secret_key = key;
    }
    public AuthData next() {
        this.session_id = genNewKey();                      //генерируем значение 5 знаков
        String token_clean = this.secret_key + "-" + this.session_id;
        this.token = getToken(token_clean);                 //вычисляем значение
        System.out.println("##########: "+login);
        System.out.println("##########: "+secret_key);
        return this;
    }

    public boolean isReady() {
        return (this.mIsReady);
    }

    public static AuthData newInstance(String login, String secret_key) {
        return new AuthData(login, secret_key);
    }

}
