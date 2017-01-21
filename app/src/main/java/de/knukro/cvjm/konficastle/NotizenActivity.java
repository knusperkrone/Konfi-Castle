package de.knukro.cvjm.konficastle;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;

public class NotizenActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etContent;
    private String time, day, initText;
    private DbOpenHelper dbOpenHelper;
    private boolean wasExpanded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notizen);

        findViewById(R.id.btn_cancel).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

        etContent = (EditText) findViewById(R.id.etContent);

        Intent values = getIntent();
        TextView titleView = (TextView) findViewById(R.id.notiz_Title);
        String title = values.getStringExtra("title");
        titleView.setText(title);
        day = values.getStringExtra("day");
        time = values.getStringExtra("time");
        wasExpanded = values.getBooleanExtra("expandend", false);
        if (title.startsWith("Notiz")) {
            initText = values.getStringExtra("content");
            etContent.setText(initText.substring(9));
        }
        dbOpenHelper = DbOpenHelper.getInstance();
    }

    private boolean updateNote() {
        String content = etContent.getText().toString();
        if (content.length() > 0) {
            dbOpenHelper.updateNote(this, day, time, initText, content);
            return true;
        } else if (content.length() == 0) {
            Toast.makeText(this, "Willst du die Notiz nicht lieber löschen?", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean deleteNote() {
        String content = etContent.getText().toString();
        if (content.length() > 0) {
            dbOpenHelper.deleteNote(this, day, time, content);
            return true;
        }
        Toast.makeText(this, "Da gibt es nichts zum löschen!", Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean saveNote() {
        String content = etContent.getText().toString();
        if (content.length() > 0) {
            try {
                dbOpenHelper.putNote(this, day, time, content);
            } catch (SQLException e) {
                Toast.makeText(this, "Oh, ein Fehler! Gibt es die Notiz schon?", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } else {
            Toast.makeText(this, "Eine leere Notiz speichern?", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (!wasExpanded) {
                    ExpandableTermin.toExpand = "";
                }
                finish();
                break;
            case R.id.btn_delete:
                if (deleteNote()) {
                    Toast.makeText(this, "Notiz gelöscht", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.btn_save:
                if (initText != null && updateNote() || saveNote()) { //Old or new notice?
                    Toast.makeText(this, "Notiz gespeichert!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            default:
        }
    }
}
