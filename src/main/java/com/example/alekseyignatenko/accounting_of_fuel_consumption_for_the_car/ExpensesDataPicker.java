package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ExpensesDataPicker {

    protected static int DIALOG_DATE = 1;
    protected int Day,Month,Year;
    private Button DataTextView;
    private Context Cont;



    public void ExpensesDataPicker(Context context,Button view){
        DataTextView = view;
        Calendar calendar = Calendar.getInstance();
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        Month = calendar.get(Calendar.MONTH);
        Year = calendar.get(Calendar.YEAR);
        Cont = context;
        DataTextView.setText(""+Day+"."+(Month+1)+"."+Year);
    }

    public void ExpensesDataPicker(Context context,String Data,Button view){
        Cont = context;
        DataTextView = view;
        DataTextView.setText(Data);
        StringDataToIntDAY_MONTH_YEAR(Data);
    }


    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            Year = year;
            Month = monthOfYear;
            Day = dayOfMonth;
            DataTextView.setText("" + Day + "." + (Month+1) + "." + Year);
        }
    };

    private void StringDataToIntDAY_MONTH_YEAR(String Data){
        StringBuilder day = new StringBuilder();
        StringBuilder month = new StringBuilder();
        StringBuilder year = new StringBuilder();
        int b = 0;
        for (int i = 0; i < Data.length(); i++) {
            if ((Data.charAt(i) >= '0' && Data.charAt(i) <= '9')) {
                switch (b){
                    case 0:
                        day.append(Data.charAt(i));
                        break;
                    case 1:
                        month.append(Data.charAt(i));
                        break;
                    case 2:
                        year.append(Data.charAt(i));
                        break;
                    default:break;
                };
            }else{
                ++b;
            }
        }
        Day = Integer.valueOf(day.toString());
        Month = Integer.valueOf(month.toString())-1;
        Year = Integer.valueOf(year.toString());
        //Toast.makeText(Cont,month,Toast.LENGTH_LONG).show();
    }
}
