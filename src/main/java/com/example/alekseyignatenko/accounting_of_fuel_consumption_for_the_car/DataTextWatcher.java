package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class DataTextWatcher implements TextWatcher {

    public EditText editText;
    private Integer BeforeChanged;
    private Integer AfterChanged;



    public DataTextWatcher(EditText ET){
        super();
        editText=ET;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        BeforeChanged = editText.length();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        AfterChanged = editText.length();
        int CursorPosition;
        if(editText.getText().toString().length()>10){
            CursorPosition = editText.getSelectionStart();
            editText.removeTextChangedListener(this);
            editText.setText(editText.getText().subSequence(0, editText.getText().length()-1));
            editText.addTextChangedListener(this);
            editText.setSelection(CursorPosition-1);
        }

        //if(AfterChanged.length()>BeforeChanged.length()){
            //if((AfterChanged.length()==2)||(AfterChanged.length()==5)){
               // editText.removeTextChangedListener(this);
                //editText.setText(AfterChanged+".");
                //editText.addTextChangedListener(this);
               // editText.setSelection(editText.getText().toString().length());
           // }
       // }else {
        //    if((AfterChanged.length()==2)||(AfterChanged.length()==5)){
          //      editText.removeTextChangedListener(this);
          //      editText.setText(editText.getText().subSequence(0, editText.getText().length()-1));
           //     editText.addTextChangedListener(this);
           //     editText.setSelection(editText.getText().toString().length());
           // }
        //}

        StringBuilder Data = RemoveSpecialCharacters(editText.getText().toString());

        if(Data.length()>=3) {
            Data.insert(2, ".");
        }
        if(Data.length()>=6) {
            Data.insert(5,".");
        }
        CursorPosition = editText.getSelectionStart();
        editText.removeTextChangedListener(this);
        editText.setText(Data.toString());
        editText.addTextChangedListener(this);
        if(AfterChanged>BeforeChanged){
        if((Data.length()==4||Data.length()==7)&(CursorPosition<Data.length())){
         editText.setSelection(CursorPosition+1);
        }else{
            editText.setSelection(CursorPosition);
        }
        }else{
            if((Data.length()==2||Data.length()==5)&((CursorPosition-1)>=0)){
                editText.setSelection(CursorPosition-1);
            }else{
                editText.setSelection(CursorPosition);
            }
        }
    }

    private StringBuilder RemoveSpecialCharacters(String str){
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
