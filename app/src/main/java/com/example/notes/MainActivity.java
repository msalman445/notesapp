package com.example.notes;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar topAppbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);


        topAppbar = findViewById(R.id.topAppBar);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        int fragmentHolder = R.id.fragmentNotes;

        setSupportActionBar(topAppbar);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(fragmentHolder, new HomeFragment())
                    .commit();
        }

//        Navigation Drawer
        topAppbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.itmAllNotes){
                    topAppbar.setTitle("Notes");
                    callFragment(fragmentHolder, new HomeFragment());
                } else if (id == R.id.itmFavorites) {
                    topAppbar.setTitle("Favorite Notes");
                    callFragment(fragmentHolder, new FavoriteFragment());
                } else if (id == R.id.itmRecycleBin) {
                    topAppbar.setTitle("Recycle Bin");
                    callFragment(fragmentHolder, new RecycleBinFragment());

                } else if (id == R.id.itmShare) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, "Discover a world of effortless organization with our new Notes app. Capture ideas, create lists, and stay on top of your tasks. Download now and unlock your full potential!\n" +
                            "\n" +
                            "https://play.google.com/store/apps/details?id=com.salman.notepad");
                    intent.setType("text/plain");

                    Intent shareIntent = Intent.createChooser(intent, null);
                    startActivity(shareIntent);

                } else if (id == R.id.itmSettings) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
                drawerLayout.close();
                return true;
            }
        });

    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)){
            drawerLayout.close();
        }else {
            super.onBackPressed();
        }
    }

    public void callFragment(int fragmentHolder, Fragment fragmentToPlace){
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(fragmentHolder, fragmentToPlace)
                .commitNow();
    }



        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.itmSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentNotes);
                if (currentFragment instanceof Searchable){
                    ((Searchable) currentFragment).performSearch(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentNotes);
                if (currentFragment instanceof Searchable){
                    ((Searchable) currentFragment).performSearch(newText);
                }
                return false;
            }
        });



        return super.onCreateOptionsMenu(menu);
    }
}