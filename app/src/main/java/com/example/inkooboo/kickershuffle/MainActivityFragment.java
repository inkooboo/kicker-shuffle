package com.example.inkooboo.kickershuffle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Chronometer;
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
    private Chronometer m_chronometr;

    class Team {
        public AutoCompleteTextView defense = null;
        public AutoCompleteTextView attack = null;
    }

    private Team m_white = new Team();
    private Team m_yellow = new Team();

    private List<String> m_names = null;
    private static final String NAMES_KEY = "names";

    void reshuffleTeams() {
        Team newWhite = new Team();
        Team newYellow = new Team();

        // exchange teams
        int seed1 = (int)(Math.random() * 4);

        if (seed1 == 0) {
            newWhite.defense = m_yellow.defense;
            newWhite.attack = m_white.attack;

            newYellow.defense = m_white.defense;
            newYellow.attack = m_yellow.attack;

        } else if (seed1 == 1) {
            newWhite.defense = m_yellow.attack;
            newWhite.attack = m_white.attack;

            newYellow.attack = m_white.defense;
            newYellow.defense = m_yellow.defense;

        } else if (seed1 == 2) {
            newWhite.attack = m_yellow.defense;
            newWhite.defense = m_white.defense;

            newYellow.defense = m_white.attack;
            newYellow.attack = m_yellow.attack;
        } else if (seed1 == 3) {
            newWhite.attack = m_yellow.attack;
            newWhite.defense = m_white.defense;

            newYellow.attack = m_white.attack;
            newYellow.defense = m_yellow.defense;
        }

        AutoCompleteTextView tmp = null;

        // shuffle white team
        int seed2 = (int)(Math.random() * 2);
        if (seed2 == 0) {
            tmp = newWhite.attack;
            newWhite.attack = newWhite.defense;
            newWhite.defense = tmp;
        }

        // shuffle yellow team
        int seed3 = (int)(Math.random() * 2);
        if (seed3 == 0) {
            tmp = newYellow.attack;
            newYellow.attack = newYellow.defense;
            newYellow.defense = tmp;
        }

        m_white = newWhite;
        m_yellow = newYellow;
    }

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
        m_chronometr = (Chronometer)view.findViewById(R.id.chronometer);
        m_input.add((AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView1));
        m_input.add((AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView2));
        m_input.add((AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView3));
        m_input.add((AutoCompleteTextView)view.findViewById(R.id.autoCompleteTextView4));

        // initial shuffle
        Collections.shuffle(m_input);
        // make teams
        m_yellow.defense = m_input.get(0);
        m_yellow.attack = m_input.get(1);
        m_white.defense = m_input.get(2);
        m_white.attack = m_input.get(3);
        // reshuffle again
        reshuffleTeams();

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

                    reshuffleTeams();

                    m_result.setText(
                            "Команда белых:\n" +
                                    "       защита - " + m_white.defense.getText().toString() + "\n" +
                                    "  нападение - " + m_white.attack.getText().toString() + "\n\n" +
                                    "Команда жЁлтых:\n" +
                                    "       защита - " + m_yellow.defense.getText().toString() + "\n" +
                                    "  нападение - " + m_yellow.attack.getText().toString()
                    );

                    m_chronometr.setBase(SystemClock.elapsedRealtime());
                    m_chronometr.start();
                }
            }
        );

        return view;
    }

}
