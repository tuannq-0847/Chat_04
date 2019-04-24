package com.sun.chat_04.ui.profile

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.sun.chat_04.R
import com.sun.chat_04.util.Constants
import kotlinx.android.synthetic.main.fragment_image_detail.imageViewAvatar

class ImageDetailFragment : Fragment(), OnClickListener {

    private val uri by lazy { Uri.parse(arguments?.getString(Constants.ARGUMENT_IMAGE_URI, "")) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_detail, container, false)
        view.setOnClickListener(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayImage()
    }

    override fun onClick(v: View?) {
    }

    private fun displayImage() {
        Glide.with(imageViewAvatar)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.image_avatar)
            .into(imageViewAvatar)
    }

    companion object {
        fun newInstance(uri: String?) = ImageDetailFragment().apply {
            arguments = Bundle().apply {
                uri?.let {
                    putString(Constants.ARGUMENT_IMAGE_URI, uri)
                }
            }
        }
    }
}
