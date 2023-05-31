package com.capstone.turuappmobile.ui.fragment.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.capstone.turuappmobile.R
import com.capstone.turuappmobile.databinding.FragmentProfileBinding
import com.capstone.turuappmobile.ui.activity.login.LoginActivity
import com.capstone.turuappmobile.ui.animation.ShimmerAnimation
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

        binding.btnLogout.setOnClickListener {
            Firebase.auth.signOut()

            googleSignInClient.signOut().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Berhasil logout dari akun Google
                    // Redirect ke halaman login
                    requireActivity().finish()
                    requireActivity().startActivity(
                        Intent(
                            requireActivity(),
                            LoginActivity::class.java
                        )
                    )
                } else {
                    // Gagal logout dari akun Google, tangani sesuai kebutuhan
                }
            }

        }
    }

}