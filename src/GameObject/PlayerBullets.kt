package GameObject

import AppDisplayManager.AppDisplayManager.Companion.inputCrossKey
import AppDisplayManager.GAME_SPACE_WIDTH
import pointToMorton
import processing.core.PApplet
import processing.core.PApplet.radians
import processing.core.PConstants
import processing.core.PGraphics
import processing.core.PImage
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class PlayerBullets(playerObject: PlayerObject): BulletObjectManager{

    val bullets : MutableList<PlayerNormalBulletData> = Collections.synchronizedList(mutableListOf())
    val bulletImgs = kotlin.arrayOfNulls<PImage>(3)
    var interval = 60
    val playerObject = playerObject



    override fun start(app: PApplet) {
        val bulletsImgSprite = app.loadImage("res/image/test_player_bullets.png")
        bulletImgs[0] = bulletsImgSprite.get(0,0, 32, 32)
        bulletImgs[1] = bulletsImgSprite.get(32,0, 32, 32)
        bulletImgs[2] = bulletsImgSprite.get(64,0, 32, 32)
    }

    override fun draw(frame: PGraphics) {
        synchronized(bullets) {
            val bulletObjIterator = bullets.listIterator()


            while (bulletObjIterator.hasNext()) {
                val bulletData = bulletObjIterator.next()
                frame.beginShape(PConstants.QUADS)
                val size = bulletData.halfSize
                frame.texture(bulletImgs[bulletData.imageKind])
                frame.vertex(bulletData.posX - size, bulletData.posY - size, 0f, 0f)
                frame.vertex(bulletData.posX + size, bulletData.posY - size, 30f, 0f)
                frame.vertex(bulletData.posX + size, bulletData.posY + size, 30f, 30f)
                frame.vertex(bulletData.posX - size, bulletData.posY + size, 0f, 30f)
                frame.endShape()

            }
        }
    }

    val bulletSpeed = 8f
    val cos90B = cos(radians(90f)) * bulletSpeed
    val sin90B = -sin(radians(90f)) * bulletSpeed
    val cos100B = cos(radians(96f)) * bulletSpeed
    val sin100B = -sin(radians(96f)) * bulletSpeed

    override fun updateData(): GameObjectBase {
        if(inputCrossKey and 0b100000 != 0 && --interval <= 0){
            add(playerObject.posX, playerObject.posY, cos90B, sin90B, 0, 4f)
            add(playerObject.posX + 5f, playerObject.posY, cos90B - 0.5f, sin90B, 0, 4f)
            add(playerObject.posX - 5f, playerObject.posY, cos90B + 0.5f, sin90B, 0, 4f)
            add(playerObject.posX + 5f , playerObject.posY, cos100B, sin100B, 1, 4f)
            add(playerObject.posX - 5f , playerObject.posY, -cos100B, sin100B, 2, 4f)


            interval = 4
        }

        bulletObjSize += bullets.size
        return this
    }

    override fun updatePos() {
        bullets.forEach {
            it.posX += it.dx
            it.posY += it.dy
            it.newMorton = pointToMorton(it.posX, it.posY, it.halfSize, it.halfSize)
            if(it.newMorton != it.oldMorton){
                GameObjectManager.playerBulletsMorton[it.oldMorton].remove(it)
                GameObjectManager.playerBulletsMorton[it.newMorton].add(it)
            }
            it.oldMorton = it.newMorton
        }
    }

    fun add(posX : Float, posY : Float, dx: Float, dy: Float, imageKind: Int, halfSize : Float){
        val morton = pointToMorton(posX, posY, halfSize, halfSize)
        val newBullet = PlayerNormalBulletData(posX, posY, dx, dy, imageKind, morton, morton, halfSize)
        bullets.add(newBullet)
        GameObjectManager.playerBulletsMorton[morton].add(newBullet)
    }

    override fun checkRemoval() {
        synchronized(bullets){
            val bulletObjIterator = bullets.listIterator()
            while (bulletObjIterator.hasNext()) {
                val bullet = bulletObjIterator.next()
                if (bullet.deleteFlag or checkOnExistableArea(bullet.posX, bullet.posY)) {
                    GameObjectManager.playerBulletsMorton[bullet.oldMorton].remove(bullet)
                    bulletObjIterator.remove()
                }
            }
        }
    }

    fun checkOnExistableArea(posX: Float, posY: Float) : Boolean{
        return posX < 10 || posX > GAME_SPACE_WIDTH - 10 || posY < 10 /*|| posY > GAME_SPACE_HEIGHT - 10*/
    }

    override val objectType: ObjectType = ObjectType.PLAYER_BULLET
}

data class PlayerNormalBulletData(override var posX : Float, override var posY : Float, var dx : Float, var dy : Float, val imageKind : Int,  override var newMorton : Int, override var oldMorton : Int, override val halfSize : Float) : BulletObject {
    override val collisionType = CollisionType.Cycle
    override var deleteFlag: Boolean = false
}