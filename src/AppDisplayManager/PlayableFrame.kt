package AppDisplayManager

import GameObject.GameObjectManager
import GameObject.PlayerObject
import MORTON_UNITX
import MORTON_UNITY
import processing.core.PConstants
import processing.core.PConstants.P3D
import processing.core.PGraphics
import processing.opengl.PGraphicsOpenGL


class PlayableFrame : GraphicFrame() {
    private val gameObjectManager = GameObjectManager(PlayerObject(null))//managerの生成, playerも生成

    lateinit var framePG : PGraphics
    lateinit var framePGO : PGraphicsOpenGL

    override fun start(app: AppDisplayManager) {
        framePG = app.createGraphics(PLAYABLE_FRAME_WIDTH, PLAYABLE_FRAME_HEIGHT, P3D)
        framePGO = framePG as PGraphicsOpenGL
        gameObjectManager.start(app)
    }
    override fun draw(app: AppDisplayManager) {
        framePGO.beginDraw()
        with(framePGO) {
            background(0x55)

            colorMode(PConstants.HSB, 255f)
            imageMode(PConstants.CENTER)

            translate(-40f, -40f)

            noStroke()
            gameObjectManager.draw(framePGO)

            //drawGrid()

            translate(40f, 40f)
        }

        framePGO.endDraw()
        app.image(framePG, PLAYABLE_START_POSX.toFloat(), PLAYABLE_START_POSY.toFloat())
    }

    fun drawGrid(){
        framePGO.strokeWeight(1f)

        /*debug*/
            framePG.stroke(255F, 170F, 170F)
            for (i in 1..7) {
                framePG.line(
                    (MORTON_UNITX * i).toFloat(),
                    0f,
                    (MORTON_UNITX * i).toFloat(),
                    PLAYABLE_FRAME_HEIGHT + 160f
                )
                framePG.line(
                    0f,
                    (MORTON_UNITY * i).toFloat(),
                    PLAYABLE_FRAME_WIDTH + 160f,
                    (MORTON_UNITY * i).toFloat()
                )
            }
            framePG.stroke(255F, 100F, 100F)
            for (i in 1..3) {
                framePG.line(
                    (MORTON_UNITX * i * 2).toFloat(),
                    0f,
                    (MORTON_UNITX * i * 2).toFloat(),
                    PLAYABLE_FRAME_HEIGHT + 160f
                )
                framePG.line(
                    0f,
                    (MORTON_UNITY * i * 2).toFloat(),
                    PLAYABLE_FRAME_WIDTH + 160f,
                    (MORTON_UNITY * i * 2).toFloat()
                )
            }
    }
}