package GameObject

import AppDisplayManager.FRAME_HEIGHT
import AppDisplayManager.FRAME_WIDTH
import pointToMorton
import processing.core.PApplet
import processing.core.PApplet.radians
import processing.core.PConstants
import processing.core.PGraphics
import processing.core.PImage
import processing.opengl.PGraphicsOpenGL
import processing.opengl.PShader
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

data class RoundBullets(private val gameObjectManager: GameObjectManager): BulletObjectManager{
    override val objectType: ObjectType = ObjectType.ENEMY_BULLET

    val bullets : MutableList<RoundBulletData> = Collections.synchronizedList(mutableListOf())

    lateinit var bulletTex : PGraphics
    lateinit var bulletPgo : PGraphicsOpenGL
    lateinit var pointShader: PShader
    lateinit var bulletTexImgs : Array<PImage>

    var a_round = 0

    /*debug*/
    val random = Random()
    var degree = 0

    override fun start(app: PApplet) {
        bulletTex = app.createGraphics(30, 30, PConstants.P2D)
        pointShader = app.loadShader("src/AppDisplayManager/Shader/RoundBullets.frag")
        pointShader.set("center", 15f, 15f)
        pointShader.set("a", 1f)
        pointShader.set("r", 15f)

        bulletTexImgs = Array(90){i -> makeBulletTex(i * 4)}

        bulletPgo = bulletTex as PGraphicsOpenGL
        with(bulletTex){
            beginDraw()
            shader(pointShader)
            noStroke()
            rect(0f, 0f, 30f, 30f)
            endDraw()
        }
    }

    //objectdataから情報管理を行う
    override fun updateData() : GameObjectBase{

        bulletObjSize += bullets.size

        return this
    }


    fun add(posX : Float, posY : Float, degree: Int, halfSize : Float, speed: Float){
        val morton = pointToMorton(posX, posY, halfSize, halfSize)
        val newBullet = RoundBulletData(posX, posY, cos(degree.toFloat())*speed, sin(degree.toFloat())*speed, morton, morton, halfSize)
        bullets.add(newBullet)
        GameObjectManager.enemyBulletsMorton[morton].add(newBullet)
    }


    var imk = 0
    var ii = 0

    override fun draw(frame: PGraphics) {

        var i = ii++
        synchronized(bullets) {
            val bulletObjIterator = bullets.listIterator()
//            bulletObjIterator.forEachRemaining {
//                frame.tint(frame.color(i++ % 360, 40, 100))
//                frame.image(bulletTexImg, it.posX, it.posY, it.halfSize * 2 + 4, it.halfSize * 2 + 4)
//            }

            imk = (imk + 1) % 90
            frame.beginShape(PConstants.QUADS)
            frame.texture(bulletTexImgs[imk])
            while (bulletObjIterator.hasNext()) {
                val bulletData = bulletObjIterator.next()
                frame.tint(frame.color(i++ % 360, 40, 100))
                val size = bulletData.halfSize + 2
                frame.vertex(bulletData.posX - size, bulletData.posY - size, 0f, 0f)
                frame.vertex(bulletData.posX + size, bulletData.posY - size, 30f, 0f)
                frame.vertex(bulletData.posX + size, bulletData.posY + size, 30f, 30f)
                frame.vertex(bulletData.posX - size, bulletData.posY + size, 0f, 30f)
            }
            frame.endShape()
        }

//        frame.endDraw()
//
//        updateBulletTex()
//
//        frame.beginDraw()
    }

    fun updateBulletTex(){
        a_round = (a_round + 1) % 360
        
            pointShader.set("a", cos(radians(a_round.toFloat())) * 2 + 3)

            with(bulletTex) {
                beginDraw()
                shader(pointShader)
                background(0, 0f)
                rect(0f, 0f, 30f, 30f)
                endDraw()
            }

    }

    fun makeBulletTex(a : Int) : PImage{
            //print(" DD")
            pointShader.set("a", cos(radians(a.toFloat())) * 2 + 3)

            with(bulletTex) {
                beginDraw()
                shader(pointShader)
                background(0, 0f)
                rect(0f, 0f, 30f, 30f)
                endDraw()
            }

        return bulletTex.copy()

    }

    override fun updatePos() {
        bullets.forEach {
            it.posX += it.dx
            it.posY += it.dy
            it.newMorton = pointToMorton(it.posX, it.posY, it.halfSize, it.halfSize)
            if(it.newMorton != it.oldMorton){
                GameObjectManager.enemyBulletsMorton[it.oldMorton].remove(it)
                GameObjectManager.enemyBulletsMorton[it.newMorton].add(it)
            }
            it.oldMorton = it.newMorton
        }
    }

    override fun checkRemoval() {
        synchronized(bullets){
            val bulletObjIterator = bullets.listIterator()
            while (bulletObjIterator.hasNext()) {
                val bullet = bulletObjIterator.next()
                if (bullet.deleteFlag or checkOnExistableArea(bullet.posX, bullet.posY)) {
                    GameObjectManager.enemyBulletsMorton[bullet.oldMorton].remove(bullet)
                    bulletObjIterator.remove()
                }
            }
        }
    }

    fun checkOnExistableArea(posX: Float, posY: Float) : Boolean{
        return posX < 10 || posX > FRAME_WIDTH + 70 || posY < 10 || posY > FRAME_HEIGHT + 70
    }
}

data class RoundBulletData(override var posX : Float, override var posY : Float, var dx : Float, var dy : Float, override var newMorton : Int, override var oldMorton : Int, override val halfSize : Float) : BulletObject {
    override val collisionType = CollisionType.Cycle
    override var deleteFlag: Boolean = false
}