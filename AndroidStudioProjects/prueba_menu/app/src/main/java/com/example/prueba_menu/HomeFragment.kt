package com.example.prueba_menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.prueba_menu.databinding.FragmentHomeBinding
import com.example.prueba_menu.databinding.ItemSnapshotBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import android.content.Context
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment() {

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mFirebaseAdapter: FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>
    private lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout y configura el binding
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)

        // Configura el LayoutManager
        mLayoutManager = LinearLayoutManager(context)

        // Configura el RecyclerView (el adaptador se asignará más tarde en onViewCreated)
        mBinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mLayoutManager
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Crea la consulta a Firebase
        val query = FirebaseDatabase.getInstance().reference.child("snapshots")

        // Configura las opciones para FirebaseRecyclerAdapter
        val options = FirebaseRecyclerOptions.Builder<Snapshot>()
            .setQuery(query, Snapshot::class.java)
            .build()

        // Inicializa el adaptador
        mFirebaseAdapter = object : FirebaseRecyclerAdapter<Snapshot, SnapshotHolder>(options) {

            private lateinit var mContext: Context

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnapshotHolder {
                mContext = parent.context
                val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_snapshot, parent, false)
                return SnapshotHolder(view)
            }

            override fun onBindViewHolder(holder: SnapshotHolder, position: Int, model: Snapshot) {
                val snapshot = getItem(position)

                with(holder) {
                    setListener(snapshot)

                    binding.tvTitle.text = snapshot.title
                    Glide.with(mContext)
                        .load(snapshot.photoUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(binding.imgPhoto)
                }
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChanged() {
                super.onDataChanged()
                mBinding.progressBar.visibility = View.GONE
            }
        }

        // Asigna el adaptador al RecyclerView
        mBinding.recyclerView.adapter = mFirebaseAdapter
        mFirebaseAdapter.startListening() // Inicia la escucha de Firebase
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mFirebaseAdapter.stopListening() // Detén la escucha de Firebase
    }

    inner class SnapshotHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemSnapshotBinding.bind(view)

        fun setListener(snapshot: Snapshot) {
            // Configura eventos del snapshot aquí
        }
    }
}