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

internal const val PLAYABLE_AD_RESPONSE_CLASS =
    "Lcom/navercorp/game/android/community/data/core/entity/player/ad/PlayableAd${'$'}Response;"

internal const val PLAYABLE_AD_RESPONSE_EVENT_CLASS =
    "Lcom/navercorp/game/android/community/data/core/entity/player/ad/PlayableAd${'$'}Response${'$'}Event;"

internal const val PLAYABLE_AD_RESPONSE_CONTROL_TYPE_CLASS =
    "Lcom/navercorp/game/android/community/data/core/entity/player/ad/PlayableAd${'$'}Response${'$'}ControlType;"

internal const val COMMON_GFP_AD_REQUEST_CLASS =
    "Lcom/navercorp/game/android/community/core/feature/feature/gfp/CommonGfpAdRequest;"

internal const val STREAMING_VOD_END_COMMON_EFFECT_CLASS =
    "Lcom/navercorp/game/android/community/ui/common/ui/overlayplayerend/vod/streaming/effect/StreamingVodEndCommonEffectKt;"

internal const val IMA_IN_STREAM_VIDEO_PLAYER_CONTROLLER_CLASS =
    "Lcom/naver/gfpsdk/mediation/ImaInStreamVideoPlayerController;"

internal const val NDA_IN_STREAM_VIDEO_PLAYER_CONTROLLER_CLASS =
    "Lcom/naver/gfpsdk/internal/mediation/ndavideo/NdaInStreamVideoPlayerController;"

internal const val OUT_STREAM_VIDEO_AD_PLAYBACK_CLASS =
    "Lcom/naver/ads/video/player/OutStreamVideoAdPlayback;"

internal const val IN_STREAM_VIDEO_AD_PLAYBACK_CLASS =
    "Lcom/naver/gfpsdk/adplayer/InStreamVideoAdPlayback;"

internal const val VIDEO_ADS_REQUEST_CLASS =
    "Lcom/naver/ads/video/VideoAdsRequest;"

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

internal object PlayableAdResponseFingerprint : Fingerprint(
    returnType = "Ljava/lang/String;",
    strings = listOf(
        "Response(id=",
        ", _event=",
        ", adCount=",
        ", _adControlType="
    ),
    custom = { method, classDef ->
        classDef.type == PLAYABLE_AD_RESPONSE_CLASS &&
            method.parameterTypes.isEmpty()
    }
)

internal object CommonGfpAdRequestFingerprint : Fingerprint(
    returnType = "Z",
    custom = { method, classDef ->
        classDef.type == COMMON_GFP_AD_REQUEST_CLASS &&
            method.name == "isValid" &&
            method.parameterTypes.isEmpty()
    }
)

internal object StreamingVodEndCommonEffectFingerprint : Fingerprint(
    returnType = COMMON_GFP_AD_REQUEST_CLASS,
    strings = listOf(
        "aos_vod_chzzk_banner_",
        "chzzk_video",
        "replay",
        "upload"
    ),
    custom = { method, classDef ->
        classDef.type == STREAMING_VOD_END_COMMON_EFFECT_CLASS &&
            method.parameterTypes.size == 6
    }
)

internal object ImaInStreamVideoPlayerControllerFingerprint : Fingerprint(
    returnType = "V",
    custom = { method, classDef ->
        classDef.type == IMA_IN_STREAM_VIDEO_PLAYER_CONTROLLER_CLASS &&
            method.name == "requestAndPlayAds" &&
            method.parameterTypes == listOf("J")
    }
)

internal object NdaInStreamVideoPlayerControllerFingerprint : Fingerprint(
    returnType = "V",
    custom = { method, classDef ->
        classDef.type == NDA_IN_STREAM_VIDEO_PLAYER_CONTROLLER_CLASS &&
            method.name == "requestAds" &&
            method.parameterTypes.size == 3
    }
)

internal object OutStreamVideoAdPlaybackFingerprint : Fingerprint(
    returnType = "V",
    strings = listOf("adsRequest"),
    custom = { method, classDef ->
        classDef.type == OUT_STREAM_VIDEO_AD_PLAYBACK_CLASS &&
            method.parameterTypes.any { it.toString() == VIDEO_ADS_REQUEST_CLASS }
    }
)

internal object InStreamVideoAdPlaybackFingerprint : Fingerprint(
    returnType = "V",
    custom = { method, classDef ->
        classDef.type == IN_STREAM_VIDEO_AD_PLAYBACK_CLASS &&
            method.parameterTypes == listOf(VIDEO_ADS_REQUEST_CLASS)
    }
)
