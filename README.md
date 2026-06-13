# CHZZK Patch Code Bundle

This folder collects the source files that were relevant to the CHZZK 3.6.2 patch work.

## Main Patch Files

- `patches/src/main/kotlin/app/revanced/patches/chzzk/cheatkey/UnlockCheatKeyPatch.kt`
  - Main Cheat Key/ad/time-machine related bytecode patch.
  - Stable final behavior:
    - Forces local Cheat Key status getters.
    - Forces local time-machine related getters.
    - Forces ad-related getters so live ads are suppressed.
    - Avoids unsafe constructor mutation because that caused app crashes.

- `patches/src/main/kotlin/app/revanced/patches/chzzk/cheatkey/Fingerprints.kt`
  - Fingerprints and class constants used by `UnlockCheatKeyPatch.kt`.
  - Identifies CHZZK classes such as `CheatKeyStatus`, `CheatKeyInfo`, `StreamingLiveStatus`, `StreamingLiveItem`, and `PlayableAd.Data`.

- `patches/src/main/kotlin/app/revanced/patches/chzzk/shared/Constants.kt`
  - CHZZK package/version compatibility.
  - Target version is CHZZK `3.6.2`.

## Existing CHZZK Patches Also Included

- `patches/src/main/kotlin/app/revanced/patches/chzzk/p2p/DisableP2PPatch.kt`
  - Disables CHZZK P2P/grid streaming behavior.

- `patches/src/main/kotlin/app/revanced/patches/chzzk/p2p/P2PFingerprint.kt`
  - Fingerprint support for the P2P patch.

- `patches/src/main/kotlin/app/revanced/patches/chzzk/tongpow/AutoClaimTongPowPatch.kt`
  - Auto-claims CHZZK TongPow rewards.

- `patches/src/main/kotlin/app/revanced/patches/chzzk/tongpow/TongPowFingerprints.kt`
  - Fingerprint support for TongPow patch.

## Build/Script Context

- `patch_apps.sh`
  - Existing helper script.
  - CHZZK version support was updated during this work.

## Analysis Report

- `CHZZK_CHEATKEY_ANALYSIS_REPORT.md`
  - Full technical report of what was analyzed, what worked, what crashed, and why realtime replay still appears server-gated.

## Final Stable APK From This Work

The final tested APK is not copied into this code bundle because the user asked for code only.

Final APK location in the main workspace:

```text
chzzkpatch_v5.apk
```

## Notes

- Ads were successfully removed in user testing.
- Live realtime replay/time-machine still requires server-side Cheat Key entitlement.
- The crashy approach was constructor field mutation; the stable approach is getter and narrow direct-read patching.
