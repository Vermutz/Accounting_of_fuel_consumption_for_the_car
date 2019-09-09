package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

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

    private EditText Data;
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

        setTitle("Учет расходов");

        Data = (EditText) findViewById(R.id.editText);           //Дата
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

        CheckBoxPetrol.setOnCheckedChangeListener(PetrolOnCheckedChangeListner);

        Data.addTextChangedListener(new DataTextWatcher(Data));

        Data.setText(getDataToday());

        Data.setOnFocusChangeListener(DataOnFocusChangeListner);

        Cost.addTextChangedListener(CostTextWatcher);
        Cost.setOnFocusChangeListener(CostOnFocusChangeListner);

        Price.addTextChangedListener(PriceTextWatcher);

        Quantity.addTextChangedListener(QuantityTextWatcher);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidationCheck(Data,CostTyp,Quantity,Price,Cost)) {

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



    private  boolean ValidationCheck(EditText Data,EditText CostType,EditText Quantity,EditText Price,EditText Cost){
        if(CheckForText(Data)&CheckForText(CostType)&CheckForText(Quantity)&CheckForText(Price)&CheckForText(Cost)){
            if(CheckComposition(Price,Quantity,Cost)){
                if(CheckData(Data)){
                    return true;
                }else {
                    //дата не корректна
                    Toast.makeText(this, "Не корректно введена дата", Toast.LENGTH_LONG).show();
                    return false;
                }
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
    private TextWatcher CostTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(CheckForText(Cost)){
            if (!CheckComposition(Price,Quantity,Cost)) {
                if(CheckForText(Quantity)) {
                    Double cost = Double.valueOf(Cost.getText().toString());
                    Double quantity = Double.valueOf(Quantity.getText().toString());
                    Double price = (double) Math.round((cost / quantity) * 100) / 100;
                    Price.removeTextChangedListener(PriceTextWatcher);
                    Price.setText(price.toString());
                    Price.addTextChangedListener(PriceTextWatcher);
                }else if(CheckForText(Price)){
                    Double cost = Double.valueOf(Cost.getText().toString());
                    Double price = Double.valueOf(Price.getText().toString());
                    Double quantity = (double) Math.round((cost / price) * 1000) / 1000;
                    Quantity.removeTextChangedListener(QuantityTextWatcher);
                    Quantity.setText(quantity.toString());
                    Quantity.addTextChangedListener(QuantityTextWatcher);
                }
            }
            }else {
                Cost.removeTextChangedListener(CostTextWatcher);
                Cost.setText("");
                Cost.addTextChangedListener(CostTextWatcher);
            }
        }
    };
    private TextWatcher PriceTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(CheckForText(Price)){
                if (!CheckComposition(Price,Quantity,Cost)) {
                    if (CheckForText(Quantity)) {
                        Double SetCost = (double) Math.round(CompositionEditText(Price, Quantity)*100)/100;
                        Cost.removeTextChangedListener(CostTextWatcher);
                        Cost.setText(SetCost.toString());
                        Cost.addTextChangedListener(CostTextWatcher);
                    }

                }
            }else {
                Cost.removeTextChangedListener(CostTextWatcher);
                Cost.setText("");
                Cost.addTextChangedListener(CostTextWatcher);
                Price.removeTextChangedListener(this);
                //Price.clearFocus();
                Price.setText("");
                Price.addTextChangedListener(this);
                //Price.requestFocus();
            }
        }
    };
    private TextWatcher QuantityTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(CheckForText(Quantity)){
                if (!CheckComposition(Price,Quantity,Cost)) {
                    if (CheckForText(Price)) {
                        Double SetCost = (double) Math.round(CompositionEditText(Price, Quantity)*100)/100;
                        Cost.removeTextChangedListener(CostTextWatcher);
                        Cost.setText(SetCost.toString());
                        Cost.addTextChangedListener(CostTextWatcher);
                    }
                }
            }else {
                Cost.removeTextChangedListener(CostTextWatcher);
                Cost.setText("");
                Cost.addTextChangedListener(CostTextWatcher);
                Quantity.removeTextChangedListener(this);
                Quantity.setText("");
                Quantity.addTextChangedListener(this);
            }
        }
    };

    private View.OnFocusChangeListener CostOnFocusChangeListner = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {

        }
    };
    private View.OnFocusChangeListener DataOnFocusChangeListner = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(!hasFocus) {
                String DataEditText = Data.getText().toString();
                if(DataEditText.length()<3&DataEditText.length()!=0){
                    StringBuilder datatoday = new StringBuilder(getDataToday());
                    datatoday.delete(0,2);
                    String CorrectData;
                    if(DataEditText.length()<2){
                        CorrectData = "0"+DataEditText+datatoday;
                    }else {
                        CorrectData=DataEditText+datatoday;
                    }
                    Data.setText(CorrectData);
                }
                if(DataEditText.length()<6&DataEditText.length()>3){
                    StringBuilder datatoday = new StringBuilder(getDataToday());
                    datatoday.delete(0,4);
                    String CorrectData;
                    if(DataEditText.length()<5){
                        CorrectData =""+RemoveDataSpecialCharacters(DataEditText).insert(2,0)+datatoday;
                    }else {
                        CorrectData=""+RemoveDataSpecialCharacters(DataEditText)+datatoday;
                    }
                    Data.setText(CorrectData);
                }
                if(DataEditText.length()>6){
                    String CorrectData="";
                    StringBuilder datatoday = new StringBuilder(getDataToday());
                    StringBuilder DataEditTextStringBilder = RemoveDataSpecialCharacters(DataEditText);
                    switch(DataEditText.length()){
                        case 7:
                            StringBuilder Year =new StringBuilder(""
                                    +datatoday.charAt(4)
                                    +datatoday.charAt(5)
                                    +datatoday.charAt(6)
                                    +DataEditTextStringBilder.charAt(4));
                            if(Integer.valueOf(""+Year)<=Integer.valueOf(""+datatoday.delete(0,4))) {
                                CorrectData = "" + DataEditTextStringBilder.delete(4, 5)+Year;
                            }else{
                                int cen =Character.getNumericValue(Year.charAt(2));
                                if(cen!=0) {
                                    cen--;
                                    Year.replace(2,3,Integer.toString(cen));
                                }else{
                                    cen=9;
                                    int cen2 =Character.getNumericValue(Year.charAt(1));
                                    if (cen2!=0){
                                        cen2--;
                                        Year.replace(1,3,(Integer.toString(cen2)+Integer.toString(cen)));
                                    }else {
                                        cen2=9;
                                        int cen3 = Character.getNumericValue(Year.charAt(0));
                                        cen3--;
                                        Year.replace(0,3,(Integer.toString(cen3)+Integer.toString(cen2)+Integer.toString(cen)));
                                    }
                                }
                                CorrectData = ""+DataEditTextStringBilder.delete(4,5)+Year;
                            }
                            break;
                        case 8:
                            StringBuilder Year2 =new StringBuilder(""
                                    +datatoday.charAt(4)
                                    +datatoday.charAt(5)
                                    +DataEditTextStringBilder.charAt(4)
                                    +DataEditTextStringBilder.charAt(5));
                            if(Integer.valueOf(""+Year2)<=Integer.valueOf(""+datatoday.delete(0,4))) {
                                CorrectData = "" + DataEditTextStringBilder.delete(4, 6)+Year2;
                            }else{
                                int cen =Character.getNumericValue(Year2.charAt(1));
                                if(cen!=0){
                                cen--;
                                    Year2.replace(1,2,Integer.toString(cen));
                                }else {
                                    cen=9;
                                    int cen2 = Character.getNumericValue(Year2.charAt(0));
                                    cen2--;
                                    Year2.replace(0,2,(Integer.toString(cen2)+Integer.toString(cen)));
                                };
                                CorrectData = ""+DataEditTextStringBilder.delete(4,6)+Year2;
                            }
                            break;
                        case 9:

                            StringBuilder Year3 =new StringBuilder(""
                                    +datatoday.charAt(4)
                                    +DataEditTextStringBilder.charAt(4)
                                    +DataEditTextStringBilder.charAt(5)
                                    +DataEditTextStringBilder.charAt(6));
                            if(Integer.valueOf(""+Year3)<=Integer.valueOf(""+datatoday.delete(0,4))) {
                                CorrectData = "" + DataEditTextStringBilder.delete(4, 7)+Year3;
                            }else{
                                int cen =Character.getNumericValue(Year3.charAt(0));

                                    cen--;
                                    Year3.replace(0,1,Integer.toString(cen));

                                CorrectData = ""+DataEditTextStringBilder.delete(4,7)+Year3;
                            }
                            break;
                        case 10:
                            CorrectData=""+DataEditTextStringBilder;
                            break;
                        default:break;
                    }
                    Data.setText(CorrectData);

                }
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener PetrolOnCheckedChangeListner = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                CostTyp.setHint("Одометр (км)");
                CostTyp.setFilters(CheckBoxOnInPutFilter);
                CostTyp.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }else{
                CostTyp.setHint("Тип затрат");
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
    private boolean CheckData(EditText Data){
        String data =   Data.getText().toString();
        if(data.length()==10) {
            StringBuilder Day = new StringBuilder();
            StringBuilder Month = new StringBuilder();
            StringBuilder Year = new StringBuilder();

            if(data.charAt(0)==(char)48){
             Day.append(data.charAt(1));
            }else{
            Day.append(data.charAt(0));
            Day.append(data.charAt(1));
            }
            if(data.charAt(3)==(char)48){
                Month.append(data.charAt(4));
            }else {
                Month.append(data.charAt(3));
                Month.append(data.charAt(4));
            }

            Year.append(data.charAt(6));
            Year.append(data.charAt(7));
            Year.append(data.charAt(9));
            Year.append(data.charAt(9));

            if((Integer.parseInt(Day.toString())<=31)
                    &(Integer.parseInt(Day.toString())>0)
                    &(Integer.parseInt(Month.toString())<=12)
                    &(Integer.parseInt(Month.toString())>0)
                    &(Integer.parseInt(Year.toString())>0)){
                return true;
            }else {
                return false;
            }
        }else{
         return false;
        }
    }
    private String getDataToday(){
        Calendar c = Calendar.getInstance();
        String days,mounths,years,DataToday;
        if(c.get(Calendar.DAY_OF_MONTH)>9){
            days = ""+c.get(Calendar.DAY_OF_MONTH);
        }else{
            days="0"+c.get(Calendar.DAY_OF_MONTH);
        }
        if(c.get(Calendar.MONTH)>9){
            mounths = ""+c.get(Calendar.MONTH);
        }else{
            mounths="0"+c.get(Calendar.MONTH);
        }
        years =""+c.get(Calendar.YEAR);
        DataToday = days+mounths+years;
        return DataToday;
    };

    private StringBuilder RemoveDataSpecialCharacters(String str){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++)
        {
            if ((str.charAt(i) >= '0' && str.charAt(i) <= '9'))
            {
                sb.append(str.charAt(i));
            }
        }

        return sb;
    }

}
