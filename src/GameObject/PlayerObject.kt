package GameObject

import AppDisplayManager.AppDisplayManager
import AppDisplayManager.PLAYABLE_FRAME_HEIGHT
import AppDisplayManager.PLAYABLE_FRAME_WIDTH
import pointToMorton
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PImage
import java.util.*

class PlayerObject(override val objectData: Objects?) : BodyObject{
    override var posX = PLAYABLE_FRAME_WIDTH / 2f + 40f
    override var posY = PLAYABLE_FRAME_HEIGHT - 30f + 40f //プレイヤー座標 初期位置

    private val halfSize = 0f

    override val objectType: ObjectType = ObjectType.PLAYER
    override var morton: Int = pointToMorton(posX, posY, halfSize, halfSize)
    override var deleteFlag: Boolean = false

    private val playerSpeed = 3f
    private val spAdjust45deg = 0.7071f //45度系移動スピード調整
    private val spAdjustShift = 0.4f //スニーク調整

    //境界線
    private val ltBorder = halfSize + 40f
    private val rBorder = PLAYABLE_FRAME_WIDTH - halfSize + 40f
    private val bBorder = PLAYABLE_FRAME_HEIGHT - halfSize + 40f

    lateinit var playerImg : PImage

    init {

    }

    fun start(app : PApplet){
        playerImg = app.loadImage("res/image/test_player.png")
    }

    override fun updateData() : GameObject {
        return this
    }
    
    override fun updatePos(){

        var posDx : Float = (((AppDisplayManager.inputCrossKey and 0b0100) shr 2) - (AppDisplayManager.inputCrossKey and 0b0001)).toFloat()
        var posDy : Float = (((AppDisplayManager.inputCrossKey and 0b1000) shr 3) - ((AppDisplayManager.inputCrossKey and 0b0010) shr 1)).toFloat()

        posDx *= playerSpeed
        posDy *= playerSpeed

        //45度系
        if(posDx != 0f && posDy != 0f){
            posDx *= spAdjust45deg
            posDy *= spAdjust45deg
        }

        //Shiftスニーク
        if(AppDisplayManager.inputCrossKey and 0b10000 != 0){
            posDx *= spAdjustShift
            posDy *= spAdjustShift
        }

        posX += posDx
        posY += posDy

        if(posX < ltBorder){
            posX = ltBorder
        }else if (posX >= rBorder){
            posX = rBorder
        }

        if(posY < ltBorder){
            posY = ltBorder
        }else if (posY >= bBorder){
            posY = bBorder
        }

        morton = pointToMorton(posX, posY, halfSize, halfSize)

        /*debug*/
        //println(morton)
    }

    override fun draw(frame: PGraphics) {
        frame.fill(0f, 255f, 255f)
        frame.image(playerImg, posX, posY, (halfSize + 1) * 10, (halfSize + 1) * 10)
        frame.ellipse(posX, posY,(halfSize + 1) * 2, (halfSize + 1) * 2)
    }

    override fun checkRemoval() {

    }
}