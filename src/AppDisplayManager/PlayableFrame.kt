package AppDisplayManager

import GameObject.GameObjectManager
import GameObject.PlayerObject
import MORTON_UNITX
import MORTON_UNITY
import processing.core.PGraphics


class PlayableFrame : GraphicFrame() {
    private val gameObjectManager = GameObjectManager(PlayerObject(null))//managerの生成, playerも生成

    lateinit var framePG : PGraphics

    override fun start(app: AppDisplayManager) {
        framePG = app.createGraphics(PLAYABLE_FRAME_WIDTH, PLAYABLE_FRAME_HEIGHT)
        with(framePG){
            beginDraw()
            background(0xaa)
            endDraw()
        }
    }
    override fun draw(app: AppDisplayManager) {
        with(framePG){
            beginDraw()
            background(0xff)

            /*debug*/
            framePG.stroke(255F, 170F, 170F)
            translate(-40f, -40f)
            for(i in 1..7){
                framePG.line((MORTON_UNITX * i).toFloat(), 0f, (MORTON_UNITX * i).toFloat(), PLAYABLE_FRAME_HEIGHT + 160f)
                framePG.line(0f, (MORTON_UNITY * i).toFloat(), PLAYABLE_FRAME_WIDTH + 160f, (MORTON_UNITY * i).toFloat())
            }
            framePG.stroke(255F, 100F, 100F)
            for(i in 1..3){
                framePG.line((MORTON_UNITX * i * 2).toFloat(), 0f, (MORTON_UNITX * i * 2).toFloat(), PLAYABLE_FRAME_HEIGHT + 160f)
                framePG.line(0f, (MORTON_UNITY * i * 2).toFloat(), PLAYABLE_FRAME_WIDTH + 160f, (MORTON_UNITY * i * 2).toFloat())
            }
            framePG.stroke(0F)


            gameObjectManager.draw(framePG)
            translate(40f, 40f)
            endDraw()
        }
        app.image(framePG, PLAYABLE_START_POSX.toFloat(), PLAYABLE_START_POSY.toFloat())
    }
}