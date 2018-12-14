package AppDisplayManager

import GameObject.bodyObjSize
import GameObject.bulletObjSize
import GameObject.calFps
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PImage
import processing.opengl.PShader

//メインウィンドウ
const val WINDOW_WIDTH = 920
const val WINDOW_HEIGHT = 600

//PlayableFrame
const val PLAYABLE_FRAME_WIDTH = 480
const val PLAYABLE_FRAME_HEIGHT = 560
const val PLAYABLE_START_POSX = 20
const val PLAYABLE_START_POSY = 20

class AppDisplayManager : PApplet (){
    companion object {
        var inputCrossKey = 0b00000 //dir: shift, d, r, u, l
    }

    lateinit var frames : Array<GraphicFrame>
    lateinit var shader : PShader
    lateinit var pg : PGraphics
    lateinit var png : PImage

    var weight = 100f

    var minfps = 60f

    fun run(args: Array<String>) : Unit = PApplet.main(AppDisplayManager::class.qualifiedName) //processing起動

    override fun settings() : Unit{
        //TIS = Text Input Sources, TSM = Text Services Manager
        size(WINDOW_WIDTH, WINDOW_HEIGHT, P3D)
        png = loadImage("res/image/particleTest.png")
        frames = arrayOf(PlayableFrame())
    }

    override fun setup() : Unit{
        fill(0xff)
        frameRate(60f)

        pg = createGraphics(height, height, P2D)

        shader = pg.loadShader("src/AppDisplayManager/Shader/Bullets.frag", "src/AppDisplayManager/Shader/Bullets.vert")
        shader.set("weight", weight)

        with(pg) {

        }

        strokeWeight(weight)
        strokeCap(SQUARE)
        stroke(255)

        frames.forEach { it.start(this) }
    }


    override fun draw() : Unit{
        background(0f)
        shader.set("sprite", png)

        /*debug*/
        with(pg){
            beginDraw()

            endDraw()
        }

        image(pg, 0f, 0f)

        shader(shader, POINTS)
        filter(shader)
        blendMode(ADD)
        point(mouseX.toFloat(), mouseY.toFloat())
        point(mouseX.toFloat() + 50, mouseY.toFloat())


        //frames.forEach { it.draw(this) }

        //fps
        fill(0xff)

        val s = arrayOf("bul:$bulletObjSize bod:$bodyObjSize", "cFps:$calFps", "mdFps:$minfps", "dFps:$frameRate")
        for (i in 0 until s.size){
            text(s[i], WINDOW_WIDTH -150f, WINDOW_HEIGHT - 10f * (i + 1))
        }

    }

    override fun keyPressed() {
        //println("Pressed:$keyCode")
        when(keyCode){
            37, 38, 39, 40 ->{
                val keySwitch = 0b1 shl (keyCode - 37)
                inputCrossKey = inputCrossKey or keySwitch
            }

            32 ->{ //space
                minfps = 60f
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
}

