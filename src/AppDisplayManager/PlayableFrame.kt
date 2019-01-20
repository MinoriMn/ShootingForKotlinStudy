package AppDisplayManager

import GameObject.GameObjectManager
import GameObject.PlayerObject
import MORTON_UNITX
import MORTON_UNITY
import processing.core.PConstants
import processing.core.PGraphics


class PlayableFrame : GraphicFrame() {
    private val gameObjectManager = GameObjectManager(PlayerObject())//managerの生成, playerも生成

    lateinit var framePG : PGraphics

    override fun start(app: AppDisplayManager) {
        framePG = app.createGraphics(FRAME_WIDTH, FRAME_HEIGHT, PConstants.P2D)
        framePG.imageMode(PConstants.CENTER)
        framePG.noStroke()
        gameObjectManager.start(app)
    }
    override fun draw(app: AppDisplayManager) {
        framePG.colorMode(PConstants.HSB, 360f, 100f, 100f)
        framePG.beginDraw()
        with(framePG) {
            background(0f, 0f, 30f)

            translate(-40f, -40f)

            gameObjectManager.draw(framePG)

            //drawGrid()

            //translate(40f, 40f)
        }

        framePG.endDraw()
        app.image(framePG, PLAYABLE_START_POSX, PLAYABLE_START_POSY)
    }

    fun drawGrid(){
        framePG.strokeWeight(1f)

        /*debug*/
            framePG.stroke(255F, 170F, 170F)
            for (i in 1..7) {
                framePG.line(
                    (MORTON_UNITX * i).toFloat(),
                    0f,
                    (MORTON_UNITX * i).toFloat(),
                    FRAME_HEIGHT + 160f
                )
                framePG.line(
                    0f,
                    (MORTON_UNITY * i).toFloat(),
                    FRAME_WIDTH + 160f,
                    (MORTON_UNITY * i).toFloat()
                )
            }
            framePG.stroke(255F, 100F, 100F)
            for (i in 1..3) {
                framePG.line(
                    (MORTON_UNITX * i * 2).toFloat(),
                    0f,
                    (MORTON_UNITX * i * 2).toFloat(),
                    FRAME_HEIGHT + 160f
                )
                framePG.line(
                    0f,
                    (MORTON_UNITY * i * 2).toFloat(),
                    FRAME_WIDTH + 160f,
                    (MORTON_UNITY * i * 2).toFloat()
                )
            }
    }
}