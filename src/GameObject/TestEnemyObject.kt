package GameObject

import AppDisplayManager.GAME_SPACE_WIDTH
import pointToMorton
import processing.core.PApplet.radians
import processing.core.PGraphics
import java.util.*
import kotlin.math.sin

class TestEnemyObject(override var posX : Float, override var posY : Float, pos_deg : Float, override val objectData: Objects?, override val gameObjectManager: GameObjectManager) :EnemyBody{
    /*debug*/
    override val halfSize = 20f
    private var HP = 100

    override var morton: Int = pointToMorton(posX, posY, halfSize, halfSize)

    override val objectType: ObjectType = ObjectType.ENEMY
    override val collisionType: CollisionType = CollisionType.Cycle
    override var deleteFlag: Boolean = false

    private var pos_deg = pos_deg
    private var bul_deg = 0

    private val random = Random()

    override fun updateData(): GameObjectBase {
        for (i in 0..3){
            gameObjectManager.roundBullets.add(posX, posY, bul_deg, 0.3f + random.nextFloat() * 1f, 1f)
            bul_deg++
        }
        bul_deg %= 360

        return this
    }

    override fun updatePos() {
        pos_deg = (pos_deg + 1) % 360

        posX = GAME_SPACE_WIDTH / 2f + 40f * sin(radians(pos_deg.toFloat()))

        morton = pointToMorton(posX, posY, halfSize, halfSize)
    }

    override fun shouldDestroyed(): Boolean {
        return HP < 0
    }

    override fun hitBullet() {
        HP -= 1
    }

    override fun draw(frame: PGraphics) {
        frame.fill(0xaa)
        frame.rect(posX - halfSize, posY - halfSize, halfSize * 2, halfSize * 2)
    }
}