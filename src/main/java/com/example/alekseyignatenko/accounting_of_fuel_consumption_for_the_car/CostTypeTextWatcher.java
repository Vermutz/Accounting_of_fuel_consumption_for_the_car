package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.EditText;

public class CostTypeTextWatcher implements TextWatcher {

    private CheckBox CheckBox;
    private EditText CostType;

    public CostTypeTextWatcher(CheckBox checkBox, EditText costType){
        CheckBox =checkBox;
        CostType=costType;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(CheckBox.isChecked()){
            if(CheckForText(CostType)){
                CostType.setGravity(Gravity.RIGHT);
            }else{
                CostType.setGravity(Gravity.LEFT);
            }
        }else {
            CostType.setGravity(Gravity.LEFT);
        }
    }

    private boolean CheckForText(EditText editText){
        if(editText.getText().toString().equals("")||editText.getText().toString().equals(".")){
            return false; // текст не заполнен
        }else {
            return true;  //  текст заполнен
        }
    }
}
