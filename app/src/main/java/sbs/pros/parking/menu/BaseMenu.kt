package sbs.pros.parking.menu

import androidx.fragment.app.Fragment

abstract class BaseMenu(layout: Int): Fragment(layout) {

    abstract val fragmentListener: FragmentListener

    interface FragmentListener{
        fun onTitleChanged(title: String)
    }


    fun setTitle(title: String){ fragmentListener.onTitleChanged(title) }
}