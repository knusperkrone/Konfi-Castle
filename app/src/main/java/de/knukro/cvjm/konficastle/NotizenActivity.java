package de.knukro.cvjm.konficastle;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;

import de.knukro.cvjm.konficastle.helper.DbOpenHelper;
import de.knukro.cvjm.konficastle.structs.ExpandableTermin;

public class NotizenActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etContent;
    private String time, day, initText;
    private DbOpenHelper dbOpenHelper;
    private int parentPosition;


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
        parentPosition = values.getIntExtra("parent", -1);
        initText = values.getStringExtra("content");
        if (title.startsWith("Notiz")) {
            etContent.setText(initText.substring(9));
            etContent.setSelection(etContent.getText().toString().length());
        }
        dbOpenHelper = DbOpenHelper.getInstance();
    }

    private boolean updateNote() {
        String content = etContent.getText().toString();
        if (content.length() > 0) {
            dbOpenHelper.updateNote(this, day, time, initText, content);
            Toast.makeText(this, getString(R.string.activity_notiz_done_update), Toast.LENGTH_SHORT).show();

            ExpandableRecyclerAdapter adapter = SharedValues.getAdapter();
            adapter.notifyChildChanged(parentPosition,
                    ((ExpandableTermin) adapter.getParentList().get(parentPosition)).updateNotiz(initText, content));
            return true;
        } else {
            Toast.makeText(this, getString(R.string.activity_notiz_error_update), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void deleteNote() {
        if (initText != null) { //Init text got stripped from NOTIZ_
            dbOpenHelper.deleteNote(this, day, time, initText);
            Toast.makeText(this, getString(R.string.activity_notiz_done_delete), Toast.LENGTH_SHORT).show();

            ExpandableRecyclerAdapter adapter = SharedValues.getAdapter();
            ExpandableTermin currTermin = ((ExpandableTermin) adapter.getParentList().get(parentPosition));
            adapter.notifyChildRemoved(parentPosition, currTermin.removeNotiz(initText));
            if (currTermin.details.isEmpty()) {
                adapter.notifyParentChanged(parentPosition);
            }
        } else {
            Toast.makeText(this, getString(R.string.activity_notiz_error_delete), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveNote() {
        String content = etContent.getText().toString();
        if (content.length() > 0) {
            try {
                dbOpenHelper.putNote(this, day, time, content);
            } catch (SQLException e) {
                Toast.makeText(this, getString(R.string.activity_notiz_error2_save), Toast.LENGTH_SHORT).show();
                return false;
            }
            Toast.makeText(this, getString(R.string.activity_notiz_done_save), Toast.LENGTH_SHORT).show();

            ExpandableRecyclerAdapter adapter = SharedValues.getAdapter();
            ExpandableTermin currTermin = ((ExpandableTermin) adapter.getParentList().get(parentPosition));
            adapter.notifyChildInserted(parentPosition, currTermin.insertNotiz(content));
            if (currTermin.details.size() == 1) {
                adapter.notifyParentChanged(parentPosition);
            }
            adapter.expandParent(parentPosition);
            return true;
        } else {
            Toast.makeText(this, getString(R.string.activity_notiz_error1_save), Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_delete:
                deleteNote();
                finish();
                break;
            case R.id.btn_save:
                if (initText != null && updateNote() || saveNote()) { //Old or new notice?
                    finish();
                }
            default:
        }
    }

}
