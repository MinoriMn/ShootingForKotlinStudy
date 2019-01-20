package GameObject

import AppDisplayManager.PLAYABLE_FRAME_HEIGHT
import AppDisplayManager.PLAYABLE_FRAME_WIDTH
import pointToMorton
import processing.core.PGraphics
import java.util.*

class EnemyObject(override val objectData: Objects?) : BodyObject{
    /*debug*/
    override var posX = PLAYABLE_FRAME_WIDTH / 2f
    override var posY = PLAYABLE_FRAME_HEIGHT / 2f

    private val halfSize = 5f
    private var HP = 100

    override var morton: Int = pointToMorton(posX, posY, halfSize, halfSize)

    override val objectType: ObjectType = ObjectType.ENEMY
    override var deleteFlag: Boolean = false

    override fun updateData(): GameObjectBase {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updatePos() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkRemoval(a: Int){
        if(HP < 0){

        }
    }

    override fun draw(frame: PGraphics) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}