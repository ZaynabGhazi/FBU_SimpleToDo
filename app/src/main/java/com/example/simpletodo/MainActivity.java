package com.example.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> items;
    //for Recycler view: (adapter--LayoutManager)
    ItemsAdapter itemsAdapter;
    //view UIs:
    Button btnAdd;
    EditText etItem;
    RecyclerView rvItems;
    //keys:
    public static final String KEY_ITEM_TEXT="item text";
    public static final String KEY_ITEM_POSITION="item position";
    public static final int EDIT_TEXT_CODE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //added:
        //fetch UI elements from layout:
        btnAdd = findViewById(R.id.btnAdd);
        etItem = findViewById(R.id.etItem);
        rvItems = findViewById(R.id.rvItems);
        //fetch saved items (Persistence):
        loadItems();
        //edit items upon click:
        //implement interface created in Adapter class:
        ItemsAdapter.OnClickListener clickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Log.d("Main Activity","Single Click at position"+position);
                //launch new activity
                //creation:
                Intent i = new Intent(MainActivity.this,EditActivity.class);
                //pass data: item content + p
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                //display activity:
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };

        //delete item upon long click (Adapter of recyclerView):
        //implement interface created in Adapter class:
        ItemsAdapter.OnLongClickListener onLongClickListener= new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                //delete + notify adapter with p
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"Item was removed",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };
        itemsAdapter = new  ItemsAdapter(items, onLongClickListener, clickListener);
        rvItems.setAdapter(itemsAdapter);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        //handle button action
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = etItem.getText().toString();
                //add item to model
                items.add(todoItem);
                //notify adapter of item insertion
                itemsAdapter.notifyItemInserted(items.size()-1);
                //clear textView
                etItem.setText("");
                Toast.makeText(getApplicationContext(), "Item was added",Toast.LENGTH_SHORT).show();
                //save current state of list (Persistence)
                saveItems();
            }
        });
    }

    //handle editing: activity 2

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE){
            //update text : fetch return text + get position of modification
            String newText = data.getStringExtra(KEY_ITEM_TEXT);
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            //update model + notify adapter
            items.set(position,newText);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(), "Item was updated", Toast.LENGTH_SHORT).show();

        }
        else{
            Log.w("MainActivity","Unknown call to onActivityResult");
        }
    }

    //Using Apache persistence methods:
    private File getDataFile(){
        return new File(getFilesDir(),"data.txt");
    }
    //read data file to load items
    private void loadItems(){
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity","Error loading items",e);
            items = new ArrayList<>();
        }
    }
    //write items to file
    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(),items);
        } catch (IOException e) {
            Log.e("MainActivity","Error writing items",e);
        }

    }
}