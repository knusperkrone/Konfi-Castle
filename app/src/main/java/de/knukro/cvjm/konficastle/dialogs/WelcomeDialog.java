package de.knukro.cvjm.konficastle.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import de.knukro.cvjm.konficastle.R;
import de.knukro.cvjm.konficastle.SettingsActivity;


public class WelcomeDialog extends DialogFragment {

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater i = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(i.inflate(R.layout.dialog_welcome, null, false));
        builder.setIcon(R.drawable.ic_menu_send);
        builder.setTitle(getString(R.string.dialog_welcome_title));
        builder.setPositiveButton(getString(R.string.dialog_welcome_button_pos), null);
        if (!getTag().equals("settings"))
            builder.setNegativeButton(getString(R.string.dialog_welcome_button_neg), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                }
            });
        return builder.create();
    }

}
