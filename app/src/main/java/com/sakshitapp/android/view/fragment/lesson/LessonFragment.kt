package com.sakshitapp.android.view.fragment.lesson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.material.snackbar.Snackbar
import com.sakshitapp.android.R
import com.sakshitapp.android.databinding.FragmentLessonBinding
import com.sakshitapp.android.viewmodel.ViewModelFactory


class LessonFragment : Fragment() {

    private val viewModel: LessonViewModel by viewModels {
        ViewModelFactory()
    }

    private var _binding: FragmentLessonBinding? = null
    private var player: SimpleExoPlayer? = null
    private var playWhenReady: Boolean = true
    private var playbackPosition: Long = 0
    private var currentWindow: Int = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val hasSeen get() = arguments?.getBoolean("hasSeen") ?: false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonBinding.inflate(inflater, container, false)
        observe()
        binding.finish.visibility = if (hasSeen) View.GONE else View.VISIBLE
        return binding.root
    }

    private fun observe() {
        viewModel.getData().observe(viewLifecycleOwner, { lesson ->
            binding.apply {
                description.text = lesson.description
                if (lesson.question.isEmpty()) {
                    finish.text = getString(R.string.done)
                    finish.setOnClickListener {
                        findNavController()
                            .navigate(R.id.action_lessonFragment_to_congratulationFragment, Bundle().apply {
                                putAll(arguments)
                                putBoolean("hasPassed", true)
                            })
                    }
                } else {
                    finish.text = getString(R.string.quiz)
                    finish.setOnClickListener {
                        findNavController()
                            .navigate(R.id.action_lessonFragment_to_quizFragment, arguments)
                    }
                }
            }
        })
        viewModel.error().observe(viewLifecycleOwner, {
            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
        })
        viewModel.progress().observe(viewLifecycleOwner, {
            if (it) {
                activity?.window?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            binding.loading.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
        val courseId = arguments?.getString("courseId")
        val lessonId = arguments?.getString("lessonId")
        viewModel.loadDrafts(requireContext(), courseId, lessonId)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        val trackSelector = DefaultTrackSelector(requireContext())
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSizeSd()
        )
        player = SimpleExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()
        binding.player.player = player
        player?.addListener(object: Player.EventListener{
            override fun onPlayerError(error: ExoPlaybackException) {
                super.onPlayerError(error)
                Snackbar.make(binding.root, error.localizedMessage, Snackbar.LENGTH_LONG).show()
            }
        })
        viewModel.getLink().observe(viewLifecycleOwner, { url ->
            val mediaItem = MediaItem.fromUri(url)
            player?.setMediaItem(mediaItem)
            player?.playWhenReady = playWhenReady
            player?.seekTo(currentWindow, playbackPosition)
            player?.prepare()
        })
    }

    private fun releasePlayer() {
        player?.let {
            playWhenReady = it.playWhenReady
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            it.release()
        }
        player = null
    }
}