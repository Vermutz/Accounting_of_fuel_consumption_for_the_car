package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewExpensesFragment extends Fragment {

    private DBHelper dbHelper;

    protected Button Data;
    private EditText CostTyp;
    private EditText Quantity;
    private EditText Price;
    private EditText Cost;
    private CheckBox CheckBoxPetrol;
    private InputFilter CheckBoxOnInPutFilter[] = new InputFilter[]{new DecimalDigitsInputFilter(15,3)};
    private InputFilter CheckBoxOffInPutFilter[] = new InputFilter[]{};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_expenses,container,false);

        getActivity().setTitle(R.string.title_activity_add_new_expenses);

        Data = (Button) view.findViewById(R.id.buttonData);           //Дата
        CostTyp = (EditText) view.findViewById(R.id.editText2);       //Тип затрат
        Quantity = (EditText) view.findViewById(R.id.editText3);      //Количество
        Price = (EditText) view.findViewById(R.id.editText4);       //Цена
        Cost = (EditText) view.findViewById(R.id.editText5);         //Стоимость

        CheckBoxPetrol = (CheckBox) view.findViewById(R.id.checkBox);

        Quantity.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(15,3)});
        Price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(15,2)});
        Cost.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(15,2)});


        final Button Save =(Button) view.findViewById(R.id.button);



        CompositionTextWatcher compositionTextWatcher = new CompositionTextWatcher(Price,Quantity,Cost);
        LauncherActivity.EDP.ExpensesDataPicker(getActivity(),Data);

        Data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().showDialog(ExpensesDataPicker.DIALOG_DATE);
            }
        });

        CheckBoxPetrol.setOnCheckedChangeListener(PetrolOnCheckedChangeListner);
        Price.addTextChangedListener(compositionTextWatcher);
        Quantity.addTextChangedListener(compositionTextWatcher);
        Cost.addTextChangedListener(compositionTextWatcher);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidationCheck(CostTyp,Quantity,Price,Cost)) {

                    dbHelper = new DBHelper(getActivity());
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
                    //setResult(RESULT_OK, intent);
                    //finish();
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });
        return view;
    }

    private  boolean ValidationCheck(EditText CostType,EditText Quantity,EditText Price,EditText Cost){
        if(CheckForText(CostType)&CheckForText(Quantity)&CheckForText(Price)&CheckForText(Cost)){
                                    return true;
        }else {
            //одна или несколько колонок не заполнены
            Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_LONG).show();
            return false;
        }
    }

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

}
