package com.eidotab.smartab.SQlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.WifiManager;
import com.eidotab.smartab.Models.Mesa;
import com.google.gson.Gson;
import java.io.File;



public class DBHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "smartab00a.db";
    private static final int DATABASE_VERSION = 3;
    private String DB_PATH = null;
    private Context mContext;

    public static DBHelper GetDBHelper(Context context)
    {
        DBHelper dbHelper = new DBHelper(context);

        if (!dbHelper.isDataBaseExist())
        {
            dbHelper.deleteAllMesa();
            dbHelper.createDataBase();
        }
        return dbHelper;
    }

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mContext = context;

        DB_PATH = "/data/data/" + mContext.getPackageName() + "/databases/";
    }

    private void createDataBase()
    {
        boolean isExist = isDataBaseExist();

        if (!isExist)
        {
            this.getReadableDatabase();

            onCreate(this.getWritableDatabase());
        }
    }

    public boolean isDataBaseExist()
    {
        File file = new File(DB_PATH + DATABASE_NAME);

        return file.exists();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Escribir la estructura de la bd: Tablas, ...
        db.execSQL(" CREATE TABLE mesa (_id TEXT primary key, jmesa  TEXT); ");
        // ....
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // Escribir las modificaciones en la bd.
        db.execSQL(" DROP TABLE IF EXISTS mesa; ");
        onCreate(db);
    }


    /* IMPLEMENTACIÓN: MÉTODOS CRUD */

    /* TABLA: mesa */

    private static final String TABLE_NAME_MESA = "mesa";




    public boolean addMesa(Mesa mesa)
    {


        WifiManager wimanager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();

        if (macAddress == null)
        {

            return false;

        }
        else
        {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Gson gson = new Gson();
        String json = gson.toJson(mesa);
        contentValues.put("_id", macAddress);
        contentValues.put("jmesa", json);
        db.insert(TABLE_NAME_MESA, null, contentValues);

        return true;

        }

    }


    public Mesa getMesa()  //PARA OBTENER QUE MESAS ATIENDE EL RELOJ ENVIAR EL MACADDRESS
    {
        Mesa mesa = new Mesa();

        WifiManager wimanager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        String macAddress = wimanager.getConnectionInfo().getMacAddress();

        SQLiteDatabase db = this.getReadableDatabase();

        Gson gson = new Gson();

        Cursor cursor = db.rawQuery(" SELECT * FROM " + TABLE_NAME_MESA + " WHERE _id = '" + macAddress+ "'", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {

            String sacadata = (cursor.getString(cursor.getColumnIndex("jmesa")));

            mesa  = gson.fromJson(sacadata, Mesa.class);

            cursor.moveToNext();
        }
        cursor.close();
        return mesa;
    }


    public boolean deleteAllMesa()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME_MESA, "",
                new String[]{  });

        return true;
    }




}
