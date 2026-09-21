#!/usr/bin/env python3
"""Compose the 1024x500 Play feature graphic from the app's own brand assets."""
from PIL import Image, ImageDraw, ImageFont
import math, pathlib

REPO = pathlib.Path("/Users/Emmett/dev/bitcoin-mobile-app")
OUT = REPO / "store-listing" / "feature-graphic.png"
# The circular mascot the app itself shows on Home, not the launcher-style tile.
MASCOT = REPO / "androidApp" / "src" / "main" / "res" / "drawable-nodpi" / "bunny_mascot.png"

W, H = 1024, 500
BG = (0x12, 0x12, 0x12)
ORANGE = (0xF7, 0x93, 0x1A)
TEXT = (0xF5, 0xF5, 0xF5)
SUBTLE = (0xB0, 0xB0, 0xB0)

TITLE_FONT = "/System/Library/Fonts/Supplemental/Arial Black.ttf"
BODY_FONT = "/System/Library/Fonts/Supplemental/Arial.ttf"

img = Image.new("RGB", (W, H), BG)

# Warm radial glow behind the mascot - same ambient-depth idea as the app's TopGlow.
glow = Image.new("RGB", (W, H), BG)
gd = glow.load()
cx, cy, radius = 270, 250, 330
for y in range(H):
    for x in range(W):
        d = math.hypot(x - cx, y - cy)
        if d < radius:
            t = (1 - d / radius) ** 2 * 0.22
            gd[x, y] = (
                int(BG[0] + (ORANGE[0] - BG[0]) * t),
                int(BG[1] + (ORANGE[1] - BG[1]) * t),
                int(BG[2] + (ORANGE[2] - BG[2]) * t),
            )
img = glow
draw = ImageDraw.Draw(img)

# Mascot, left third
m = Image.open(MASCOT).convert("RGBA")
size = 300
m = m.resize((size, size), Image.LANCZOS)
img.paste(m, (cx - size // 2, cy - size // 2), m)

# Title + tagline, right of the mascot. Play crops the edges of this asset in
# some placements, so the text is sized to leave a real right-hand margin
# rather than filling the full width.
tx, right_margin = 470, 56
title = ImageFont.truetype(TITLE_FONT, 64)
tag = ImageFont.truetype(BODY_FONT, 26)

while draw.textlength("Bitcoin Bites", font=title) > (W - tx - right_margin):
    title = ImageFont.truetype(TITLE_FONT, title.size - 2)

draw.text((tx, 196), "Bitcoin Bites", font=title, fill=TEXT)
rule_w = int(draw.textlength("Bitcoin Bites", font=title))
# Orange rule echoing the app's top-bar accent line
draw.rectangle([tx, 276, tx + rule_w, 280], fill=ORANGE)
draw.text((tx, 302), "Bitcoin Knowledge —", font=tag, fill=SUBTLE)
draw.text((tx, 338), "one bite at a time", font=tag, fill=SUBTLE)

img.save(OUT, "PNG")
print(f"wrote {OUT} ({img.size[0]}x{img.size[1]})")
