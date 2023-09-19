package com.mercadolibre.android.point_mainapp_demo.app.view.payment.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mercadolibre.android.point_integration_sdk.nativesdk.payment.data.InstallmentAmount

internal class PaymentInstallmentAdapter(private val callback: (InstallmentAmount) -> Unit) :
    ListAdapter<InstallmentAmount, PaymentInstallmentViewHolder>(DiffCallbackInstallment()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentInstallmentViewHolder = PaymentInstallmentViewHolder.from(parent)

    override fun onBindViewHolder(holder: PaymentInstallmentViewHolder, position: Int) {
        holder.onRender(currentList[position]) {
            callback(it)
        }
    }
}

private class DiffCallbackInstallment : DiffUtil.ItemCallback<InstallmentAmount>() {
    override fun areItemsTheSame(oldItem: InstallmentAmount, newItem: InstallmentAmount): Boolean =
        oldItem.installment == newItem.installment

    override fun areContentsTheSame(
        oldItem: InstallmentAmount,
        newItem: InstallmentAmount
    ): Boolean = oldItem == newItem
}
