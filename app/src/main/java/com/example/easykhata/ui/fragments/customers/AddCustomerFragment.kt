package com.example.easykhata.ui.fragments.customers
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentAddCustomerBinding
class AddCustomerFragment : Fragment(R.layout.fragment_add_customer) {
    lateinit var binding: FragmentAddCustomerBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentAddCustomerBinding.bind(view)
        setUpClicks()
    }
    private fun setUpClicks(){
     binding.apply {
         addCustomerToolbar.setNavigationOnClickListener {
             findNavController().navigateUp()
         }
         addNewCustomer.setOnClickListener {
             findNavController().navigate(R.id.action_addCustomerFragment_to_addCustomerDetailFragment)
         }
       }
    }
}