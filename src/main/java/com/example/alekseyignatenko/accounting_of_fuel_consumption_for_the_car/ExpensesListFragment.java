package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ExpensesListFragment extends Fragment {

    private ListView ExpensesListView;
    private Intent intent;
    //private Context context;
    private ArrayList<Expenses> arrayList;
    private BOXadapter boxadapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses_list,container,false);
        Button AddNewExpensesButton = view.findViewById(R.id.button2);
        ExpensesListView = view.findViewById(R.id.ListViewMain);
        //context = this;
        arrayList = new ArrayList<Expenses>();
        CreateArrayListFromSQLDB();
        boxadapter = new BOXadapter(getActivity(),arrayList);
        ExpensesListView.setAdapter(boxadapter);
        final ExpensesListFragment ELF = this;

        AddNewExpensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent = new Intent(getActivity(),AddNewExpenses.class);
                //startActivityForResult(intent,1);
                AddNewExpensesFragment addNewExpensesFragment = new AddNewExpensesFragment();
                addNewExpensesFragment.setTargetFragment(ELF,1);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.launch_frame,addNewExpensesFragment)
                        .addToBackStack(AddNewExpensesFragment.class.getName())
                        .commit();
            }
        });
        ExpensesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Expenses exp = arrayList.get(position);
                Integer ID = exp.ID;
                //intent = new Intent(getActivity(),ChangeOrRemoveExpenses.class);
                //intent.putExtra("ID",ID);
                //startActivityForResult(intent,2);
                Bundle bundle = new Bundle();
                bundle.putInt("ID",ID);
                ChangeOrRemoveExpensesFragment changeOrRemoveExpensesFragment = new ChangeOrRemoveExpensesFragment();
                changeOrRemoveExpensesFragment.setArguments(bundle);
                changeOrRemoveExpensesFragment.setTargetFragment(ELF,1);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.launch_frame,changeOrRemoveExpensesFragment)
                        .addToBackStack(AddNewExpensesFragment.class.getName())
                        .commit();
            }
        });

        double Consumption = consumption();
        if(Consumption!=0) {
            getActivity().setTitle("Расход топлива = " + Consumption+"л/100км");
        }else{
            getActivity().setTitle(getString(R.string.Title_Cost_Accounting));
        }
        return view;
    }


    private void CreateArrayListFromSQLDB(){
        DBHelper dbHelper = new DBHelper(getActivity());
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CreateArrayListFromSQLDB();
        boxadapter.notifyDataSetChanged();
        double Consumption = consumption();
        if(Consumption!=0) {
            getActivity().setTitle(getString(R.string.Title_Average_Fuel_Consumption) + Consumption+getString(R.string.Title_Unit_Of_Measurement));
        }else{
            getActivity().setTitle(getString(R.string.Title_Cost_Accounting));
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
