package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;

public class CompositionTextWatcher implements TextWatcher {

    private EditText Quantity;
    private EditText Price;
    private EditText Cost;
    private String ChangedET="",QUANTITY="QUANTITY",PRICE="PRICE",COST="COST"; //запоминаем в какой EditText вносим расчитаное значение

    public CompositionTextWatcher(EditText price,EditText quantity, EditText cost){
        Price = price;
        Quantity = quantity;
        Cost = cost;
        Price.setOnFocusChangeListener(onFocusChangeListener);
        Quantity.setOnFocusChangeListener(onFocusChangeListener);
        Cost.setOnFocusChangeListener(onFocusChangeListener);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(Cost.hasFocus()){
            if(CheckForText(Cost)){             //Проверим есть ли текст или осталась точка. заменяем току на пустую строку
                Cost.setGravity(Gravity.RIGHT);
                CostEditTextChanged();
            }else {
                Cost.setGravity(Gravity.LEFT);
                RemoveText(Cost);
            }
        }else if (Quantity.hasFocus()){
            if(CheckForText(Quantity)){          //Если нет цены или количество то убераем стоимость
                Quantity.setGravity(Gravity.RIGHT);
                QuantityEditTextChenged();
            }else {
                Quantity.setGravity(Gravity.LEFT);
                Cost.setGravity(Gravity.LEFT);
                RemoveText(Quantity);
                RemoveText(Cost);
            }
        }else if (Price.hasFocus()){
            if(CheckForText(Price)){
                Price.setGravity(Gravity.RIGHT);
                PriceEditTextChanged();
            }else {
                Price.setGravity(Gravity.LEFT);
                Cost.setGravity(Gravity.LEFT);
                RemoveText(Price);
                RemoveText(Cost);
            }
        }
    }

    //Логика расчета цены количества и стоимости
    private void CostEditTextChanged(){
        if(CheckForText(Price)&CheckForText(Quantity)){                                  //если заполнены цена,количество и стоимость
            if(ChangedET==QUANTITY){                                                     //то проверяем куда раньше вводились изменения
                QuantityChange();                                                        //поумолчанию изменяем цену
            }else {
                PriceChange();
            }
        }else if(CheckForText(Price)){                                                  //если заполнены цена и стоимость считаем количество
            QuantityChange();
        }else if(CheckForText(Quantity)){                                               //если заполнены количество и стоимость считаем цену
            PriceChange();
        }
    }
    private void PriceEditTextChanged(){
        if(CheckForText(Cost)&CheckForText(Quantity)){
            if(ChangedET==QUANTITY){
                QuantityChange();
            }else{
                CostChange();
            }
        }else if(CheckForText(Cost)){                                        //Если меняли цену продолжаем менять цену
            QuantityChange();                                               //Если заполнено количество считаем стоимость
        }else if(CheckForText(Quantity)){
            CostChange();
        }
    }
    private void QuantityEditTextChenged(){
        if(CheckForText(Cost)&CheckForText(Price)){
            if(ChangedET==PRICE){
                PriceChange();
            }else{
                CostChange();
            }
        }else if(CheckForText(Cost)){                         //Если меняли цену продолжаем менять цену
            PriceChange();                                                 //Если заполнена цена считаем стоимость
        }else if(CheckForText(Price)){
            CostChange();
        }
    }

    //При смене фокуса убераем напоминание
    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            ChangedET ="";
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

    //расчет количества цены и стоимости
    private void PriceChange(){
        Double cost = Double.valueOf(Cost.getText().toString());
        Double quantity = Double.valueOf(Quantity.getText().toString());
        Double price = (double) Math.round((cost / quantity) * 100) / 100;
        Price.removeTextChangedListener(this);
        Price.setText(price.toString());
        Price.addTextChangedListener(this);
        ChangedET = PRICE;
        Price.setGravity(Gravity.RIGHT);

    }
    private  void QuantityChange(){
        Double cost = Double.valueOf(Cost.getText().toString());
        Double price = Double.valueOf(Price.getText().toString());
        Double quantity = (double) Math.round((cost / price) * 1000) / 1000;
        Quantity.removeTextChangedListener(this);
        Quantity.setText(quantity.toString());
        Quantity.addTextChangedListener(this);
        ChangedET = QUANTITY;
        Quantity.setGravity(Gravity.RIGHT);
    }
    private void CostChange(){
        Double SetCost = (double) Math.round(CompositionEditText(Price, Quantity) * 100) / 100;
        Cost.removeTextChangedListener(this);
        Cost.setText(SetCost.toString());
        Cost.addTextChangedListener(this);
        ChangedET = COST;
        Cost.setGravity(Gravity.RIGHT);
    }
    private void RemoveText(EditText editText){
        editText.removeTextChangedListener(this);
        editText.setText("");
        editText.addTextChangedListener(this);
    }
}
