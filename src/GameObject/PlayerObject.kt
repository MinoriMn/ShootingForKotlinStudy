package GameObject

import AppDisplayManager.AppDisplayManager
import AppDisplayManager.PLAYABLE_FRAME_HEIGHT
import AppDisplayManager.PLAYABLE_FRAME_WIDTH
import pointToMorton
import processing.core.PGraphics
import java.util.*

class PlayerObject(override val objectData: Objects?) : BodyObject{

    override val objectType: ObjectType = ObjectType.ENEMY_BULLET

    var playerPosX = PLAYABLE_FRAME_WIDTH / 2f + 40f
    var playerPosY = PLAYABLE_FRAME_HEIGHT - 30f + 40f //プレイヤー座標 初期位置
    private val playerHalfSize = 0f
    var playerMorton = pointToMorton(playerPosX, playerPosY, playerHalfSize, playerHalfSize)
    private val playerSpeed = 3f
    private val spAdjust45deg = 0.7071f //45度系移動スピード調整
    private val spAdjustShift = 0.4f //スニーク調整

    //境界線
    private val ltBorder = playerHalfSize + 40f
    private val rBorder = PLAYABLE_FRAME_WIDTH - playerHalfSize + 40f
    private val bBorder = PLAYABLE_FRAME_HEIGHT - playerHalfSize + 40f

    override fun updateData() {

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

        playerPosX += posDx
        playerPosY += posDy

        if(playerPosX < ltBorder){
            playerPosX = ltBorder
        }else if (playerPosX >= rBorder){
            playerPosX = rBorder
        }

        if(playerPosY < ltBorder){
            playerPosY = ltBorder
        }else if (playerPosY >= bBorder){
            playerPosY = bBorder
        }

        playerMorton = pointToMorton(playerPosX, playerPosY, playerHalfSize, playerHalfSize)

        /*debug*/
        //println(playerMorton)
    }

    override fun draw(frame: PGraphics) {
        frame.ellipse(playerPosX, playerPosY,(playerHalfSize + 1) * 2, (playerHalfSize + 1) * 2)
    }

    override fun checkRemoval() {

    }
}