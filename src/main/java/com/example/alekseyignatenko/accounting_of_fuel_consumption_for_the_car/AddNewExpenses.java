package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;

public class AddNewExpenses extends AppCompatActivity {

    private DBHelper dbHelper;
    private Context context;
    private ExpensesDataPicker EDP;

    protected Button Data;
    private EditText CostTyp;
    private EditText Quantity;
    private EditText Price;
    private EditText Cost;
    private CheckBox CheckBoxPetrol;
    private InputFilter CheckBoxOnInPutFilter[] = new InputFilter[]{new DecimalDigitsInputFilter(15,3)};
    private InputFilter CheckBoxOffInPutFilter[] = new InputFilter[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_expenses);

        setTitle(R.string.Title_Cost_Accounting);

        Data = (Button) findViewById(R.id.buttonData);           //Дата
        CostTyp = (EditText) findViewById(R.id.editText2);       //Тип затрат
        Quantity = (EditText) findViewById(R.id.editText3);      //Количество
        Price = (EditText) findViewById(R.id.editText4);       //Цена
        Cost = (EditText) findViewById(R.id.editText5);         //Стоимость

        CheckBoxPetrol = (CheckBox) findViewById(R.id.checkBox);

        Quantity.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(15,3)});
        Price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(15,2)});
        Cost.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(15,2)});


        final Button Save =(Button) findViewById(R.id.button);

        context = this;

        EDP = new ExpensesDataPicker();
        EDP.ExpensesDataPicker(context,Data);

        Data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(ExpensesDataPicker.DIALOG_DATE);
            }
        });


        CheckBoxPetrol.setOnCheckedChangeListener(PetrolOnCheckedChangeListner);

        //Data.addTextChangedListener(new DataTextWatcher(Data));

        //Data.setText(getDataToday());

        //Data.setOnFocusChangeListener(DataOnFocusChangeListner);

        //Cost.addTextChangedListener(CostTextWatcher);
        Cost.setOnFocusChangeListener(CostOnFocusChangeListner);

        //Price.addTextChangedListener(PriceTextWatcher);
        Price.setOnFocusChangeListener(PriceOnFocusChangeListner);

        //Quantity.addTextChangedListener(QuantityTextWatcher);
        Quantity.setOnFocusChangeListener(QuantityOnFocusChangeListner);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidationCheck(CostTyp,Quantity,Price,Cost)) {

                    dbHelper = new DBHelper(context);
                    // создаем объект для данных
                    ContentValues cv = new ContentValues();

                    SQLiteDatabase db= dbHelper.getWritableDatabase();

                    String data =Data.getText().toString();
                    //String costtyp = CostTyp.getText().toString();
                    Double quantity = Double.valueOf(Quantity.getText().toString());
                    Double price = Double.valueOf(Price.getText().toString());
                    Double cost = Double.valueOf(Cost.getText().toString());

                    cv.put("Data",data);
                    //cv.put("CostType",costtyp);
                    cv.put("Quantity",quantity);
                    cv.put("Price",price);
                    cv.put("Cost",cost);
                    if(CheckBoxPetrol.isChecked()){
                        cv.put("Petrol",1);
                        cv.put("CostType","Бензин");
                        Double mileage = Double.valueOf(CostTyp.getText().toString());
                        cv.put("Mileage",mileage);
                    }else {
                        cv.put("Petrol",0);
                        String costtyp = CostTyp.getText().toString();
                        cv.put("CostType",costtyp);
                    }

                    db.insert("Expenses",null,cv);
                    dbHelper.close();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

    }

    protected Dialog onCreateDialog(int id){
        if(id==ExpensesDataPicker.DIALOG_DATE){
            DatePickerDialog DPD = new DatePickerDialog(this,EDP.myCallBack,EDP.Year,EDP.Month,EDP.Day);
            return DPD;
        }
        return super.onCreateDialog(id);
    }



    private  boolean ValidationCheck(EditText CostType,EditText Quantity,EditText Price,EditText Cost){
        if(CheckForText(CostType)&CheckForText(Quantity)&CheckForText(Price)&CheckForText(Cost)){
            if(CheckComposition(Price,Quantity,Cost)){
                                    return true;
            }else {
                //цена, количество, стоимость не соответствуют
                Toast.makeText(this, "Не коррекино введены цена или стоимость", Toast.LENGTH_LONG).show();
                return false;
            }
        }else {
            //одна или несколько колонок не заполнены
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private View.OnFocusChangeListener CostOnFocusChangeListner = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(CheckForText(Cost)){
                if (!CheckComposition(Price,Quantity,Cost)) {
                    if(CheckForText(Quantity)) {
                        Double cost = Double.valueOf(Cost.getText().toString());
                        Double quantity = Double.valueOf(Quantity.getText().toString());
                        Double price = (double) Math.round((cost / quantity) * 100) / 100;
                        //Price.removeTextChangedListener(PriceTextWatcher);
                        Price.setText(price.toString());
                        //Price.addTextChangedListener(PriceTextWatcher);
                    }else if(CheckForText(Price)){
                        Double cost = Double.valueOf(Cost.getText().toString());
                        Double price = Double.valueOf(Price.getText().toString());
                        Double quantity = (double) Math.round((cost / price) * 1000) / 1000;
                        //Quantity.removeTextChangedListener(QuantityTextWatcher);
                        Quantity.setText(quantity.toString());
                        //Quantity.addTextChangedListener(QuantityTextWatcher);
                    }
                }
            }else {
                //Cost.removeTextChangedListener(CostTextWatcher);
                Cost.setText("");
                //Cost.addTextChangedListener(CostTextWatcher);
            }
        }
    };
    private View.OnFocusChangeListener PriceOnFocusChangeListner = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(CheckForText(Price)){
                if (!CheckComposition(Price,Quantity,Cost)) {
                    if (CheckForText(Quantity)) {
                        Double SetCost = (double) Math.round(CompositionEditText(Price, Quantity)*100)/100;
                        //Cost.removeTextChangedListener(CostTextWatcher);
                        Cost.setText(SetCost.toString());
                        //Cost.addTextChangedListener(CostTextWatcher);
                    }

                }
            }else {
                //Cost.removeTextChangedListener(CostTextWatcher);
                Cost.setText("");
                //Cost.addTextChangedListener(CostTextWatcher);
                //Price.removeTextChangedListener(this);
                //Price.clearFocus();
                Price.setText("");
                //Price.addTextChangedListener(this);
                //Price.requestFocus();
            }
        }
    };
    private View.OnFocusChangeListener QuantityOnFocusChangeListner = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(CheckForText(Quantity)){
                if (!CheckComposition(Price,Quantity,Cost)) {
                    if (CheckForText(Price)) {
                        Double SetCost = (double) Math.round(CompositionEditText(Price, Quantity)*100)/100;
                        //Cost.removeTextChangedListener(CostTextWatcher);
                        Cost.setText(SetCost.toString());
                        //Cost.addTextChangedListener(CostTextWatcher);
                    }
                }
            }else {
                //Cost.removeTextChangedListener(CostTextWatcher);
                Cost.setText("");
                //Cost.addTextChangedListener(CostTextWatcher);
                //Quantity.removeTextChangedListener(this);
                Quantity.setText("");
                //Quantity.addTextChangedListener(this);
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener PetrolOnCheckedChangeListner = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                CostTyp.setHint(getString(R.string.Odometr));
                CostTyp.setFilters(CheckBoxOnInPutFilter);
                CostTyp.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }else{
                CostTyp.setHint(getString(R.string.Cost_Type));
                CostTyp.setFilters(CheckBoxOffInPutFilter);
                CostTyp.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            }
        }
    };

    private boolean CheckForText(EditText editText){
        if(editText.getText().toString().equals("")||editText.getText().toString().equals(".")){
            return false; // текст не заполнен
        }else {
            return true;  //  текст заполнен
        }
    }

    private Double CompositionEditText(EditText editText1, EditText editText2){
        Double FirstNumber = Double.valueOf(editText1.getText().toString());
        Double SecondNumber = Double.valueOf(editText2.getText().toString());
        return FirstNumber*SecondNumber;
    }

    //Проверяет равенство стоимоти и произведение цена на кол-во, если ячейки не заполнены возврашяет false
    private boolean CheckComposition (EditText price, EditText quantity, EditText cost){
        if(CheckForText(price)&CheckForText(quantity)&CheckForText(cost)) {
            Double Cost1 = CompositionEditText(price, quantity);
            Cost1 = (double) (Math.round(Cost1*100))/100;
            Double Cost2 = Double.valueOf(cost.getText().toString());
            return Double.compare(Cost1,Cost2)==0;
        }else {
            return false;
        }
    }
}
