package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class LauncherActivity extends AppCompatActivity {

    public static ExpensesDataPicker EDP = new ExpensesDataPicker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        //EDP = new ExpensesDataPicker();

        FragmentManager fragmentManager = getSupportFragmentManager();
        ExpensesListFragment expensesListFragment = new ExpensesListFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.launch_frame,expensesListFragment)
                .addToBackStack(ExpensesListFragment.class.getName())
                .commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else {
            fragmentManager.popBackStack();
        }
    }

    protected Dialog onCreateDialog(int id){
        if(id==ExpensesDataPicker.DIALOG_DATE){
            DatePickerDialog DPD = new DatePickerDialog(this,EDP.myCallBack,EDP.Year,EDP.Month,EDP.Day);
            return DPD;
        }
        return super.onCreateDialog(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Settings:break;

            case R.id.AboutProgram:break;
        }
        return super.onOptionsItemSelected(item);
    }
}
