package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    private ListView ExpensesListView;
    private Intent intent;
    private Context context;
    private ArrayList<Expenses> arrayList;
    private BOXadapter boxadapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button AddNewExpensesButton = findViewById(R.id.button2);
        ExpensesListView = findViewById(R.id.ListViewMain);
        context = this;
        arrayList = new ArrayList<Expenses>();
        CreateArrayListFromSQLDB();
        boxadapter = new BOXadapter(context,arrayList);
        ExpensesListView.setAdapter(boxadapter);

        AddNewExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context,AddNewExpenses.class);
                startActivityForResult(intent,1);
            }
        });
        ExpensesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Expenses exp = arrayList.get(position);
                Integer ID = exp.ID;
                intent = new Intent(context,ChangeOrRemoveExpenses.class);
                intent.putExtra("ID",ID);
                startActivityForResult(intent,2);
            }
        });

        double Consumption = consumption();
        if(Consumption!=0) {
            setTitle("Расход топлива = " + Consumption+"л/100км");
        }else{
            setTitle("Учет расходов");
        }
    }


    private void CreateArrayListFromSQLDB(){
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Expenses",null,null,null,null,null,null);
        if(arrayList.size()>0){
            arrayList.clear();
        }


        if(cursor.moveToFirst()){

            int idColIndex = cursor.getColumnIndex("id");
            int DataColIndex = cursor.getColumnIndex("Data");
            int CostTypeColIndex = cursor.getColumnIndex("CostType");
            int QuantityColIndex = cursor.getColumnIndex("Quantity");
            int PriceColIndex = cursor.getColumnIndex("Price");
            int CostColIndex = cursor.getColumnIndex("Cost");
            int PetrolIndex = cursor.getColumnIndex("Petrol");
            int MileageIndex = cursor.getColumnIndex("Mileage");

            do{
                Expenses exp = new Expenses();
                Integer ID = cursor.getInt(idColIndex);
                String Data = cursor.getString(DataColIndex);
                String CostType = cursor.getString(CostTypeColIndex);
                Double Quantity = cursor.getDouble(QuantityColIndex);
                Double Price = cursor.getDouble(PriceColIndex);
                Double Cost = cursor.getDouble(CostColIndex);
                Boolean Petrol;
                if(cursor.getInt(PetrolIndex)==1){
                    Petrol=true;
                }else {
                    Petrol=false;
                }
                if(Petrol){
                    Double Mileage = cursor.getDouble(MileageIndex);
                    exp.Mileage=Mileage;
                }

                exp.ID=ID;
                exp.Data=Data;
                exp.CostType=CostType;
                exp.Quantity=Quantity;
                exp.Price=Price;
                exp.Cost=Cost;
                exp.Petrol=Petrol;

                arrayList.add(exp);
            }while (cursor.moveToNext());
        }
        dbHelper.close();
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CreateArrayListFromSQLDB();
        boxadapter.notifyDataSetChanged();
        double Consumption = consumption();
        if(Consumption!=0) {
            setTitle("Расход топлива = " + Consumption+"л/100км");
        }else{
            setTitle("Учет расходов");
        }
    }

    private double consumption(){
        if(arrayList.size()>0){
        double Quantity = 0;
        ArrayList<Double> list = new ArrayList<Double>();
        for(Iterator<Expenses> iter = arrayList.iterator();iter.hasNext();){
            Expenses expenses =iter.next();
            if(expenses.Petrol){
                Quantity+=expenses.Quantity;
                list.add(expenses.Mileage);
            }
        }
        if(Collections.max(list)!=Collections.min(list)) {
            return (double) Math.round(Quantity / (Collections.max(list) - Collections.min(list)) * 1000) / 10;
        }else{
            return 0;
        }
        }else {
            return 0;
        }
    };
}
