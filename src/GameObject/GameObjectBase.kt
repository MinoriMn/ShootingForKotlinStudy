package GameObject

import processing.core.PApplet
import processing.core.PGraphics
import java.util.*

interface GameObjectBase{
    val objectType: ObjectType

    fun updateData() : GameObjectBase
    fun updatePos()
    fun draw(frame: PGraphics)
}

interface BodyObject : GameObjectBase{
    var posX : Float
    var posY : Float
    var morton: Int
    val halfSize : Float
    val collisionType : CollisionType
    fun hitBullet()
}

interface EnemyBody : BodyObject{
    var deleteFlag : Boolean
    val objectData: Objects?
    val gameObjectManager : GameObjectManager

    fun shouldDestroyed() : Boolean
}

interface BulletObjectManager : GameObjectBase{
    fun start(app : PApplet)
    fun checkRemoval()
}

interface BulletObject{
    var posX : Float
    var posY : Float
    var newMorton : Int
    var oldMorton : Int
    val halfSize : Float
    var deleteFlag : Boolean
    val collisionType : CollisionType
}

enum class ObjectType{
    PLAYER,
    PLAYER_BULLET,
    ENEMY,
    BOSS_ENEMY,
    ENEMY_BULLET
}

enum class CollisionType{
    Cycle,
    Rectangle
}