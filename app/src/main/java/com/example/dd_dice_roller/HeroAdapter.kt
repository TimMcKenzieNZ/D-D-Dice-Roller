package com.example.dd_dice_roller

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HeroAdapter(val context: Context, val application: CustomApplication) : RecyclerView.Adapter<HeroViewHolder>() {

    private var selectedIndex: Int = RecyclerView.NO_POSITION
    private var recyclerView: RecyclerView? = null

    var onSelect: (Hero) -> Unit = {}
    var onNothingSelected: () -> Unit = {}

    var heroes: MutableList<Hero> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val isHeroSelected: Boolean
        get() = application.party != null && selectedIndex != RecyclerView.NO_POSITION

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return heroes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): HeroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listview_row, parent, false)
        val holder = HeroViewHolder(view)

        view.setOnClickListener() {
            selectIndex(holder.adapterPosition)
        }

        return holder
    }

    override fun onBindViewHolder(holder: HeroViewHolder, position: Int) {
        holder.hero = heroes[position]
        holder.isActive = position == selectedIndex
    }


    override fun onBindViewHolder(holder: HeroViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.hero = heroes[position]
        }
    }

    private fun selectIndex(i: Int) {
        if (i == selectedIndex) return

        val oldSelectedIndex = selectedIndex
        selectedIndex = i

        notifyItemChanged(oldSelectedIndex)
        notifyItemChanged(selectedIndex)

        if (selectedIndex == RecyclerView.NO_POSITION) {
            onNothingSelected()
        } else {
            recyclerView?.scrollToPosition(i)
            onSelect(heroes[i])
        }
    }

    fun insert(hero: Hero) {
//        if (application.party != null) {
//            val hero = Hero("Unknown", "race", "class", "sex", 10)
//            heroes.add(hero)
//            selectIndex(heroes.size - 1)
//            notifyItemInserted(heroes.size - 1)
//
//            application.party.add(hero)
//        }
        if (application.party != null) {
            heroes.add(hero)
            selectIndex(heroes.size - 1)
            notifyItemInserted(heroes.size - 1)
        }
    }

    fun clear() {
        if (application.party != null) {
            heroes.clear()
            notifyDataSetChanged()
            //application.party.clear()
            onNothingSelected()
        }
    }


    fun delete() {
        if (isHeroSelected) {
            val hero = heroes.removeAt(selectedIndex)
            selectIndex(RecyclerView.NO_POSITION)
            notifyItemRemoved(selectedIndex)
            application.party.remove(hero)
        }
    }

}