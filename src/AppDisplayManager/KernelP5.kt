package AppDisplayManager

import com.amd.aparapi.Kernel
import com.amd.aparapi.Range


class KernelP5(private val width: Int, private val height: Int, private val pixels: IntArray) : Kernel() {
    var mx : Int
    var my : Int
    private var range: Range = Range.create(width * height)

    init {
        mx = width / 2
        my = height / 2
    }

    override fun run() {
        val gid = globalId

        val tx = gid % width
        val ty = gid / width

        val t : Int = (100000 / (pow((tx - mx).toFloat(), 2f) + pow((ty - my).toFloat(), 2f))).toInt()

        val red = min(t/2, 255)
        val green = min(t/2, 255)
        val blue = min(t, 255)

        pixels[gid] = (0xEE000000 + (red shl 16) + (green shl 8) + blue).toInt()
    }

    fun nextGeneration() {
        execute(range)
    }

}