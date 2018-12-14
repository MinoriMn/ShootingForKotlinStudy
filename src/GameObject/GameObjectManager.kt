package GameObject

import processing.core.PGraphics
import kotlin.concurrent.thread

var calFps: Float = 0F  //フレームレート
var bulletObjSize = 0
var bodyObjSize = 0

class GameObjectManager(playerObject: PlayerObject) {
    val bodyObjects : MutableList<GameObject> =  mutableListOf() //機体ごとのオブジェクトをもつ
    val bulletsObjects : MutableList<GameObject> =  mutableListOf() //一つのオブジェクトに出現数情報を格納する

    //val enemyBulletsMorton : TreeNode<List<GameObject>>( mutableListOf())

    //=========================更新スレッド==============================
    init {
        bodyObjects.add(playerObject)
        bulletsObjects.add(RoundBullets(null, this))
        threadCalTreat()


    }

    fun updatePos(){
//        synchronized(bulletsObjects) {
//            val bulletObjIterator = bulletsObjects.listIterator()
//            bulletObjIterator.forEachRemaining {
//                it.updateData()
//                it.updatePos()
//            }
//        }

        bodyObjects.forEach {
            it.updateData()
            it.updatePos()
        }
        bodyObjSize = bodyObjects.size

        bulletObjSize = 0
        bulletsObjects.forEach {
            it.updateData()
            it.updatePos()
        }

        checkRemoval()

        /*debug*/
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
        synchronized(bodyObjects) {
            val bodyObjIterator = bodyObjects.listIterator()
            bodyObjIterator.forEachRemaining { it.checkRemoval() }
        }

        bulletsObjects.forEach {
            it.checkRemoval()
        }
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
    fun draw(frame: PGraphics){
//        synchronized(bulletsObjects) {
//            val bulletObjIterator = bulletsObjects.listIterator()
//            bulletObjIterator.forEachRemaining { it.draw(frame) }
//        }

        frame.fill(255F, 0F, 0F)
        synchronized(bodyObjects) {
            val bodyObjIterator = bodyObjects.listIterator()
            bodyObjIterator.forEachRemaining { it.draw(frame) }
        }

        frame.fill(255F, 255F, 255F)
        bulletsObjects.forEach {
            it.draw(frame)
        }
    }
}

//    //=========================判定・削除スレッド==============================
//
//
//    private fun threadJudgeTreat(){
//        var error: Long = 0
//        val fps: Int = 60 //判定・削除スレッドfps
//        val idealSleep: Long = ((1000 shl 16) / fps).toLong()
//        var oldTime: Long
//        var newTime: Long = System.currentTimeMillis() shl 16
//        thread {
//            while (true){
//                oldTime = newTime
//
//                updatePos() // メイン処理
//                checkRemoval()
//
//                newTime = System.currentTimeMillis() shl 16
//                var sleepTime = idealSleep - (newTime - oldTime) - error // 休止できる時間
//                if (sleepTime < 0x20000) sleepTime = 0x20000 // 最低でも2msは休止
//                oldTime = newTime
//                Thread.sleep(sleepTime shr 16) // 休止
//
//                newTime = System.currentTimeMillis() shl 16
//                error = newTime - oldTime - sleepTime // 休止時間の誤差
//
//                judgeFps()
//            }
//        }
//    }
//
//    private var judgeBasetime: Long = 0   //測定基準時間
//    private var judgeFrameCount: Int = 0      //フレーム数
//
//    private fun judgeFps() {
//        ++judgeFrameCount        //フレーム数をインクリメント
//        val now = System.currentTimeMillis()      //現在時刻を取得
//        if (now - judgeBasetime >= 1000) {       //１秒以上経過していれば
//            judgeFps = (judgeFrameCount * 1000).toFloat() / (now - judgeBasetime).toFloat()        //フレームレートを計算
//            judgeBasetime = now     //現在時刻を基準時間に
//            judgeFrameCount = 0          //フレーム数をリセット
//        }
//    }