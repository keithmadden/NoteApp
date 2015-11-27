package com.example.keith.noteapp;

import android.widget.Spinner;import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private EditText editorNum;
    private EditText editorDate;
    //private Spinner editorSpinner;
    private String noteFilter;
    private String oldText;
    private String oldNum;
    private String oldDate;
    //private Spinner oldSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editor = (EditText) findViewById(R.id.editText);
        editorNum = (EditText) findViewById(R.id.editPhone);
        editorDate = (EditText) findViewById(R.id.editDate);
        //editorSpinner = (Spinner) findViewById(R.id.editSpinner);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle("New note");
        } else {
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            //cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            editor.setText(oldText);
            oldNum = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_NUMBER));
            editorNum.setText(oldNum);
            oldDate = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_DATE));
            editorDate.setText(oldDate);
            //oldSpinner = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_SPINNER));
            //editorSpinner.setOnItemClickListener(oldSpinner);
            editor.requestFocus();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }

        return  true;
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();
        String newNum = editorNum.getText().toString().trim();
        String newDate = editorDate.getText().toString().trim();
        //String newSpinner = editorSpinner.().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                if (newText.length() == 0 && newNum.length() == 0 && newDate.length() == 0) {
                    setResult(RESULT_CANCELED);
                } else {
                    insertNote(newText, newNum, newDate);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0 && newNum.length() == 0 && newDate.length() == 0) {
                    deleteNote();
                }
                else if (oldText.equals(newText) && oldNum.equals(newNum) && oldDate.equals(newDate)) {
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText, newNum, newDate);
                }
        }
        finish();
    }

    private void updateNote(String noteText, String noteNum, String noteDate) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.NOTE_NUMBER, noteNum);
        values.put(DBOpenHelper.NOTE_DATE, noteDate);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }


    private void insertNote(String noteText, String noteNum, String noteDate) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.NOTE_NUMBER, noteNum);
        values.put(DBOpenHelper.NOTE_DATE, noteDate);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }
}
