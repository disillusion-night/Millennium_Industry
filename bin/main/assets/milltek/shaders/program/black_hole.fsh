#version 400

uniform sampler2D DiffuseSampler;
uniform sampler2D blackholedata;
uniform vec2 InSize;
uniform vec2 OutSize;
uniform float Time;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / OutSize;
    vec4 centerData = texelFetch(blackholedata, ivec2(OutSize/2.0), 0);
    vec3 blackHoleCenter = centerData.rgb;
    float radius = texelFetch(blackholedata, ivec2(OutSize/2.0)+ivec2(1,0), 0).r;
    float rotation = texelFetch(blackholedata, ivec2(OutSize/2.0)+ivec2(1,1), 0).g;

    vec2 screenCenter = OutSize / 2.0;
    vec2 fragToCenter = gl_FragCoord.xy - screenCenter;
    float dist = length(fragToCenter);
    float r = radius * OutSize.x * 0.5;
    float strength = smoothstep(r, r*0.7, dist);
    float angle = atan(fragToCenter.y, fragToCenter.x) + rotation * strength;
    float newDist = mix(dist, r, strength*0.7);
    vec2 newPos = screenCenter + vec2(cos(angle), sin(angle)) * newDist;
    vec2 newUV = newPos / OutSize;
    fragColor = texture(DiffuseSampler, newUV);
}

