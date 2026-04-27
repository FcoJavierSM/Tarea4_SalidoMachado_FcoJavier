package dam.pmdm.spyrothedragon.ui

import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class ScepterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val staffPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#6B3A1F")
        style = Paint.Style.FILL
    }

    private val staffHighlight = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#A0522D")
        style = Paint.Style.FILL
    }

    private val gemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        maskFilter = BlurMaskFilter(40f, BlurMaskFilter.Blur.NORMAL)
    }

    private val sparklePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    var glowColor: Int = Color.CYAN
        set(value) {
            field = value
            glowPaint.color = value
            gemPaint.color = value
            invalidate()
        }

    var sparkleAngle: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    var shimmerIntensity: Float = 0.5f
        set(value) {
            field = value
            invalidate()
        }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f

        val staffW = width * 0.09f
        val gemCy = cy - height * 0.18f
        val gemR = width * 0.17f
        val staffTop = gemCy + gemR * 0.7f
        val staffBottom = height - px(12).toFloat()

        canvas.drawRoundRect(
            cx - staffW / 2, staffTop, cx + staffW * 0.25f, staffBottom,
            staffW / 2, staffW / 2, staffPaint
        )
        canvas.drawRoundRect(
            cx - staffW * 0.25f, staffTop, cx + staffW / 2, staffBottom,
            staffW / 2, staffW / 2, staffHighlight
        )

        val glowR = gemR * (1.4f + shimmerIntensity * 0.6f)
        glowPaint.color = adjustAlpha(glowColor, (180 * (0.5f + shimmerIntensity * 0.5f)).toInt())
        canvas.drawCircle(cx, gemCy, glowR, glowPaint)

        gemPaint.shader = RadialGradient(
            cx - gemR * 0.3f, gemCy - gemR * 0.3f, gemR,
            intArrayOf(Color.WHITE, glowColor, darken(glowColor)),
            floatArrayOf(0f, 0.4f, 1f),
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(cx, gemCy, gemR, gemPaint)

        canvas.save()
        canvas.rotate(sparkleAngle, cx, gemCy)
        val sparkleOffsets = arrayOf(
            Pair(0f, -1.7f), Pair(1.7f, 0f), Pair(0f, 1.7f), Pair(-1.7f, 0f),
            Pair(1.2f, -1.2f), Pair(1.2f, 1.2f), Pair(-1.2f, 1.2f), Pair(-1.2f, -1.2f)
        )
        sparkleOffsets.forEachIndexed { i, (dx, dy) ->
            val scale = if (i < 4) 0.22f else 0.14f
            drawStar(canvas, cx + dx * gemR, gemCy + dy * gemR, gemR * scale)
        }
        canvas.restore()

        drawStar(canvas, cx, gemCy, gemR * 0.12f)
    }

    private fun drawStar(canvas: Canvas, x: Float, y: Float, size: Float) {
        val path = Path()
        path.moveTo(x, y - size)
        path.lineTo(x + size * 0.25f, y - size * 0.25f)
        path.lineTo(x + size, y)
        path.lineTo(x + size * 0.25f, y + size * 0.25f)
        path.lineTo(x, y + size)
        path.lineTo(x - size * 0.25f, y + size * 0.25f)
        path.lineTo(x - size, y)
        path.lineTo(x - size * 0.25f, y - size * 0.25f)
        path.close()
        canvas.drawPath(path, sparklePaint)
    }

    private fun adjustAlpha(color: Int, alpha: Int): Int =
        Color.argb(alpha.coerceIn(0, 255), Color.red(color), Color.green(color), Color.blue(color))

    private fun darken(color: Int): Int = Color.rgb(
        (Color.red(color) * 0.5f).toInt(),
        (Color.green(color) * 0.5f).toInt(),
        (Color.blue(color) * 0.5f).toInt()
    )

    private fun px(dp: Int) = (dp * resources.displayMetrics.density).toInt()
}
