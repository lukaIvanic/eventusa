package com.example.eventusa.screens.home.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventusa.R
import com.example.eventusa.screens.home.fragments.events.EventsFragment
import com.example.eventusa.screens.home.fragments.tasks.TasksFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        bottomNavigation = findViewById(R.id.bottom_navigation)

        setupUI()

    }

    private fun setupUI() {
        setupTouch()
    }

    private fun setupTouch() {
        setupNavigationTouch()
    }

    private fun setupNavigationTouch() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {
                    val eventsFragment = EventsFragment()
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.content, eventsFragment, "")
                    fragmentTransaction.commit()
                    true
                }
                R.id.item_2 -> {
                    val tasksFragment = TasksFragment()
                    val fragmentTransaction = supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.content, tasksFragment, "")
                    fragmentTransaction.commit()
                    true
                }
                else -> false
            }
        }




        bottomNavigation.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.item_1 -> {

                }
                R.id.item_2 -> {

                }
            }
        }
    }

}