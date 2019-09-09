package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;



public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "AccountingOfFuelDB", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Expenses ("+ "id integer primary key autoincrement,"
                + "Data string," + "CostType string," +"Quantity Double,"+"Price Double,"+"Cost Double,"+ "Petrol int,"+"Mileage Double"+");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1 && newVersion == 2){
            try {
                db.beginTransaction();
                db.execSQL("alter table Expenses add column Petrol int;");
                db.execSQL("alter table Expenses add column Mileage Double;");
                db.setTransactionSuccessful();
            }finally {
                db.endTransaction();
            }
        }
    }
}
