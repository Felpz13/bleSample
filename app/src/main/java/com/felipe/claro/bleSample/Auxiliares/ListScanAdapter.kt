package com.felipe.claro.bleSample.Auxiliares

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.felipe.claro.bleSample.R

class ListScanAdapter(private val context: Context, private val dataSource: ArrayList<BluetoothDevice>) : BaseAdapter()
{

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item


        val vh : ViewHolder
        val view : View?

        if (convertView == null)
        {
            view = inflater.inflate(R.layout.list_item_device, parent, false)
            vh = ViewHolder(view)
            view.tag = vh
        }

        else
        {
            view = convertView
            vh = view.tag as ViewHolder
        }

        if(dataSource[position].name == null)
        {
            vh.deviceName.text = context.getString(R.string.no_name)
        }

        else
        {
            vh.deviceName.text = dataSource[position].name
        }


        vh.deviceAddress.text = dataSource[position].toString()

        return view!!
    }

    private class ViewHolder(view: View?)
    {
        val deviceName : TextView = view?.findViewById(R.id.deviceName) as TextView
        val deviceAddress : TextView = view?.findViewById(R.id.deviceAddress) as TextView

    }
}
