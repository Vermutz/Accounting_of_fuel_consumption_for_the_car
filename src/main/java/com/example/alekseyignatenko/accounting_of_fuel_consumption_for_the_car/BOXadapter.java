package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;



public class BOXadapter extends BaseAdapter {

    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Expenses> objects;
    BOXadapterValue BoXadapterValue;

    BOXadapter(Context context,BOXadapterValue boXadapterValue){
        ctx = context;
        objects = boXadapterValue.arrayList;
        BoXadapterValue = boXadapterValue;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        };

        Expenses exp = getExpenses(position);

        ((TextView) view.findViewById(R.id.tvData)).setText(exp.Data);
        ((TextView) view.findViewById(R.id.tvTypeCost)).setText(exp.CostType);
        ((TextView) view.findViewById(R.id.tvQuantity)).setText(BoXadapterValue.BOXadapterQuantity+exp.Quantity);
        ((TextView) view.findViewById(R.id.tvPrice)).setText(BoXadapterValue.BOXadapterPrice + exp.Price);
        ((TextView) view.findViewById(R.id.tvСost)).setText(BoXadapterValue.BOXadapterCost+exp.Cost);

        return view;
    }

    // товар по позиции
    Expenses getExpenses(int position) {
        return ((Expenses) getItem(position));
    }
}
