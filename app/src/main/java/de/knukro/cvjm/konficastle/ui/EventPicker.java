package de.knukro.cvjm.konficastle.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import java.util.HashMap;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.helper.DbOpenHelper;

public class EventPicker extends DialogPreference {

    private NumberPicker event_picker, instanz_picker;
    private int event_value = -1, instanz_value = -1;

    private String[] event_val;
    private HashMap<String, String[]> instanzen;

    private final SharedPreferences sp;

    public EventPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.settings_event_spinner);
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        DbOpenHelper dbHelper = DbOpenHelper.getInstance();
        event_val = dbHelper.getEvents();
        instanzen = dbHelper.getInstanzen();

        event_picker = (NumberPicker) view.findViewById(R.id.picker_event);
        instanz_picker = (NumberPicker) view.findViewById(R.id.picker_instanz);

        event_picker.setMinValue(0);
        event_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        event_picker.setMaxValue(event_val.length - 1);
        event_picker.setWrapSelectorWheel(true);
        event_picker.setDisplayedValues(event_val);
        event_picker.setValue(getEventValue());
        event_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {
                //Update instanzen View after Scrolling
                String[] instanzen_val = instanzen.get(event_val[event_picker.getValue()]);
                instanz_picker.setDisplayedValues(null);
                instanz_picker.setMaxValue(instanzen_val.length - 1);
                instanz_picker.setDisplayedValues(instanzen_val);
                instanz_picker.setValue(0);
            }
        });

        instanz_picker.setMinValue(0);
        String[] instanzen_val = instanzen.get(event_val[event_picker.getValue()]);
        instanz_picker.setMaxValue(instanzen_val.length - 1);
        instanz_picker.setDisplayedValues(instanzen_val);
        instanz_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        instanz_picker.setWrapSelectorWheel(true);
        instanz_picker.setValue(getInstanzValue());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            event_picker.clearFocus();
            instanz_picker.clearFocus();
            event_value = event_picker.getValue();
            instanz_value = instanz_picker.getValue();
            callChangeListener(event_val[event_value]);
            Context context = getContext();
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt("event_spinner", event_value);
            edit.putString(context.getString(R.string.key_event), event_val[event_value]);
            edit.putString(context.getString(R.string.key_instanz), String.valueOf(instanz_value + 1));
            edit.apply();
        }
    }

    private int getEventValue() {
        if (event_value == -1) {
            for (int i = 0; i < event_val.length; i++) {
                if (event_val[i].equals(sp.getString(getContext().getString(R.string.key_event), "Konfi Castle"))) {
                    event_value = i;
                    break;
                }
            }
        }
        return event_value;
    }

    private int getInstanzValue() {
        if (instanz_value == -1)
            instanz_value = Integer.valueOf(sp.getString(getContext().getString(R.string.key_instanz), "1")) - 1;
        return instanz_value;
    }

}
