package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Locale;

public class LauncherActivity extends AppCompatActivity {

    public static ExpensesDataPicker EDP = new ExpensesDataPicker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = sharedPreferences.getString(SettingsFragment.LANG,SettingsFragment.DEFAULT);

        setLenguage(lang);

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
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (item.getItemId()){
            case R.id.Settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.launch_frame,settingsFragment)
                        .addToBackStack(SettingsFragment.class.getName())
                        .commit();
                break;
            case R.id.AboutProgram:
                AboutProgramFragment aboutProgramFragment = new AboutProgramFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.launch_frame,aboutProgramFragment)
                        .addToBackStack(AboutProgramFragment.class.getName())
                        .commit();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setLenguage(String Lenguage){
        Locale locale = new Locale(Lenguage);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale=locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
    }
}
