package GameObject

import processing.core.PGraphics
import java.util.*

interface GameObject{
    val objectType: ObjectType
    val objectData: Objects?

    fun updateData()
    fun updatePos()
    fun checkRemoval()
    fun draw(frame: PGraphics)
}

interface BodyObject : GameObject{

}

interface BulletObjects : GameObject{

}

enum class ObjectType{
    PLAYER,
    PLAYER_BULLET,
    ENEMY,
    BOSS_ENEMY,
    ENEMY_BULLET
}