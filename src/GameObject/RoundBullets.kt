package GameObject

import AppDisplayManager.PLAYABLE_FRAME_HEIGHT
import AppDisplayManager.PLAYABLE_FRAME_WIDTH
import circleCollision
import pointToMorton
import processing.core.PApplet
import processing.core.PApplet.radians
import processing.core.PConstants.P3D
import processing.core.PGraphics
import processing.core.PImage
import processing.opengl.PGraphicsOpenGL
import processing.opengl.PShader
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

data class RoundBullets(override val objectData: Objects?, private val gameObjectManager: GameObjectManager): BulletObjectManager{
    override val objectType: ObjectType = ObjectType.ENEMY_BULLET

    val bullets : MutableList<RoundBulletData> = Collections.synchronizedList(mutableListOf())

    lateinit var bulletTex : PGraphics
    lateinit var bulletPgo : PGraphicsOpenGL
    lateinit var pointShader: PShader
    lateinit var bulletTexImg : PImage
    var a_round = 0

    /*debug*/
    val random = Random()
    var degree = 0

    override fun start(app: PApplet) {
        bulletTex = app.createGraphics(30, 30, P3D)
        pointShader = app.loadShader("src/AppDisplayManager/Shader/RoundBullets.frag")
        pointShader.set("center", 15f, 15f)
        pointShader.set("a", 1f)
        pointShader.set("r", 15f)

        bulletPgo = bulletTex as PGraphicsOpenGL
        with(bulletTex){
            beginDraw()
            shader(pointShader)
            noStroke()
            fill(random.nextInt(255).toFloat(), random.nextInt(255).toFloat(), 255f, 255f)

            rect(0f, 0f, 30f, 30f)
            endDraw()
        }

        bulletTexImg = bulletTex.get()
    }

    //objectdataから情報管理を行う
    override fun updateData() : GameObject{
        /*debug 1300 obj -> dFPS=60F, when 1600 obj -> dFPS=54F */
        degree = ++degree % 360
        add(PLAYABLE_FRAME_WIDTH / 2f + 40f, PLAYABLE_FRAME_HEIGHT / 2f + 40f, degree.toFloat(), 5f + random.nextFloat() * 3, 1f)
        degree = ++degree % 360
        add(PLAYABLE_FRAME_WIDTH / 2f + 40f, PLAYABLE_FRAME_HEIGHT / 2f + 40f, degree.toFloat(), 5f + random.nextFloat() * 3, 1f)
        degree = ++degree % 360
        add(PLAYABLE_FRAME_WIDTH / 2f + 40f, PLAYABLE_FRAME_HEIGHT / 2f + 40f, degree.toFloat(), 5f + random.nextFloat() * 3, 1f)
        degree = ++degree % 360
        add(PLAYABLE_FRAME_WIDTH / 2f + 40f, PLAYABLE_FRAME_HEIGHT / 2f + 40f, degree.toFloat(), 5f + random.nextFloat() * 3, 1f)

        bulletObjSize += bullets.size

        return this
    }


    fun add(posX : Float, posY : Float, degree: Float, halfSize : Float, speed: Float){
        val morton = pointToMorton(posX, posY, halfSize, halfSize)
        val newBullet = RoundBulletData(posX, posY, cos(degree)*speed, sin(degree)*speed, morton, morton, halfSize)
        bullets.add(newBullet)
        GameObjectManager.enemyBulletsMorton[morton].add(newBullet)
    }


    var i = 0

    override fun draw(frame: PGraphics) {

        i = 0

        synchronized(bullets) {
            val bulletObjIterator = bullets.listIterator()
            bulletObjIterator.forEachRemaining {
                frame.tint(frame.color(i++ % 255, 100, 255))
                frame.image(bulletTexImg, it.posX, it.posY, it.halfSize * 2, it.halfSize * 2)
            }
        }
        frame.endDraw()

        updateBulletTex()
        bulletTexImg = bulletTex.get()

        frame.beginDraw()

    }

    fun updateBulletTex(){
        a_round = (a_round + 1) % 90

        if(a_round % 4 == 0) {
//            pointShader.set("a", random.nextFloat() * 2 + 3)
            pointShader.set("a", sin(radians(a_round.toFloat() * 4)) * 2 + 3)

            with(bulletTex) {
                beginDraw()
                shader(pointShader)
                background(0, 0f)
                rect(0f, 0f, 30f, 30f)
                endDraw()
            }
        }
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
        return posX < 10 || posX > PLAYABLE_FRAME_WIDTH + 70 || posY < 10 || posY > PLAYABLE_FRAME_HEIGHT + 70
    }
}

data class RoundBulletData(override var posX : Float, override var posY : Float, var dx : Float, var dy : Float, override var newMorton : Int, override var oldMorton : Int, val halfSize : Float) : BulletObject {
    override fun collisionDetection(playerObject: BodyObject) {
        val hit = circleCollision(playerObject.posX, posX, playerObject.posY, posY, 0f, halfSize)
        deleteFlag = hit
    }

    override var deleteFlag: Boolean = false
}