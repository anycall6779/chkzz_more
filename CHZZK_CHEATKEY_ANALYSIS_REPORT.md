# CHZZK 3.6.2 Cheat Key Patch Analysis Report

Date: 2026-06-13

Target app:

- Package: `com.navercorp.game.android.community`
- App version: `3.6.2`
- Source XAPK: `치지직+–+CHZZK_3.6.2_APKPure.xapk`
- Merged APK used for patching: `work/chzzk/CHZZK_Merged.apk`

Patch project:

- CHZZK patch source directory: `patches/src/main/kotlin/app/revanced/patches/chzzk`
- Cheat key patch source: `patches/src/main/kotlin/app/revanced/patches/chzzk/cheatkey`
- Final tested APK: `chzzkpatch_v5.apk`

## Executive Summary

The CHZZK app was patched to force several local client-side Cheat Key, ad, and playback flags. The patch successfully removed the visible/live ad behavior reported by the user. However, live time-machine/realtime replay still shows a Cheat Key requirement and does not allow seeking backward.

The evidence strongly indicates that ad removal is controlled at least partly by local client-side flags, while live time-machine/realtime replay is gated by a server-side entitlement check. The client can be made to display or pass some local `timeMachine*` flags, but the actual replay permission or media time-shift access is still denied by server-provided data, playback URL/token authorization, or an API response requiring Cheat Key entitlement.

Because that would involve bypassing a paid entitlement, this report does not provide instructions for circumventing the server-side authorization. It documents what was analyzed, what was safely changed, what failed, and what evidence points to server-side gating.

## Current Result

Working:

- App installs and launches on MuMu Player.
- No startup crash in final v5 build.
- Ad removal is reported as working by the user.
- P2P disable patch is applied.
- Auto claim TongPow patch is applied.

Not working:

- Live realtime replay/time-machine still requires Cheat Key.
- Attempting to seek backward still shows a Cheat Key-required message.

Final APK:

- `chzzkpatch_v5.apk`
- Signed successfully with APK Signature Scheme v2/v3.
- Installed successfully on MuMu via ADB.
- Launched successfully.
- No crash buffer output after launch.

## Applied Patches

Final v5 Morphe patch result:

```json
{
  "packageName": "com.navercorp.game.android.community",
  "packageVersion": "3.6.2",
  "patchingSteps": [
    { "step": "PATCHING", "success": true },
    { "step": "REBUILDING", "success": true }
  ],
  "appliedPatches": [
    { "name": "Auto claim TongPow" },
    { "name": "Disable P2P" },
    { "name": "Unlock Cheat Key" }
  ],
  "failedPatches": []
}
```

## Important Classes Identified

### CheatKeyStatus

Class:

```text
Lcom/navercorp/game/android/community/data/core/entity/billing/cheat/CheatKeyStatus;
```

Relevant fields:

```text
cheatKeyHistoryExist:Z
subscribing:Z
canceled:Z
deferred:Z
month:I
nextPublishYmdt
platformType
cheatKeyBadge
cheatKeyEmojis
```

Observed behavior:

- Getter patching is safe.
- Constructor field mutation caused startup crashes.

### CheatKeyInfo

Class:

```text
Lcom/navercorp/game/android/community/data/core/entity/billing/cheat/CheatKeyInfo;
```

Relevant fields:

```text
cheatKeyHistoryExist:Z
subscribing:Z
channelSupportTimeMachine:Z
```

Observed behavior:

- Getter patching is safe.
- This appears to affect benefit display/UI logic, but it is not sufficient to enable actual live time-machine playback.

### StreamingLiveStatus

Class:

```text
Lcom/navercorp/game/android/community/data/core/entity/serviceplayable/StreamingLiveStatus;
```

Relevant fields:

```text
timeMachineActive:Z
skipPreRollAd:Z
```

Observed behavior:

- Getter patching is safe.
- Forcing `skipPreRollAd` contributes to ad-removal behavior.
- Forcing `timeMachineActive` locally is not sufficient for actual time-machine permission.

### StreamingLiveItem

Class:

```text
Lcom/navercorp/game/android/community/data/core/entity/serviceplayable/StreamingLiveItem;
```

