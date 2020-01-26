package com.example.dd_dice_roller


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso



class CustomListAdapter(context: Context, var actionList:Array<ActionList.ItemPage>) : BaseAdapter(){

    private val inflater: LayoutInflater
        = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItem(position: Int): Any {
        return actionList.get(position)

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return actionList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = inflater.inflate(R.layout.listview_row, parent, false)

        val nameView = rowView.findViewById(R.id.nameText) as TextView

        val descriptionView = rowView.findViewById(R.id.description) as TextView

        val imageView = rowView.findViewById(R.id.actionImage) as ImageView

        val diePage = getItem(position) as ActionList.ItemPage



//        nameView.text = when(diePage.name) {
//            "Attack" -> c.getString(R.string.attack)
//            "Use Ability" -> c.getString(R.string.use_ability)
//            else -> c.getString(R.string.cast_spell)
//        }
//
//        descriptionView.text = when(diePage.description) {
//            "With Might" -> c.getString(R.string.might)
//            "With Heart" -> c.getString(R.string.heart)
//            else -> c.getString(R.string.cunning)
//        }


        nameView.text = diePage.name

        descriptionView.text = diePage.description

        Picasso.get().load(diePage.imageUrl).into(imageView)

        return rowView
    }
}
