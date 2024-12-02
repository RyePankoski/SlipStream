#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform float u_time;
uniform float u_intensity;

varying vec2 v_texCoord;

// Pseudo-random function
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898,78.233))) * 43758.5453123);
}

void main() {
    // Create static noise effect
    vec2 uv = v_texCoord;

    // Generate noise based on position and time
    float noise = random(uv + u_time);

    // Calculate distortion amount based on intensity
    float distortAmount = noise * u_intensity * 0.1;

    // Apply noise distortion to texture coordinates
    vec2 distortedUV = uv + vec2(distortAmount);

    // Sample the texture with distorted coordinates
    vec4 color = texture2D(u_texture, distortedUV);

    // Mix in some noise to the color itself for a TV static effect
    float staticNoise = random(uv + u_time) * u_intensity * 0.15;
    color.rgb = mix(color.rgb, vec3(staticNoise), u_intensity * 0.3);

    // Add a slight red tint when intensity is high
    color.rgb = mix(color.rgb, vec3(1.0, 0.0, 0.0), u_intensity * 0.2);

    gl_FragColor = color;
}
