package app.revanced.patches.chzzk.cheatkey

import app.morphe.patcher.Fingerprint

internal const val CHEAT_KEY_STATUS_CLASS =
    "Lcom/navercorp/game/android/community/data/core/entity/billing/cheat/CheatKeyStatus;"

internal const val NAV_PROFILE_CHEAT_KEY_DATA_CLASS =
    "Lcom/navercorp/game/android/community/app/ui/home/profile/detail/cheat/NavProfileCheatKeyComposable${'$'}Data;"

internal const val CHEAT_KEY_INFO_CLASS =
    "Lcom/navercorp/game/android/community/data/core/entity/billing/cheat/CheatKeyInfo;"

internal const val STREAMING_LIVE_STATUS_CLASS =
    "Lcom/navercorp/game/android/community/data/core/entity/serviceplayable/StreamingLiveStatus;"

internal const val STREAMING_LIVE_ITEM_CLASS =
    "Lcom/navercorp/game/android/community/data/core/entity/serviceplayable/StreamingLiveItem;"

internal const val PLAYABLE_AD_DATA_CLASS =
    "Lcom/navercorp/game/android/community/data/core/entity/player/ad/PlayableAd${'$'}Data;"

internal object CheatKeyStatusFingerprint : Fingerprint(
    returnType = "Ljava/lang/String;",
    strings = listOf(
        "CheatKeyStatus(cheatKeyHistoryExist=",
        ", subscribing=",
        ", canceled=",
        ", deferred="
    ),
    custom = { method, classDef ->
        classDef.type == CHEAT_KEY_STATUS_CLASS &&
            method.parameterTypes.isEmpty()
    }
)

internal object CheatKeyInfoFingerprint : Fingerprint(
    returnType = "Ljava/lang/String;",
    strings = listOf(
        "CheatKeyInfo(cheatKeyHistoryExist=",
        ", subscribing=",
        ", channelSupportTimeMachine="
    ),
    custom = { method, classDef ->
        classDef.type == CHEAT_KEY_INFO_CLASS &&
            method.parameterTypes.isEmpty()
    }
)

internal object NavProfileCheatKeyDataFingerprint : Fingerprint(
    returnType = "Ljava/lang/String;",
    strings = listOf(
        "Data(showPurchase=",
        ", clearTop=",
        ", singleTop="
    ),
    custom = { method, classDef ->
        classDef.type == NAV_PROFILE_CHEAT_KEY_DATA_CLASS &&
            method.parameterTypes.isEmpty()
    }
)

internal object StreamingLiveStatusFingerprint : Fingerprint(
    returnType = "Ljava/lang/String;",
    strings = listOf(
        ", timeMachineActive=",
        ", skipPreRollAd="
    ),
    custom = { method, classDef ->
        classDef.type == STREAMING_LIVE_STATUS_CLASS &&
            method.parameterTypes.isEmpty()
    }
)

internal object StreamingLiveItemFingerprint : Fingerprint(
    returnType = "Ljava/lang/String;",
    strings = listOf(
        ", timeMachineActive=",
        ", timeMachinePlayback="
    ),
    custom = { method, classDef ->
        classDef.type == STREAMING_LIVE_ITEM_CLASS &&
            method.parameterTypes.isEmpty()
    }
)

internal object PlayableAdDataFingerprint : Fingerprint(
    returnType = "Ljava/lang/String;",
    strings = listOf(
        "Data(id=",
        ", adRoll=",
        ", adCount=",
        ", adMonetizationAvailability="
    ),
    custom = { method, classDef ->
        classDef.type == PLAYABLE_AD_DATA_CLASS &&
            method.parameterTypes.isEmpty()
    }
)
