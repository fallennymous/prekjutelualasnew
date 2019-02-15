package com.example.fallennymous.prekjutelulas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.fallennymous.prekjutelulas.R;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.fallennymous.prekjutelulas.Database.Database;
import com.example.fallennymous.prekjutelulas.Interface.ItemClickListener;
import com.example.fallennymous.prekjutelulas.Model.Food;
import com.example.fallennymous.prekjutelulas.Model.Order;
import com.example.fallennymous.prekjutelulas.ViewHolder.FoodViewHolder;


public class FoodList extends AppCompatActivity {

    private final static String TAG = "FoodList";

    @BindView(R.id.recycler_food) RecyclerView recycler_food;
    RecyclerView.LayoutManager layoutManager = null;
    @BindView(R.id.searchBar) MaterialSearchBar materialSearchBar;

    FirebaseDatabase database = null;
    DatabaseReference foodList = null;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter = null;

    // search functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter = null;
    List<String> suggestList = new ArrayList<>();

    // get category id to load food list
    String categoryId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        ButterKnife.bind(this);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

        // Init recycler view
        recycler_food.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutManager);

        // Get CategoryId from Home
        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }

        // load Food list
        if (!categoryId.isEmpty() && categoryId != null) {
            loadFoodsList(categoryId);
        }

        // load suggest list from Firebase
        materialSearchBar.setHint("masukkan nama makanan");
        loadSuggest();

        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);

        // set text change listener for search bar
        addTextChangeListener();

        // set Search action listener
        onSearchActionListener();
    }

    /**
     * set text change listener for Search bar
     */
    private void addTextChangeListener() {
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            /**
             * This method is called to notify you that, within charSequence, the count characters beginning at
             * start have just replaced old text that had length before.
             * It is an error to attempt to make changes to s from this callback.
             * @param charSequence
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                Log.d(TAG, "charSequence: " + charSequence + " start: " + start + " before: " + before + " count: " + count);
                // When user type their text, we will change suggest list
                List<String> suggest = new ArrayList<>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())) {
                        suggest.add(search);
                    }
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * set on search action listener
     */
    private void onSearchActionListener() {
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                Log.d(TAG, "enabled: " + enabled);
                // When Search bar is close
                // Restore original adapter
                if(!enabled) {
                    recycler_food.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Log.d(TAG, "text: " + text);
                // When Search finished
                // Show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    /**
     * Searching food by text
     * @param text
     */
    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("name").equalTo(text.toString())) { // Compare name
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                // set food name
                viewHolder.food_name.setText(model.getName());
                // set food image
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);
                // set current food
                final Food currentFoodItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // send FoodId to FoodDetail
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };
        // set adapter for recycler view is search result
        recycler_food.setAdapter(searchAdapter);
    }

    /**
     * Loading suggest list from Firebase
     */
    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get food item
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Loading Foods list by categoryId
     * @param categoryId
     */
    private void loadFoodsList(String categoryId) {
        // setting adapter by filtering "MenuId"
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                // set food name
                viewHolder.food_name.setText(model.getName());
                // set food image
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);
                // set current food
                final Food currentFoodItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // send FoodId to FoodDetail
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };
        // set adapter for recycler view
        recycler_food.setAdapter(adapter);
    }
}