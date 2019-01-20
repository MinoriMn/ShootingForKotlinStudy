

import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import javax.sound.sampled.*
import kotlin.system.exitProcess

class AudioFilePlayer {
    private val lineListener = LineListener { e ->
        if(e.type == LineEvent.Type.STOP){
            resetClip(e.source as Clip)
        }
    }

    private val soundEffects = hashMapOf(
        SE.NormalShot to createClip(File("res/sound/normal_shot.wav"))
    )

    private val shotSounds = Array(4){createClip(File("res/sound/normal_shot.wav"))}
    private var shotShoudIndex = 0

    init{
        soundEffects.forEach { se, clip ->  clip.addLineListener(this)}
        shotSounds.forEach { clip -> clip.addLineListener(this) }
    }

    fun createClip(path: File): Clip {
        //指定されたURLのオーディオ入力ストリームを取得
        try {
            AudioSystem.getAudioInputStream(path).use { ais ->

                //ファイルの形式取得
                val af = ais.format

                //単一のオーディオ形式を含む指定した情報からデータラインの情報オブジェクトを構築
                val dataLine = DataLine.Info(Clip::class.java, af)

                //指定された Line.Info オブジェクトの記述に一致するラインを取得
                val c = AudioSystem.getLine(dataLine) as Clip

                //再生準備完了
                c.open(ais)

                return c
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: UnsupportedAudioFileException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: LineUnavailableException) {
            e.printStackTrace()
        }
        exitProcess(-6)
    }

    fun playSE(se : SE){
        when(se){
            SE.NormalShot -> {
                shotShoudIndex = (shotShoudIndex + 1) % shotSounds.size
                shotSounds[shotShoudIndex].start()
            }

            else -> soundEffects[se]!!.start()
        }

    }

    fun loopPlaySE(index : SE, count : Int){
        soundEffects[index]!!.loop(count)
    }

    fun stopSE(index : SE){
        val clip = soundEffects[index]!!

        clip.stop()
        clip.flush()
        clip.framePosition = 0
    }

    fun stopSE(clip: Clip){
        clip.stop()
        clip.flush()
        clip.framePosition = 0
    }

    fun resetClip(index: SE){
        val clip = soundEffects[index]!!

        clip.flush()
        clip.framePosition = 0

//        lastFramePositions[index] = 0
    }

    fun resetClip(clip: Clip){
        clip.flush()
        clip.framePosition = 0
    }
}

private fun Clip.addLineListener(audioFilePlayer: AudioFilePlayer) {

}

enum class SE{
    NormalShot,
    Bomb
}

