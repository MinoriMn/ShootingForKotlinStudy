//uniform mat4 modelViewProjectionMatrix;
//
//layout(location = 0) in vec2 particle_vert;
//layout(location = 1) in vec3 pos;
//layout(location = 2) in vec4 color;

//=================-=====-=
//        uniform mat4 projection;
//        uniform mat4 modelview;
//
//        uniform float weight;
//
//        in vec4 vertex;//attribute -> in
//        in vec4 color;
//        in vec2 offset;
//        out vec4 vertColor;//varying -> in, out
//        out vec2 texCoord;
//
//        void main() {
//            vec4 pos = modelview * vertex;
//            vec4 clip = projection * pos;
//            gl_Position = clip + projection * vec4(offset, 0, 0);
//            texCoord = (vec2(0.5) + offset / weight);
//            vertColor = color;
//        }

uniform mat4 projection; //プロジェクション行列
uniform mat4 modelview; //モデルビュー行列

uniform float weight;

in vec4 position;
in vec4 color;
in vec2 offset; // is the (2D) displacement from the center point to the edge vertex.

out vec4 vertColor;
out vec2 texCoord;

void main() {
  vec4 pos = modelview * position; //カメラ座標系変換
  vec4 clip = projection * pos; //投影変換

  gl_Position = clip + projection * vec4(offset, 0, 0);//clipで得た投影座標にoffset分追加しているようだが？

  texCoord = (vec2(0.5) + offset / weight);
  vertColor = color;
}