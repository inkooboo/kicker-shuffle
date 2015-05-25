package com.example.inkooboo.kickershuffle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MainActivityFragment extends Fragment {

    private Button m_button;
    private TextView m_result;
    private List<AutoCompleteTextView> m_input = new ArrayList<AutoCompleteTextView>();


    private List<String> m_names = null;
    private static final String NAMES_KEY = "names";

    public static void setStringArrayPref(Context context, String key, List<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (String value : values) {
            a.put(value);
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
            }
        }
        return urls;
    }

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        m_button = (Button)view.findViewById(R.id.button);
        m_result = (TextView)view.findViewById(R.id.textView2);
        m_input.add((AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView1));
        m_input.add((AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView2));
        m_input.add((AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView3));
        m_input.add((AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView4));

        m_names = getStringArrayPref(getActivity(), NAMES_KEY);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, m_names);
        for (AutoCompleteTextView textView : m_input) {
            textView.setAdapter(adapter);
            textView.setThreshold(1);
        }

        m_button.setOnClickListener(
            new Button.OnClickListener() {
                public void onClick(View v) {
                    for (AutoCompleteTextView textView : m_input) {
                        final String name = textView.getText().toString();
                        if (!m_names.contains(name)) {

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                                private final String m_name = name;

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            m_names.add(name);
                                            setStringArrayPref(getActivity(), NAMES_KEY, m_names);
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            break;
                                    }
                                }
                            };


                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Добавить имя `" + name + "` в постоянный список игроков?").setPositiveButton("Да", dialogClickListener)
                                    .setNegativeButton("Нет", dialogClickListener).show();
                        }
                    }

                    Collections.shuffle(m_input);
                    m_result.setText(
                        "Команда белых:\n" +
                        "       защита - " + m_input.get(0).getText().toString() + "\n" +
                        "  нападение - " + m_input.get(1).getText().toString() + "\n\n" +
                        "Команда желтых:\n" +
                        "       защита - " + m_input.get(2).getText().toString() + "\n" +
                        "  нападение - " + m_input.get(3).getText().toString()
                    );
                }
            }
        );

        return view;
    }

}
