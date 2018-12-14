#ifdef GL_ES
precision mediump float;//精度修飾子
precision mediump int;
#endif

uniform sampler2D sprite;

in vec4 vertColor;
in vec2 texCoord;

void main() {
  gl_FragColor = texture2D(sprite, texCoord) * vertColor;
}