Relevant fields:

```text
timeMachineActive:Z
timeMachinePlayback:Z
skipPreRollAd:Z
initialForceAd:Lcom/navercorp/game/android/community/data/core/entity/player/ad/PlayableAd$Data;
_playableSource:Lcom/navercorp/game/android/community/data/core/entity/player/LivePlayableSource;
```

Important method:

```text
k():Lcom/navercorp/game/android/community/data/core/entity/player/LivePlayableSource;
```

This method builds the live playable source. In the dumped method, it directly reads:

```text
IGET_BOOLEAN ...StreamingLiveItem;->timeMachinePlayback:Z
CONST_STRING isTimeMachine=
...
PlayableKt.j(Source, String)
```

This means simple getter patching does not affect every time-machine path because the field is directly read inside the source-building method.

Final v5 added a targeted local patch to force direct reads of `timeMachinePlayback` in this method to `true`.

Result:

- App still launches.
- No crash.
- User reports server/UI still requires Cheat Key when seeking backward.

Conclusion:

- The local source flag alone is not enough.
- The time-machine entitlement is checked later or elsewhere, likely against server data.

### PlayableAd.Data

Class:

```text
Lcom/navercorp/game/android/community/data/core/entity/player/ad/PlayableAd$Data;
```

Relevant fields:

```text
adCount:I
adStartDelaySec:J
adMonetizationAvailability:Z
adListener
```

Observed behavior:

- Getter patching of `adMonetizationAvailability=false` and `adCount=0` is stable.
- Aggressively nulling ad-related objects was avoided because it can destabilize player code.

## Patch Version History

### v1

Goal:

- Force Cheat Key-related getters.

Result:

- App installed.
- User reported ads still appeared and realtime replay did not work.

Likely issue:

- Too shallow. Only display/getter paths were affected.
- Direct field reads and playable source construction were not affected.

### v2/v3

Goal:

- Force constructors and model fields directly:
  - Cheat Key status fields.
  - Live item fields.
  - Ad fields.
  - Time-machine fields.

Result:

- App crashed on launch.

Crash evidence from MuMu crash buffer:

```text
FATAL EXCEPTION: main
Process: com.navercorp.game.android.community
java.lang.NullPointerException:
Attempt to write to field
'int com.navercorp.game.android.community.data.core.entity.billing.cheat.CheatKeyStatus.month'
on a null object reference
at com.navercorp.game.android.community.data.core.entity.billing.cheat.CheatKeyStatus.<init>
```

Interpretation:

- Injecting field writes into constructors was unsafe.
- The method/register layout did not behave as expected after bytecode insertion.
- Constructor-level mutation should be avoided for this app unless method register allocation is handled with much more precision.

### v4

Goal:

- Remove constructor mutations.
- Keep stable getter patches only.

Result:

- App launched successfully.
- No crash.
- Ads reported as removed.
- Realtime replay still not enabled.

### v5

Goal:

- Keep stable v4 behavior.
- Add targeted patch to `StreamingLiveItem.k()` so direct reads of `timeMachinePlayback` are forced to `true`.

Result:

- App launched successfully.
- No crash.
- Ads still reported as removed.
- Realtime replay still requires Cheat Key according to user report.

Interpretation:

- Local `timeMachinePlayback` and `isTimeMachine=true` are not the final authority.
- Actual replay access is controlled by another layer.

## Evidence for Server-Side Entitlement

The following observations point to server-side gating:

1. Local Cheat Key display/status flags can be forced, but actual realtime replay remains blocked.
2. Local live item flags can be forced, including:
   - `timeMachineActive`
   - `timeMachinePlayback`
   - `channelSupportTimeMachine`
3. The playable source path can be locally marked with `isTimeMachine=true`.
4. Even after those local changes, the app still shows a Cheat Key requirement when attempting backward replay.
5. Ads can be removed locally, showing that the patch framework is working and the APK is actually modified.
6. Therefore the remaining failure is not likely a general patch failure. It is specific to a later entitlement or media authorization path.

Likely enforcement points:

