package com.example.fallennymous.prekjutelulas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.fallennymous.prekjutelulas.Common.Common;
import com.example.fallennymous.prekjutelulas.Interface.ItemClickListener;
import com.example.fallennymous.prekjutelulas.Model.Request;
import com.example.fallennymous.prekjutelulas.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fallennymous on 02/08/2018.
 */

public class OrderStatus extends AppCompatActivity {
    @BindView(R.id.listOrders)
    RecyclerView recyclerView;

    public RecyclerView.LayoutManager layoutManager = null;

    FirebaseDatabase database;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        ButterKnife.bind(this);

        // Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        // set recycler view
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // loading order status
       // if(getIntent() == null)
            loadOrders(Common.currenUser.getPhone());
      //  else
          //  loadOrders(getIntent().getStringExtra("userphone"));
    }

    /**
     * Loading order status
     * @param phone
     */
    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                        .equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                // get key from DatabaseReference
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                // set text for order status, address and phone
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrAddress.setText(model.getAddress());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(OrderStatus.this, "" + position, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        // set adapter for recycler view
        recyclerView.setAdapter(adapter);
    }


}
