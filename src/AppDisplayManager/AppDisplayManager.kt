package AppDisplayManager

import AudioFilePlayer
import GameObject.bodyObjSize
import GameObject.bulletObjSize
import GameObject.calFps
import SE
import processing.core.PApplet
import processing.core.PConstants


//メインウィンドウ
const val WINDOW_WIDTH = 920
const val WINDOW_HEIGHT = 600

//PlayableFrame
const val FRAME_WIDTH = 480
const val FRAME_HEIGHT = 560
const val FRAME_PADDING = 80

const val GAME_SPACE_WIDTH = FRAME_WIDTH + FRAME_PADDING
const val GAME_SPACE_HEIGHT = FRAME_HEIGHT + FRAME_PADDING

const val PLAYABLE_START_POSX = 20f
const val PLAYABLE_START_POSY = 20f

class AppDisplayManager : PApplet (){
    lateinit var frames : Array<GraphicFrame>
    val player = AudioFilePlayer()

    fun run(args: Array<String>) : Unit = PApplet.main(AppDisplayManager::class.qualifiedName) //processing起動

    override fun settings(){
        //TIS = Text Input Sources, TSM = Text Services Manager
        size(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P2D)
        //fullScreen(PConstants.P2D)

        frames = arrayOf(PlayableFrame())
    }

    override fun setup(){
        fill(0xff)
        frameRate(60f)

        hint(PConstants.DISABLE_DEPTH_TEST)
        hint(PConstants.DISABLE_OPENGL_ERRORS)

        frames.forEach { it.start(this) }
    }


    override fun draw(){
        background(255f)


        frames.forEach { it.draw(this) }

        //fps
        fill(0x00)

        val s = arrayOf("bul:$bulletObjSize bod:$bodyObjSize", "cFps:$calFps", "dFps:$frameRate")
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

            16 ->{
                inputCrossKey = inputCrossKey or 0b010000
            }

            90 ->{
                player.playSE(SE.NormalShot)
                //zzzplayer.loopPlaySE(SE.NormalShot, Clip.LOOP_CONTINUOUSLY)

                inputCrossKey = inputCrossKey or 0b100000
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
                inputCrossKey = inputCrossKey and 0b101111
            }

            90 ->{
                inputCrossKey = inputCrossKey and 0b011111
            }
        }
    }

    companion object {
        var inputCrossKey = 0b000000 //dir: z, shift, d, r, u, l
    }
}