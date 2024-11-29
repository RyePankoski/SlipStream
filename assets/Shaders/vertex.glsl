attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec2 v_texCoord;

void main() {
    v_texCoord = vec2(a_texCoord0.x, 1.0 - a_texCoord0.y);
    gl_Position = u_projTrans * a_position;
}
