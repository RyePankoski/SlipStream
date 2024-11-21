#version 100

precision mediump float;

uniform sampler2D u_texture;
uniform float u_resolution;
uniform float u_radius;

varying vec2 v_texCoord;

void main() {
    vec4 color = vec4(0.0);
    float weight[5] = float[](0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);
    for (int i = -4; i <= 4; i++) {
        color += texture2D(u_texture, v_texCoord + vec2(0.0, i) / u_resolution) * weight[abs(i)];
    }
    gl_FragColor = color;
}
