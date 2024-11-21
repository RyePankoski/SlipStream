#version 330 core

in vec2 vTexCoord;
out vec4 fragColor;

uniform sampler2D uTexture;
uniform float uResolution;
uniform float uRadius;

void main() {
    vec4 color = vec4(0.0);
    float weight[5] = float[](0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);
    for (int i = -4; i <= 4; i++) {
        color += texture(uTexture, vTexCoord + vec2(0.0, i) / uResolution) * weight[abs(i)];
    }
    fragColor = color;
}
