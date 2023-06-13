package com.capstone.turuappmobile.ui.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.data.repository.Result
import com.capstone.turuappmobile.data.viewModelFactory.ViewModelFactoryUser
import com.capstone.turuappmobile.databinding.FragmentProfileBinding
import com.capstone.turuappmobile.ui.activity.catalog.CatalogActivity
import com.capstone.turuappmobile.ui.activity.detailAnalysist.DetailAnalysistActivity
import com.capstone.turuappmobile.ui.activity.login.LoginActivity
import com.capstone.turuappmobile.ui.animation.ShimmerAnimation
import com.example.awesomedialog.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {


    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var auth: FirebaseAuth? = null
    private lateinit var googleSignInClient: GoogleSignInClient


    private val profileViewModel by viewModels<ProfileViewModel> {
        ViewModelFactoryUser.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        val firebaseUser = auth?.currentUser

        binding.apply {
            usernameProfile.text = firebaseUser?.displayName
            emailProfile.text = firebaseUser?.email
            Glide.with(requireActivity())
                .load(firebaseUser?.photoUrl)
                .placeholder(ShimmerAnimation.runShimmerAnimation())
                .into(imageProfileSource)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        if (firebaseUser == null) {
            requireActivity().finish()
            return
        }

        profileViewModel.getUserSession.observe(viewLifecycleOwner) {
            profileViewModel.checkPoints(it.jwtToken, it.UID)
        }

        profileViewModel.checkPointResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    binding.pointsProfile.text = it.data.point
                }
                is Result.Error -> {
                    showLoading(false)
                    toastMaker(it.error)
                }
            }
        }

        binding.apply {
            btnCatlogProfile.setOnClickListener {
                startActivity(Intent(requireActivity(), CatalogActivity::class.java))
            }
            btnSleepProfile.setOnClickListener {
                startActivity(Intent(requireActivity(), DetailAnalysistActivity::class.java))
            }
            btnLogout.setOnClickListener {

                AwesomeDialog.build(requireActivity())
                    .title("Log Out")
                    .body(
                        "Are you sure want to logout ?",
                    )
                    .background(R.drawable.bg_rounded_blue200)
                    .position(AwesomeDialog.POSITIONS.CENTER)
                    .onPositive(
                        "Logout",
                        buttonBackgroundColor = R.drawable.bg_rounded_blue500,
                        textColor = ContextCompat.getColor(requireActivity(), R.color.white)
                    ) {
                        Firebase.auth.signOut()

                        googleSignInClient.signOut().addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                requireActivity().finish()
                                requireActivity().startActivity(
                                    Intent(
                                        requireActivity(),
                                        LoginActivity::class.java
                                    )
                                )
                            }
                        }
                    }
                    .onNegative(
                        "Cancel",
                        buttonBackgroundColor = R.drawable.bg_rounded_white,
                        textColor = ContextCompat.getColor(requireActivity(), R.color.green_200)
                    ) {
                        Log.d("TAG", "negative ")
                    }


            }
        }



    }

    private fun showLoading(isLoading: Boolean) {
        binding.pointsProfileShimmer.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun toastMaker(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

}