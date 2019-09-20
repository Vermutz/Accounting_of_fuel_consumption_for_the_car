package com.example.alekseyignatenko.accounting_of_fuel_consumption_for_the_car;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private Button LanguageApply;
    private RadioGroup RadioGroupLanguageChoose;
    private SharedPreferences sharedPreferences;
    private RadioButton RUS,EN;
    private String lang;
    protected static String DEFAULT="default",LANG="lang";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings,container,false);
        getActivity().setTitle(R.string.Menu_Settings);

        LanguageApply = view.findViewById(R.id.buttonLanguageApply);
        RadioGroupLanguageChoose = view.findViewById(R.id.radioGroupLanguageChoose);
        RUS = view.findViewById(R.id.radioButtonLanguageRus);
        EN = view.findViewById(R.id.radioButtonLanguageEN);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        lang = sharedPreferences.getString(LANG,DEFAULT);


        if(lang.equals(DEFAULT)){
            EN.setChecked(true);
        }else{
            RUS.setChecked(true);
        }

        RadioGroupLanguageChoose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonLanguageRus:
                        lang="ru";
                        break;
                    case R.id.radioButtonLanguageEN:
                        lang="default";
                        break;
                }
            }
        });

        LanguageApply.setOnClickListener(OnLanguangeApplyClickListner);

        return view;
    }

    private View.OnClickListener OnLanguangeApplyClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            sharedPreferences.edit().putString(LANG,lang).commit();

            System.exit(0);
        }
    };

}
