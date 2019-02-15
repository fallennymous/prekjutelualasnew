package com.example.fallennymous.prekjutelulas;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fallennymous.prekjutelulas.Common.Common;
import com.example.fallennymous.prekjutelulas.Database.Database;
import com.example.fallennymous.prekjutelulas.Model.MyResponse;
import com.example.fallennymous.prekjutelulas.Model.Notification;
import com.example.fallennymous.prekjutelulas.Model.Order;
import com.example.fallennymous.prekjutelulas.Model.Request;
import com.example.fallennymous.prekjutelulas.Model.Sender;
import com.example.fallennymous.prekjutelulas.Model.Token;
import com.example.fallennymous.prekjutelulas.Remote.APIService;
import com.example.fallennymous.prekjutelulas.ViewHolder.CardAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {
    @BindView(R.id.listCard) RecyclerView recyclerView;
    @BindView(R.id.total) TextView txtTotalPrice;
    @BindView(R.id.btnPlaceOrder) FButton btnPlace;


    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database = null;
    DatabaseReference requests = null;

    // init cart list and cart adapter
    List<Order> carts = new ArrayList<>();
    CardAdapter adapter = null;

    APIService mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        //init service
        mService = Common.getFCMService();

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        // setting recycler view
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // loading food list
        loadListFood();

        // placing order
        placeOrder();
    }

    /**
     * Clicking Place Order button
     */
    private void placeOrder() {
        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });
    }

    /**
     * Showing alert dialog
     */
    private void showAlertDialog() {
        // set title and message for alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Satu Langkah Lagi!");
        alertDialog.setMessage("Masukkan Alamat: ");

        // set editText and layout params
        final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        // adding editText to layout params
        edtAddress.setLayoutParams(lp);

        // adding editText to alert dialog
        alertDialog.setView(edtAddress);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        // set positive button
        alertDialog.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Create new request
                Request request = new Request(
                        Common.currenUser.getPhone(),
                        Common.currenUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        carts
                );
                // Submit to Firebase
                // Using System.CurrentMillis to key
                String order_number = String.valueOf(System.currentTimeMillis());
                requests.child(order_number)
                        .setValue(request);
                // Deleting cart
                new Database(getBaseContext()).cleanCart();

                sendNotificationOrder(order_number);

         //       Toast.makeText(Cart.this, "Terimakasih Telah Membeli", Toast.LENGTH_SHORT).show();
         //       finish();
            }
        });
        // set Cancel button
        alertDialog.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("serverToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Token serverToken = postSnapShot.getValue(Token.class);

                    Notification notification = new Notification("Prekju Telulas", "Kamu memiliki pesanan baru "+order_number);
                    Sender content = new Sender(serverToken.getToken(),notification);

                    mService.sendNotification(content)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    if (response.code() == 200) {
                                        if (response.body().success == 1) {
                                            Toast.makeText(Cart.this, "Terimakasih Telah Memesan", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(Cart.this, "Gagal", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {
                                    Log.e("ERROR",t.getMessage());
                                }
                            });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Loading food list
     */
    private void loadListFood() {
        // get cart list
        carts = new Database(this).getCarts();
        // set adapter
        adapter = new CardAdapter(carts, this);
        recyclerView.setAdapter(adapter);

        // Calculate total price
        int total = 0;
        for(Order order : carts) {
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }
        Locale locale = new Locale("in", "ID");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        txtTotalPrice.setText(fmt.format(total));
    }
}