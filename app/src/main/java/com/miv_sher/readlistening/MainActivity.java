package com.miv_sher.readlistening;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miv_sher.readlistening.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    //just example
    //String[] dataArray = new String[]{"India","Australia","USA","U.K","Japan"};

    /**
     * List, which keeps objects represented by loaded books
     */
    List<Book> library;
    Book one = new Book("testChapter.htm", "section0.json", R.raw.section0, "Wild", "Cheryl Strayed", R.drawable.icon_wild);
    Book two = new Book("alice.htm","alice.json", R.raw.alice, "Alice in Wonderland", "Lewis Carrol", R.drawable.icon_alice);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
       // adapter = new RecyclerAdapter(dataArray);
        library = new ArrayList<Book>();
        library.add(one);
        library.add(two);
       // Book three = library.get(0);
        adapter = new RecyclerAdapter(library, this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_open) {

            Intent intent = new Intent(this, ListFileActivity.class);
            this.startActivityForResult(intent, 1);

    }

    return super.onOptionsItemSelected(item);
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        String path = data.getStringExtra("path");
        Toast.makeText(this, "selected file: " +  path, Toast.LENGTH_LONG).show();
    }
}
