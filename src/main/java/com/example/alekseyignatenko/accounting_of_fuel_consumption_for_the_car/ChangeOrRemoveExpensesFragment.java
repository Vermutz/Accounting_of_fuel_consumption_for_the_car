package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeOrRemoveExpensesFragment extends Fragment {

    private DBHelper dbHelper;
    //private Context context;
    private Integer ID;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_or_remove_expenses,container,false);


        getActivity().setTitle(R.string.Title_Change_Or_Remove_Expenses);

        Data = (Button) view.findViewById(R.id.ACOREButtonData);           //Дата
        CostTyp = (EditText) view.findViewById(R.id.ACOREeditText2);       //Тип затрат
        Quantity = (EditText) view.findViewById(R.id.ACOREeditText3);      //Количество
        Price = (EditText) view.findViewById(R.id.ACOREeditText4);       //Цена
        Cost = (EditText) view.findViewById(R.id.ACOREeditText5);         //Стоимость

        CheckBoxPetrol = (CheckBox)view.findViewById(R.id.checkBox2);

        Quantity.setGravity(Gravity.RIGHT);
        Price.setGravity(Gravity.RIGHT);
        Cost.setGravity(Gravity.RIGHT);

        final Button Remove =(Button) view.findViewById(R.id.ACOREbutton);
        final Button Change =(Button) view.findViewById(R.id.ACOREbutton2);

        ID = getArguments().getInt("ID");

        Quantity.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(15,3)});
        Price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(15,2)});
        Cost.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(15,2)});

        //context = this;

        EDP = new ExpensesDataPicker();
        String SQLDATA = "";

        dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Expenses",null,"id = ?",new String[]{ID.toString()},null,null,null);
        if(cursor.moveToFirst()){
            SQLDATA = cursor.getString(cursor.getColumnIndex("Data"));
            if(cursor.getInt(cursor.getColumnIndex("Petrol"))==1){
                CheckBoxPetrol.setChecked(true);
                CostTyp.setHint(getString(R.string.Odometr));
                CostTyp.setFilters(CheckBoxOnInPutFilter);
                CostTyp.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                CostTyp.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex("Mileage"))));
            }else{
                CheckBoxPetrol.setChecked(false);
                CostTyp.setHint(getString(R.string.Cost_Type));
                CostTyp.setFilters(CheckBoxOffInPutFilter);
                CostTyp.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                CostTyp.setText(cursor.getString(cursor.getColumnIndex("CostType")));
            }
            Quantity.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex("Quantity"))));
            Price.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex("Price"))));
            Cost.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex("Cost"))));
        }
        dbHelper.close();

        EDP.ExpensesDataPicker(getActivity(),SQLDATA,Data);

        Data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().showDialog(ExpensesDataPicker.DIALOG_DATE);
            }
        });

        CompositionTextWatcher compositionTextWatcher = new CompositionTextWatcher(Price,Quantity,Cost);
        CostTypeTextWatcher costTypeTextWatcher = new CostTypeTextWatcher(CheckBoxPetrol,CostTyp);

        Price.addTextChangedListener(compositionTextWatcher);
        Quantity.addTextChangedListener(compositionTextWatcher);
        Cost.addTextChangedListener(compositionTextWatcher);

        CostTyp.addTextChangedListener(costTypeTextWatcher);

        CheckBoxPetrol.setOnCheckedChangeListener(PetrolOnCheckedChangeListner);


        Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = new DBHelper(getActivity());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("Expenses","id ="+ID,null);
                dbHelper.close();
                Intent intent = new Intent();
                //setResult(RESULT_OK, intent);
                //finish();
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ValidationCheck(CostTyp,Quantity,Price,Cost)){
                    dbHelper = new DBHelper(getActivity());
                    ContentValues cv = new ContentValues();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

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

                    db.update("Expenses",cv,"id = ?",new String[]{ID.toString()});
                    dbHelper.close();
                    Intent intent = new Intent();
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                }
            }
        });
        return view;
    }

    private CompoundButton.OnCheckedChangeListener PetrolOnCheckedChangeListner = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                CostTyp.setText("");
                CostTyp.setHint(getString(R.string.Odometr));
                CostTyp.setFilters(CheckBoxOnInPutFilter);
                CostTyp.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            }else{
                CostTyp.setText("");
                CostTyp.setHint(getString(R.string.Cost_Type));
                CostTyp.setFilters(CheckBoxOffInPutFilter);
                CostTyp.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            }
        }
    };

    private  boolean ValidationCheck(EditText CostType,EditText Quantity,EditText Price,EditText Cost){
        if(CheckForText(CostType)&CheckForText(Quantity)&CheckForText(Price)&CheckForText(Cost)){

                    return true;

        }else {
            //одна или несколько колонок не заполнены
            Toast.makeText(getActivity(), getString(R.string.Validation_Check_Empty_Field), Toast.LENGTH_LONG).show();
            return false;
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
