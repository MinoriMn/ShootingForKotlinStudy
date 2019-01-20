package GameObject

import AppDisplayManager.AppDisplayManager
import AppDisplayManager.FRAME_PADDING
import AppDisplayManager.GAME_SPACE_HEIGHT
import AppDisplayManager.GAME_SPACE_WIDTH
import pointToMorton
import processing.core.PApplet
import processing.core.PGraphics
import processing.core.PImage

class PlayerObject() : BodyObject{
    override var posX = GAME_SPACE_WIDTH / 2f
    override var posY = GAME_SPACE_HEIGHT - 30f - FRAME_PADDING / 2f //プレイヤー座標 初期位置

    override val halfSize = 0f

    override val objectType: ObjectType = ObjectType.PLAYER
    override val collisionType: CollisionType = CollisionType.Cycle
    override var morton: Int = pointToMorton(posX, posY, halfSize, halfSize)

    private val playerSpeed = 3f
    private val spAdjust45deg = 0.7071f //45度系移動スピード調整
    private val spAdjustShift = 0.4f //スニーク調整

    //境界線
    private val ltBorder = halfSize + FRAME_PADDING / 2f
    private val rBorder = GAME_SPACE_WIDTH - halfSize - FRAME_PADDING / 2f
    private val bBorder = GAME_SPACE_HEIGHT - halfSize - FRAME_PADDING / 2f

    val playerImg = kotlin.arrayOfNulls<PImage>(10)


    init {

    }

    fun start(app : PApplet){
        val playerImgSprite = app.loadImage("res/image/player_reimu.png")
        playerImg[0] = playerImgSprite.get(0, 0, 48, 48)
        playerImg[8] = playerImgSprite.get(69, 167, 6, 6)
    }

    override fun updateData() : GameObjectBase {
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
        frame.noTint()
        frame.image(playerImg[0], posX, posY, 30f, 30f)
        frame.image(playerImg[8], posX, posY, 6f, 6f)
    }

    override fun hitBullet() {
        println("hit")
    }

}