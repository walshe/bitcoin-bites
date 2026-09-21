# Store listing assets

Assets for the Google Play listing. Not consumed by the build — these are
uploaded by hand in Play Console.

## screenshots/

Four phone screenshots, 1080×1920.

**The size matters.** Play rejects a screenshot whose longest side is more than
twice its shortest side. The emulator's native 1080×2400 is 2.22:1 and would be
refused, so these are captured with the display overridden to 16:9.

To regenerate after a UI change, with an emulator running:

```sh
ADB=/usr/local/share/android-commandlinetools/platform-tools/adb

# 16:9 instead of the native 20:9
$ADB shell wm size 1080x1920 && $ADB shell wm density 420

# clean status bar: fixed clock, full battery, no notification clutter
$ADB shell settings put global sysui_demo_allowed 1
demo() { $ADB shell am broadcast -a com.android.systemui.demo -e $1 >/dev/null; }
demo "command enter"
demo "command clock -e hhmm 0930"
demo "command battery -e level 100 -e plugged false"
demo "command network -e wifi show -e level 4 -e fully true"
demo "command network -e mobile hide"
demo "command notifications -e visible false"

# ... navigate and `adb shell screencap -p /sdcard/s.png` per shot ...

# always restore afterwards, or the emulator stays at the fake size
$ADB shell am broadcast -a com.android.systemui.demo -e command exit
$ADB shell settings put global sysui_demo_allowed 0
$ADB shell wm size reset && $ADB shell wm density reset
```

Jump straight to a given fact rather than tapping through:

```sh
$ADB shell am force-stop dev.emmett.bitcoinfacts.android
$ADB shell am start -n dev.emmett.bitcoinfacts.android/.MainActivity --es factId "<fact-id>"
```

Force-stopping first matters — sending the extra to an already-foregrounded
activity lands on whatever screen it was on instead of the fact.

On the Guided Bites screen, swipe once forward and once back before capturing.
The first-run "Swipe" hint sits on top of the body text until it's been
dismissed, and it looks like a rendering fault in a store image.

## Still needed

- **Feature graphic**, 1024×500. A designed banner, not a screenshot — nothing
  here can stand in for it.
- The 512×512 listing icon can come from `../bitcoin-bites-bunny-clean-assets/`.
