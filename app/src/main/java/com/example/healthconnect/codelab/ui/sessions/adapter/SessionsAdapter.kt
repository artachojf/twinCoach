package com.example.healthconnect.codelab.ui.sessions.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.healthconnect.codelab.ui.sessions.list.SessionsListFragment
import com.example.healthconnect.codelab.ui.sessions.calendar.SessionsCalendarFragment

class SessionsAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SessionsListFragment()
            else -> SessionsCalendarFragment()
        }
    }
}