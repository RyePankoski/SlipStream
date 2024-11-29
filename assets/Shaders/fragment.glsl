#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;
uniform float u_intensity;

varying vec2 v_texCoord;

void main() {
    // Reduce the intensity and frequency of the wave effect
    vec2 offset = vec2(
    sin(v_texCoord.y * 5.0 + u_time) * (u_intensity / 1000.0),
    0.0
    );

    // Clamp the offset to prevent extreme distortions
    offset = clamp(offset, -0.05, 0.05);

    vec4 color = texture2D(u_texture, v_texCoord + offset);

    // Optional: add a subtle vignette effect
    float vignette = 1.0 - length(v_texCoord - 0.5) * 1.5;
    color.rgb *= vignette;

    gl_FragColor = color;
}
