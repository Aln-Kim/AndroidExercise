package com.example.listapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;
    private List<DataModel> itemList = new ArrayList<>();
    private Button sortButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new Adapter(itemList);
        recyclerView.setAdapter(adapter);

        sortButton = findViewById(R.id.sort_button);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        new FetchItemsTask().execute();
    }

    private void sortItems(boolean filterBlankOrNull) {
        Collections.sort(itemList, new Comparator<DataModel>() {
            @Override
            public int compare(DataModel o1, DataModel o2) {
                int result = Integer.compare(o1.listId, o2.listId);
                if (result == 0) {
                    return o1.name.compareTo(o2.name);
                }
                return result;
            }
        });

        if (filterBlankOrNull) {
            // Filter out items with blank or null names
            List<DataModel> filteredItems = new ArrayList<>();
            for (DataModel item : itemList) {
                if (item.name != null && !item.name.isEmpty()) {
                    filteredItems.add(item);
                }
            }
            itemList.clear();
            itemList.addAll(filteredItems);
        }

        adapter.notifyDataSetChanged();
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter Options")
                .setItems(R.array.filter_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                sortItems(false); // Show all items
                                break;
                            case 1:
                                new FetchItemsTask().execute(); // Fetch items again
                                break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<DataModel>> {

        @Override
        protected List<DataModel> doInBackground(Void... voids) {
            try {
                List<DataModel> items = NetworkConfig.fetchItems();
                return items;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<DataModel> items) {
            itemList.clear();
            itemList.addAll(items);
            adapter.notifyDataSetChanged();
        }
    }
}
