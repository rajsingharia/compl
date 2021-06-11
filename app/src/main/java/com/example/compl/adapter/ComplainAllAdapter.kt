package com.example.compl.adapter
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.compl.R
import com.example.compl.model.Complaindata

class ComplainAllAdapter(val listener:OnItemClickListener): RecyclerView.Adapter<ComplainAllAdapter.ComplainAllViewHolder> (){


    private val differCallBack=object : DiffUtil.ItemCallback<Complaindata>(){
        override fun areItemsTheSame(oldItem: Complaindata, newItem: Complaindata): Boolean {
            return oldItem.title==newItem.title
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Complaindata, newItem: Complaindata): Boolean {
            return oldItem == newItem
        }

    }

    val differ= AsyncListDiffer(this,differCallBack)

    inner class ComplainAllViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var rowTitle: TextView =itemView.findViewById(R.id.complain_single_row_title)
        var rowDescription: TextView =itemView.findViewById(R.id.complain_single_row_description)
        val rowImageView: ImageView =itemView.findViewById(R.id.complain_single_row_image_view)
        val rowStatus: TextView =itemView.findViewById(R.id.complain_single_row_status)
        init {
            itemView.setOnClickListener (this)
        }

        override fun onClick(v: View?) {
            val position=adapterPosition

            //avoid clicking during deletion or animation
            if(position!= RecyclerView.NO_POSITION) {
                listener.OnItemClick(position)
            }
        }

    }

    interface OnItemClickListener{
        fun OnItemClick(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComplainAllViewHolder {
        return ComplainAllViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.complain_single_row,parent,false))
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ComplainAllViewHolder, position: Int) {

        val data=differ.currentList[position]

        val rowTitle: TextView =holder.rowTitle
        val rowDescription: TextView =holder.rowDescription
        val rowImageView:ImageView=holder.rowImageView
        val rowStatus:TextView=holder.rowStatus

        holder.itemView.apply {
            rowTitle.text=data.title
            rowDescription.text=data.description
            Glide.with(this).load(data.imageUrl).into(rowImageView)

            rowStatus.text=data.resolvedStatus

            when(data.resolvedStatus){
                "open" -> {
                    rowStatus.setTextColor(Color.parseColor("#00FF00"))
                }
                "close" -> {
                    rowStatus.setTextColor(Color.parseColor("#ff0000"))
                }
                else -> {
                    rowStatus.setTextColor(Color.parseColor("#FFA500"))
                }
            }
        }
    }
}