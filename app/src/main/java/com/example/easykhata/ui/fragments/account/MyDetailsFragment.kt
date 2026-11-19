package com.example.easykhata.ui.fragments.account
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.easykhata.R
import com.example.easykhata.databinding.FragmentMyDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MyDetailsFragment : Fragment(R.layout.fragment_my_details) {
    lateinit var binding: FragmentMyDetailsBinding
    private val args: MyDetailsFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding= FragmentMyDetailsBinding.bind(view)
        setUpClicks()
        setUpUi()
    }
    private fun setUpClicks(){
        binding.apply {
            aapKiDetailsToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
    private fun setUpUi(){
        binding.apply {
            aapKaNaamEt.setText(args.user.name)
            aapkaPhone.text=args.user.phoneNumber.replaceFirst(
                Regex("^\\+92"),
                "0"
            )
        }
    }
}