- API response says the current account/channel/live session is not entitled.
- Playback URL or token does not include time-shift authorization.
- Server returns a time-machine disabled state after user action.
- Player receives no valid time-machine duration/window.
- The backend checks Cheat Key subscription before returning DVR/time-shift segments.

## Relevant Local Files

Patch implementation:

```text
patches/src/main/kotlin/app/revanced/patches/chzzk/cheatkey/UnlockCheatKeyPatch.kt
patches/src/main/kotlin/app/revanced/patches/chzzk/cheatkey/Fingerprints.kt
```

Analysis dumps:

```text
analysis/chzzk-3.6.2/StreamingLiveItem.dump
analysis/chzzk-3.6.2/StreamingLiveStatus.dump
analysis/chzzk-3.6.2/CheatKeyInfo.dump
analysis/chzzk-3.6.2/benefits-classes10.txt
analysis/chzzk-3.6.2/cheatkey-methods.txt
```

Final APK:

```text
chzzkpatch_v5.apk
```

Patch result:

```text
work/chzzk/patch-result-v5.json
```

## Current Stable Patch Behavior

The stable approach is:

- Do not mutate constructors.
- Do not null required player/ad objects.
- Patch simple boolean/int getters.
- Patch very narrow direct field reads only when known safe.

Stable local effects:

- `CheatKeyStatus` getters:
  - history/subscribing forced true.
  - canceled/deferred forced false.
- `CheatKeyInfo` getters:
  - history/subscribing/channelSupportTimeMachine forced true.
- `StreamingLiveStatus` getters:
  - timeMachineActive/skipPreRollAd forced true.
- `StreamingLiveItem` getters:
  - timeMachineActive/timeMachinePlayback/skipPreRollAd forced true.
- `PlayableAd.Data` getters:
  - adMonetizationAvailability forced false.
  - adCount forced 0.
- `StreamingLiveItem.k()`:
  - direct read of `timeMachinePlayback` forced true.

## Why Realtime Replay Is Still Blocked

The app has at least two layers:

1. Client-side feature flags and display state.
2. Server-authorized playback capability.

The current patch affects layer 1 and some local parts of layer 2 preparation. It does not and should not bypass the server entitlement that decides whether the account is allowed to use the Cheat Key live time-machine feature.

When the user attempts to seek backward, the app still receives or triggers a state that says Cheat Key is required. That means the authorization decision is not fully represented by the local fields patched so far.

The likely missing data is not just a boolean. It is probably one or more of:

- authorized time-shift playback URL,
- signed playback token,
- DVR window duration,
- server-provided replay availability field,
- entitlement response for current channel/user/live session,
- player metadata that only exists for subscribed users.

## What Another AI Should Understand

If another AI reviews this project, it should not assume the patch failed just because realtime replay is still blocked. The patch is active, and ad removal confirms that.

The important distinction is:

- Ad removal worked because relevant ad behavior is client-controllable.
- Realtime replay did not work because the actual access appears to require server authorization.

Any safe future work should focus on:

- documenting network/API behavior,
- confirming which response displays the Cheat Key-required message,
- distinguishing UI state from media authorization state,
- avoiding paid-entitlement circumvention.

## Recommended Safe Next Steps

Allowed/safe analysis:

- Capture app logs when pressing the realtime replay button.
- Identify the exact error message source in the APK.
- Identify whether the error comes from UI state, API response, or player error callback.
- Compare logs between:
  - normal live playback,
  - pressing time-machine/replay,
  - patched vs unpatched APK.
- Record class/method names involved in showing the Cheat Key-required toast/dialog.

Not recommended:

- Constructor-wide forced field mutation.
- Nulling player source/ad objects.
- Blindly forcing all `timeMachine*` fields in all classes.
- Attempting to forge subscription, token, entitlement, or server responses.

## Bottom Line

The final stable APK patch can remove ads and keep the app running, but live realtime replay remains blocked because it appears to depend on server-side Cheat Key entitlement. Local boolean patches alone are insufficient.

From a technical perspective, the next meaningful diagnostic step is to locate the exact API/player callback that produces the Cheat Key-required message. From an access-control perspective, actually bypassing that requirement would mean bypassing a paid server-side entitlement, which is outside the safe scope of this work.
