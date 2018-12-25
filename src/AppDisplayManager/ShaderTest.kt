package AppDisplayManager

import com.jogamp.opengl.GL
import com.jogamp.opengl.GL2ES1.GL_POINT_SPRITE
import com.jogamp.opengl.GL2ES2
import com.jogamp.opengl.GL2GL3.GL_VERTEX_PROGRAM_POINT_SIZE
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PImage
import processing.opengl.PJOGL
import processing.opengl.PShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer

class ShaderTest : PApplet(){
    fun run(args: Array<String>) : Unit = PApplet.main(ShaderTest::class.qualifiedName) //processing起動

    companion object {
        var inputCrossKey = 0b00000 //dir: shift, d, r, u, l
    }

    lateinit var shader : PShader

    lateinit var vertTex : PImage

    lateinit var pgl: PJOGL
    lateinit var gl: GL2ES2

    var indexes: IntArray? = null
    var indexBuffer: IntBuffer? = null
    var indexVboId: Int = 0

    var weight = 100f


    override fun settings() : Unit{
        //TIS = Text Input Sources, TSM = Text Services Manager
        size(WINDOW_WIDTH, WINDOW_HEIGHT, P2D)
    }

    override fun setup() : Unit{
        fill(0xff)
        background(0)
        frameRate(60f)
    }

    fun PGLsetup(){
        shader = loadShader("src/AppDisplayManager/Shader/Test.frag", "src/AppDisplayManager/Shader/Test.vert")
        shader.set("resolution", width.toFloat(), height.toFloat())
        shader.set("minRes", min(width.toFloat(), height.toFloat()))
        shader.set("pointScale", 2f)

        pgl = beginPGL() as PJOGL
        gl = pgl.gl.gL2ES2

        val intBuffer = IntBuffer.allocate(1)
        gl.glGenBuffers(1, intBuffer)
        indexVboId = intBuffer.get(0)

        gl.glEnable(GL_VERTEX_PROGRAM_POINT_SIZE)
        gl.glEnable(GL_POINT_SPRITE)

        val ext = gl.getExtension("OES_texture_float")
        val ext2 = gl.getExtension("OES_texture_half_float")
        if(ext == null && ext2 == null){
            println("float texture not supported")
        }

        endPGL()
    }


    override fun draw() : Unit{
        //background(0)
        blendMode(ADD)
        fill(0)

        //drawPGLFrame()
    }

    fun drawPGLFrame(){
        vertTex = createImage(2, 1, ARGB)

        vertTex.loadPixels()

        vertTex.pixels[0] = mouseX
        vertTex.pixels[1] = mouseY

        vertTex.updatePixels()

        val indexSize = vertTex.pixels.size/2
        indexes = IntArray(indexSize){ i -> i }
        indexBuffer = allocateDirectIntBuffer(indexSize)

        indexBuffer!!.rewind()
        indexBuffer!!.put(indexes)
        indexBuffer!!.rewind()

        //--------

        // pg.background(100f,0f,0f)

        shader.set("mouse", mouseX.toFloat(), mouseY.toFloat())
        //shader.set("vert_texture", vertTex)

       shader(shader)

        pgl = beginPGL() as PJOGL
        gl = pgl.gl.gL2ES2

        shader.bind()

        gl.glEnableVertexAttribArray(LAYOUT_INDEX_LOCATION)

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, indexVboId)
        gl.glBufferData(GL.GL_ARRAY_BUFFER, (Integer.BYTES * indexSize).toLong(), indexBuffer, GL.GL_DYNAMIC_DRAW)
        gl.glVertexAttribPointer(LAYOUT_INDEX_LOCATION, 1, GL.GL_FLOAT, false, Integer.BYTES, 0)

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0)

        gl.glDisableVertexAttribArray(LAYOUT_INDEX_LOCATION)

        shader.unbind()

        rect(0f, 0f, width.toFloat(), height.toFloat())

        errCheck("DwParticleRenderGL.shader.unbind()")

        endPGL()

    }

    protected fun errCheck(user_msg: String) {
        val err = pgl.error
        if (err != 0) {
            val errString = pgl.errorString(err)
            val msg = "OpenGL error $err at $user_msg: $errString"
            PGraphics.showWarning(msg)
        }
    }

    override fun keyPressed() {
        //println("Pressed:$keyCode")
        when(keyCode){
            37, 38, 39, 40 ->{
                val keySwitch = 0b1 shl (keyCode - 37)
                inputCrossKey = inputCrossKey or keySwitch
            }

            16 ->{
                inputCrossKey = inputCrossKey or 0b10000
            }
        }
    }

    override fun keyReleased() {
        //println("Released:$keyCode")
        when(keyCode){
            37, 38, 39, 40 ->{
                val keySwitch = (0b1 shl (keyCode - 37)).inv()
                inputCrossKey = inputCrossKey and keySwitch
            }

            16 ->{
                inputCrossKey = inputCrossKey and 0b01111
            }
        }
    }

    fun allocateDirectIntBuffer(n: Int): IntBuffer {
        return ByteBuffer.allocateDirect(n * Integer.BYTES).order(ByteOrder.nativeOrder()).asIntBuffer()
    }
}

