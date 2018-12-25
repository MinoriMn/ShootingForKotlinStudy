package AppDisplayManager

import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.core.PImage
import processing.opengl.PGraphicsOpenGL
import processing.opengl.PShader

class ShaderTest2 : PApplet(){
	fun run(args: Array<String>) : Unit = PApplet.main(ShaderTest2::class.qualifiedName) //processing起動

	lateinit var pointShader: PShader
	lateinit var pg: PGraphics
	lateinit var pgo : PGraphicsOpenGL

    lateinit var img : PImage

	override fun settings() {
		size(640, 360, PConstants.P3D)
	}

	override fun setup() {
		pg = createGraphics(height, height, PConstants.P3D)
		pgo = pg as PGraphicsOpenGL

        img = loadImage("res/image/particleTest.png")

        pointShader = loadShader("src/AppDisplayManager/Shader/Bullets.frag", "src/AppDisplayManager/Shader/Bullets.vert")
		//pointShader.set("tex", img)

        with(pgo){
            shader(pointShader, PConstants.POINTS)

        }


	}

    override fun draw() {
        with(pgo) {

            strokeCap(PConstants.SQUARE)

            beginDraw()

            background(0)

            beginShape(PConstants.POINTS)

            strokeWeight(50f)
            attrib("weight", 50f)
            vertex(mouseX.toFloat(), mouseY.toFloat())

            endShape()


            beginShape(PConstants.POINTS)

            strokeWeight(30f)
            attrib("weight", 30f)
            vertex(height/2f, height/2f)

            endShape()

//            strokeWeight(50f)
//            attrib("weight", 50f)
//            point(mouseX.toFloat(), mouseY.toFloat())
//
//            strokeWeight(30f)
//            attrib("weight", 30f)
//            point(height/2f, height/2f )


            endDraw()

        }


        image(pg, 0f, 0f)

    }

}