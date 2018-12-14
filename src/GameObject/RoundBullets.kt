package GameObject

import AppDisplayManager.PLAYABLE_FRAME_HEIGHT
import AppDisplayManager.PLAYABLE_FRAME_WIDTH
import pointToMorton
import processing.core.PGraphics
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

data class RoundBullets(override val objectData: Objects?, private val gameObjectManager: GameObjectManager): BulletObjects{
    override val objectType: ObjectType = ObjectType.ENEMY_BULLET

    val bullets : MutableList<RoundBulletData> = Collections.synchronizedList(mutableListOf())

    /*debug*/
    val random = Random()
    var degree = 0

    init {

    }

    //objectdataから情報管理を行う
    override fun updateData(){
        /*debug 1300 obj*/
        degree = ++degree % 360
        add(PLAYABLE_FRAME_WIDTH / 2f + 40f, PLAYABLE_FRAME_HEIGHT / 2f + 40f, degree.toFloat(), 5f + random.nextFloat() * 3, 1f)
        degree = ++degree % 360
        add(PLAYABLE_FRAME_WIDTH / 2f + 40f, PLAYABLE_FRAME_HEIGHT / 2f + 40f, degree.toFloat(), 5f + random.nextFloat() * 3, 1f)
        degree = ++degree % 360
        add(PLAYABLE_FRAME_WIDTH / 2f + 40f, PLAYABLE_FRAME_HEIGHT / 2f + 40f, degree.toFloat(), 5f + random.nextFloat() * 3, 1f)
        degree = ++degree % 360
        add(PLAYABLE_FRAME_WIDTH / 2f + 40f, PLAYABLE_FRAME_HEIGHT / 2f + 40f, degree.toFloat(), 5f + random.nextFloat() * 3, 1f)

        bulletObjSize += bullets.size

    }

    fun add(posX : Float, posY : Float, degree: Float, halfSize : Float, speed: Float){
        bullets.add(RoundBulletData(posX, posY, cos(degree)*speed, sin(degree)*speed, pointToMorton(posX, posY, halfSize, halfSize) , halfSize))
    }

    override fun draw(frame: PGraphics) {
        synchronized(bullets) {
            val bulletObjIterator = bullets.listIterator()
            bulletObjIterator.forEachRemaining {
                frame.ellipse(it.posX, it.posY, it.halfSize, it.halfSize)
            }
        }
    }

    override fun updatePos() {
        bullets.forEach {
            it.posX += it.dx
            it.posY += it.dy
            it.morton = pointToMorton(it.posX, it.posY, it.halfSize, it.halfSize)
        }
    }

    override fun checkRemoval() {
        synchronized(bullets){
            val bulletObjIterator = bullets.listIterator()
            while (bulletObjIterator.hasNext()) {
                val bullet = bulletObjIterator.next()
                if (checkOnExistableArea(bullet.posX, bullet.posY)) {
                    bulletObjIterator.remove()
                }
            }
        }


    }

    fun checkOnExistableArea(posX: Float, posY: Float) : Boolean{
        return posX < 10 || posX > PLAYABLE_FRAME_WIDTH + 70 || posY < 10 || posY > PLAYABLE_FRAME_HEIGHT + 70
    }
}

data class RoundBulletData(var posX : Float, var posY : Float, var dx : Float, var dy : Float, var morton : Int, val halfSize : Float)