package com.example.e_challan.adapter
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_challan.PaymentActivity
import com.example.e_challan.PaymentActivity2
import com.example.e_challan.PdfViewActivity
import com.example.e_challan.R
import com.example.e_challan.model.Challan


class ChallanAdapter(private val challanList: List<Challan>) :
    RecyclerView.Adapter<ChallanAdapter.ChallanViewHolder>() {

    class ChallanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPlate: TextView = itemView.findViewById(R.id.tvPlate)
        val tvModel: TextView = itemView.findViewById(R.id.tvModel)
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnViewPdf: Button = itemView.findViewById(R.id.btnViewPdf)
        val btnPay:Button = itemView.findViewById(R.id.btnPay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_challan, parent, false)
        return ChallanViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChallanViewHolder, position: Int) {
        val challan = challanList[position]

        holder.tvName.text = "Name: ${challan.name}"
        holder.tvPlate.text = "Plate: ${challan.plate}"
        holder.tvModel.text = "Model: ${challan.model} (${challan.year})"
        holder.tvAmount.text = "Amount: Rs. ${challan.amount}"
        holder.tvStatus.text = "Status: ${challan.status}"

        holder.btnViewPdf.setOnClickListener {
            val intent = Intent(holder.itemView.context, PdfViewActivity::class.java)
            intent.putExtra("challan_no", challan.challan_no)
            holder.itemView.context.startActivity(intent)
        }
        holder.btnPay.setOnClickListener {
            val intent = Intent(holder.itemView.context, PaymentActivity2::class.java).apply {
                putExtra("AMOUNT", challan.amount?.toDouble() ?: 0.0)
                putExtra("CHALLAN_NUMBER", challan.challan_no ?: "")
                putExtra("CUSTOMER_NAME", challan.name ?: "")
                putExtra("CUSTOMER_EMAIL", challan.email ?: "")
                putExtra("CUSTOMER_MOBILE", challan.cnic ?: "") // or use phone if you have it
            }
            holder.itemView.context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int = challanList.size
}
