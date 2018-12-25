package GameObject

import processing.core.PApplet
import processing.core.PConstants
import processing.opengl.PGraphicsOpenGL
import kotlin.concurrent.thread

var calFps: Float = 0F  //フレームレート
var bulletObjSize = 0
var bodyObjSize = 0

class GameObjectManager(playerObject: PlayerObject) {
    val enemyBodyObjects : MutableList<BodyObject> =  mutableListOf() //機体ごとのオブジェクトをもつ
    val bulletObjectManager : MutableList<BulletObjectManager> =  mutableListOf() //一つのオブジェクトに出現数情報を格納する
    val playerObject = playerObject

    companion object {
        val enemyBulletsMorton = Array<MutableList<BulletObject>>(87) { mutableListOf()}
        val playerBulletsMorton = Array<MutableList<BulletObject>>(87) { mutableListOf()}

    }

    //=========================更新スレッド==============================
    init {
        //enemyBodyObjects.add(playerObject)
        bulletObjectManager.add(RoundBullets(null, this))

    }

    fun start(app : PApplet){
        playerObject.start(app)
        bulletObjectManager.forEach { it.start(app) }
        threadCalTreat()
    }

    fun updatePos(){

        playerObject.updateData().updatePos()
        enemyBodyObjects.forEach { it.updateData().updatePos() }
        bodyObjSize = enemyBodyObjects.size

        bulletObjSize = 0
        bulletObjectManager.forEach { it.updateData().updatePos() }

        checkRemoval()

        collisionDetection()

        /*debug*/
        //println("s67=${enemyBulletsMorton[67].size} s68=${enemyBulletsMorton[68].size}")
    }


    private fun threadCalTreat(){
        var error: Long = 0
        val fps: Int = 64 //計算スレッドfps
        val idealSleep: Long = ((1000 shl 16) / fps).toLong()
        var oldTime: Long
        var newTime: Long = System.currentTimeMillis() shl 16
        thread {
            while (true){
                oldTime = newTime

                updatePos() // メイン処理

                newTime = System.currentTimeMillis() shl 16
                var sleepTime = idealSleep - (newTime - oldTime) - error // 休止できる時間
                if (sleepTime < 0x20000) sleepTime = 0x20000 // 最低でも2msは休止
                oldTime = newTime
                Thread.sleep(sleepTime shr 16) // 休止

                newTime = System.currentTimeMillis() shl 16
                error = newTime - oldTime - sleepTime // 休止時間の誤差

                upFps()
            }
        }
    }

    private fun checkRemoval(){
        synchronized(enemyBodyObjects) {
            val bodyObjMagIterator = enemyBodyObjects.listIterator()
            bodyObjMagIterator.forEachRemaining { it.checkRemoval() }
        }

        bulletObjectManager.forEach {
            it.checkRemoval()
        }
    }

    //body基準でmorton探索を行う
    private fun collisionDetection(){
        thisAndUpperSearch(playerObject, playerObject.morton, enemyBulletsMorton)

        enemyBodyObjects.forEach {
            thisAndUpperSearch(it, it.morton, playerBulletsMorton)
            val m4 = it.morton * 4
            for (lsm in m4+1..m4+4)lowerSearch(it, lsm, playerBulletsMorton)
        }

    }

    //現在の位と上位探索
    private fun thisAndUpperSearch(bodyObject: BodyObject, searchMorton : Int, bulletsMorton: Array<MutableList<BulletObject>> ){
        val bulletsList = bulletsMorton[searchMorton]

        bulletsList.forEach {
            it.collisionDetection(bodyObject)
        }

        //next
        if(searchMorton != 0) thisAndUpperSearch(bodyObject, (searchMorton - 1) / 4,  bulletsMorton)
    }

    //下位探索
    private fun lowerSearch(bodyObject: BodyObject, searchMorton : Int, bulletsMorton: Array<MutableList<BulletObject>> ){
        if(searchMorton > 21) return

        val bulletsList = bulletsMorton[searchMorton]

        bulletsList.forEach {
            it.collisionDetection(bodyObject)
        }

        //next
        val sm4 = searchMorton * 4
        for (nsm in sm4+1..sm4+4) lowerSearch(bodyObject, nsm, bulletsMorton)
    }

    private var upBasetime: Long = 0   //測定基準時間
    private var upFrameCount: Int = 0      //フレーム数

    private fun upFps() {
        ++upFrameCount        //フレーム数をインクリメント
        val now = System.currentTimeMillis()      //現在時刻を取得
        if (now - upBasetime >= 1000) {       //１秒以上経過していれば
            calFps = (upFrameCount * 1000).toFloat() / (now - upBasetime).toFloat()        //フレームレートを計算
            upBasetime = now     //現在時刻を基準時間に
            upFrameCount = 0          //フレーム数をリセット
        }
    }


    //=========================描画(メイン)スレッド==============================
    fun draw(frame: PGraphicsOpenGL){
//        synchronized(bulletObjectManager) {
//            val bulletObjIterator = bulletObjectManager.listIterator()
//            bulletObjIterator.forEachRemaining { it.draw(frame) }
//        }
        frame.blendMode(PConstants.BLEND)

        frame.fill(255F, 0F, 0F)
        playerObject.draw(frame)

        synchronized(enemyBodyObjects) {
            val bodyObjMagIterator = enemyBodyObjects.listIterator()
            bodyObjMagIterator.forEachRemaining { it.draw(frame) }
        }

        frame.blendMode(PConstants.ADD)
        frame.fill(255F, 255F, 255F)
        bulletObjectManager.forEach {
            it.draw(frame)
        }

    }
}
