package com.capstone.turuappmobile.ui.activity.detailCatalog

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.api.model.DataDetailCatalog
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.ActivityDetailCatalogBinding
import com.capstone.turuappmobile.utils.loadImage
import com.example.awesomedialog.*

class DetailCatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailCatalogBinding

    private val detailCatalogViewModel by viewModels<DetailCatalogViewModel> {
        ViewModelFactoryUser.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idCatalog =  intent.getIntExtra(CATALOG_ID, 0).toString()

        detailCatalogViewModel.getUserSession.observe(this){ user ->


            detailCatalogViewModel.detailCatalog(user.jwtToken, idCatalog)


            binding.btnCatalog.setOnClickListener {

                AwesomeDialog.build(this)
                    .title("Exchange Catalog")
                    .body(
                        "Are you sure want to exchange this product?",
                    )
                    .background(R.drawable.bg_rounded_blue200)
                    .position(AwesomeDialog.POSITIONS.CENTER)
                    .onPositive(
                        "Yes",
                        buttonBackgroundColor = R.drawable.bg_rounded_blue500,
                        textColor = ContextCompat.getColor(this, R.color.white)
                    ) {
                        detailCatalogViewModel.exchangePoint(user.jwtToken, user.UID, idCatalog)
                    }
                    .onNegative(
                        "Cancel",
                        buttonBackgroundColor = R.drawable.bg_rounded_white,
                        textColor = ContextCompat.getColor(this, R.color.green_200)
                    ) {

                    }


            }
        }

        detailCatalogViewModel.detailCatalogResult.observe(this){
            when(it){
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    setAllData(it.data.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }
            }
        }

        detailCatalogViewModel.exchangePointResult.observe(this){
            when(it){
                is Result.Loading -> {
                    showLoadingNotShimmer(true)
                }
                is Result.Success -> {
                    showLoadingNotShimmer(false)
                    if(it.data.message == "Point is not enough"){
                        alertDialog(it.data.message, R.drawable.bg_rounded_red, R.drawable.warning_icon)
                    }else{
                        alertDialog(it.data.message, R.drawable.bg_rounded_blue500, R.drawable.success_icon)
                    }
                    toastMaker(it.data.message)
                }
                is Result.Error -> {
                    showLoadingNotShimmer(false)
                    toastMaker(it.error)
                }
            }
        }

        binding.btnBackDetailCatalog.setOnClickListener {
            finish()
        }
    }

    private fun setAllData(data: DataDetailCatalog){
        binding.apply {
            imageCatalogDetail.loadImage(data.img)
            catalogNameTxtDetail.text = data.name
            descriptionDetailCatalogTxt.text = data.name
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.shimmerLayoutDetailCatalog.visibility =
            if (isLoading){
                binding.shimmerLayoutDetailCatalog.startShimmer()
                View.VISIBLE
            } else {
                binding.shimmerLayoutDetailCatalog.stopShimmer()
                View.GONE
            }
    }

    private fun showLoadingNotShimmer(isLoading: Boolean) {
        binding.layoutLoading.layoutAllLoading.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }
    private fun toastMaker(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun alertDialog(message: String,background: Int, icon : Int){
        AwesomeDialog.build(this)
            .title(message,
                titleColor = ContextCompat.getColor(this, R.color.white),
            )
            .icon(icon)
            .background(R.drawable.bg_rounded_blue200)
            .position(AwesomeDialog.POSITIONS.CENTER)
            .onPositive(
                "Ok",
                buttonBackgroundColor = background,
                textColor = ContextCompat.getColor(this, R.color.white)
            ) {

            }
    }


    companion object{
        const val CATALOG_ID = "catalog_id"
    }
}