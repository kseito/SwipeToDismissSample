package com.example.swipetodismisssample

import android.animation.Animator
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.swipetodismisssample.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private lateinit var gestureDetector: GestureDetectorCompat

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                binding.container.y -= distanceY
                return super.onScroll(e1, e2, distanceX, distanceY)
            }

            override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
                // 速度が一定以上だったら画面を閉じる
                if (velocityY > 8_000) {
                    findNavController().popBackStack()
                }
                return super.onFling(e1, e2, velocityX, velocityY)
            }
        }
        gestureDetector = GestureDetectorCompat(context, gestureListener)
        binding.rootContainer.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    dismissOrRestore()
                }
            }
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    private fun dismissOrRestore() {
        // y方向のスワイプ量がスワイプ対象のビューの高さ1/3を超えたら画面を閉じる
        if (binding.container.y < binding.container.height / 3) {
            restoreViewTransform()
        } else {
            findNavController().popBackStack()
        }
    }

    private fun restoreViewTransform() {
        binding.container.run {
            animate()
                .setDuration(300)
                .setInterpolator(DecelerateInterpolator())
                .translationY(binding.container.top.toFloat())
                .setUpdateListener {
                }
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator?) {
                        // no op
                    }

                    override fun onAnimationEnd(p0: Animator?) {
                        // no op
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        // no op
                    }

                    override fun onAnimationRepeat(p0: Animator?) {
                        // no op
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}