package GameObject

import processing.core.PApplet
import processing.core.PGraphics
import java.util.*

interface GameObject{
    val objectType: ObjectType
    val objectData: Objects?

    fun updateData() : GameObject
    fun updatePos()
    fun checkRemoval()
    fun draw(frame: PGraphics)
}

interface BodyObject : GameObject{
    var posX : Float
    var posY : Float
    var morton: Int
    var deleteFlag : Boolean
}

interface BulletObjectManager : GameObject{
    fun start(app : PApplet)
}

interface BulletObject{
    var posX : Float
    var posY : Float
    var newMorton : Int
    var oldMorton : Int
    var deleteFlag : Boolean

    fun collisionDetection(bodyObject: BodyObject)
}

enum class ObjectType{
    PLAYER,
    PLAYER_BULLET,
    ENEMY,
    BOSS_ENEMY,
    ENEMY_BULLET
}