import AppDisplayManager.PLAYABLE_FRAME_HEIGHT
import AppDisplayManager.PLAYABLE_FRAME_WIDTH

//trueは衝突
fun circleCollision(px1 : Float, px2 : Float, py1 : Float, py2 : Float, r1 : Float, r2 : Float) : Boolean{
    val dx = px1 - px2
    val dy = py1 - py2
    val sr = r1 + r2

    if(Math.abs(dx) > sr || Math.abs(dy) > sr ){return false}

    return (dx * dx + dy * dy) < sr * sr
}

val MORTON_UNITX = (PLAYABLE_FRAME_WIDTH + 80) / 8
val MORTON_UNITY = (PLAYABLE_FRAME_HEIGHT + 80) / 8

fun pointToMorton(posX: Float, posY: Float, halfWidth: Float, halfHeight: Float) : Int{
    val lt_mx : Int = ((posX - halfWidth) / MORTON_UNITX).toInt()
    val lt_my : Int = ((posY - halfHeight) / MORTON_UNITY).toInt()
    val rb_mx : Int = ((posX + halfWidth) / MORTON_UNITX).toInt()
    val rb_my : Int = ((posY + halfHeight) / MORTON_UNITY).toInt()

    //左上ltと右下rbのモートン
    val lt_morton = ((lt_mx and 0b001) or ((lt_mx and 0b010) shl 1) or ((lt_mx and 0b100) shl 2)) or (((lt_my and 0b001) shl 1) or ((lt_my and 0b010) shl 2) or ((lt_my and 0b100) shl 3))
    val rb_morton = ((rb_mx and 0b001) or ((rb_mx and 0b010) shl 1) or ((rb_mx and 0b100) shl 2)) or (((rb_my and 0b001) shl 1) or ((rb_my and 0b010) shl 2) or ((rb_my and 0b100) shl 3))

    //所属空間最高位演算
    var m = lt_morton xor rb_morton
    var attachSpace = 0

    while (m != 0) {
        m = m shr 2
        attachSpace++
    }

    val L = 3 - attachSpace
    val I = rb_morton shr (2 * attachSpace)

    val r = ((Math.pow(4.0, L.toDouble())).toInt() - 1) / 3/*4-1*/ + I

    return r
}