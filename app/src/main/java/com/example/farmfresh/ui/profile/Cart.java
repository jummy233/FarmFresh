package com.example.farmfresh.ui.profile;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farmfresh.R;
import com.example.farmfresh.model.adaptor.ItemCartAdaptor;
import com.example.farmfresh.model.data.data.Item;
import com.example.farmfresh.model.data.State;

import org.json.JSONException;

import java.util.ArrayList;

public class Cart extends FragmentActivity {
    private RecyclerView recyclerView;
    private ItemCartAdaptor adaptor;
    private ArrayList<Item> items;
    private Button checkoutBtn;
    private State state;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        state = State.getInstance();

        checkoutBtn = (Button) findViewById(R.id.cartCheckout);
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View e) {
            }
        });

        try {
            initView();
            System.out.println(items.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initView() throws JSONException {
        System.out.println("initing");
        recyclerView = (RecyclerView) findViewById(R.id.cartList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        items = state.getCart();
        System.out.println(state.getCart().toString());
        adaptor = new ItemCartAdaptor(this, items);
        recyclerView.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();
    }

}
