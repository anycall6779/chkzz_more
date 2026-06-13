package app.revanced.patches.chzzk.cheatkey

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import app.morphe.util.getReference
import app.morphe.util.returnEarly
import app.revanced.patches.chzzk.shared.Constants.COMPATIBILITY_CHZZK
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

@Suppress("unused")
val disableReplayAdsPatch = bytecodePatch(
    name = "Disable Replay Ads",
    description = "Disables CHZZK replay and player ad start responses.",
) {
    compatibleWith(COMPATIBILITY_CHZZK)

    execute {
        var patchedDataMethods = 0

        PlayableAdDataFingerprint.classDef.methods
            .filter { method ->
                method.isFieldGetter(
                    opcode = Opcode.IGET_BOOLEAN,
                    fieldType = "Z",
                    definingClass = PLAYABLE_AD_DATA_CLASS,
                    fieldNames = setOf("adMonetizationAvailability")
                )
            }
            .forEach { method ->
                method.returnEarly(false)
                patchedDataMethods++
            }

        PlayableAdDataFingerprint.classDef.methods
            .filter { method ->
                method.isFieldGetter(
                    opcode = Opcode.IGET,
                    fieldType = "I",
                    definingClass = PLAYABLE_AD_DATA_CLASS,
                    fieldNames = setOf("adCount")
                )
            }
            .forEach { method ->
                method.returnEarly(0)
                patchedDataMethods++
            }

        PlayableAdDataFingerprint.classDef.methods
            .filter { method ->
                method.isFieldGetter(
                    opcode = Opcode.IGET_WIDE,
                    fieldType = "J",
                    definingClass = PLAYABLE_AD_DATA_CLASS,
                    fieldNames = setOf("adStartDelaySec")
                )
            }
            .forEach { method ->
                method.returnEarly(0L)
                patchedDataMethods++
            }

        if (patchedDataMethods == 0) {
            throw PatchException("Could not find PlayableAd.Data ad getters.")
        }

        var patchedResponseMethods = 0

        PlayableAdResponseFingerprint.classDef.methods
            .filter { method ->
                method.isFieldGetter(
                    opcode = Opcode.IGET,
                    fieldType = "I",
                    definingClass = PLAYABLE_AD_RESPONSE_CLASS,
                    fieldNames = setOf("adCount")
                )
            }
            .forEach { method ->
                method.returnEarly(0)
                patchedResponseMethods++
            }

        PlayableAdResponseFingerprint.classDef.methods
            .filter { method ->
                method.name == "o" &&
                    method.definingClass == PLAYABLE_AD_RESPONSE_CLASS &&
                    method.returnType == "Z" &&
                    method.parameterTypes.isEmpty()
            }
            .forEach { method ->
                method.returnEarly(false)
                patchedResponseMethods++
            }

        PlayableAdResponseFingerprint.classDef.methods
            .filter { method ->
                method.definingClass == PLAYABLE_AD_RESPONSE_CLASS &&
                    method.returnType == PLAYABLE_AD_RESPONSE_EVENT_CLASS &&
                    method.parameterTypes.isEmpty()
            }
            .forEach { method ->
                method.returnStaticObjectEarly(
                    className = PLAYABLE_AD_RESPONSE_EVENT_CLASS,
                    fieldName = "UNKNOWN",
                    fieldType = PLAYABLE_AD_RESPONSE_EVENT_CLASS
                )
                patchedResponseMethods++
            }

        PlayableAdResponseFingerprint.classDef.methods
            .filter { method ->
                method.definingClass == PLAYABLE_AD_RESPONSE_CLASS &&
                    method.returnType == PLAYABLE_AD_RESPONSE_CONTROL_TYPE_CLASS &&
                    method.parameterTypes.isEmpty()
            }
            .forEach { method ->
                method.returnStaticObjectEarly(
                    className = PLAYABLE_AD_RESPONSE_CONTROL_TYPE_CLASS,
                    fieldName = "UNKNOWN",
                    fieldType = PLAYABLE_AD_RESPONSE_CONTROL_TYPE_CLASS
                )
                patchedResponseMethods++
            }

        if (patchedResponseMethods == 0) {
            throw PatchException("Could not find PlayableAd.Response ad methods.")
        }

        var patchedGfpMethods = 0

        CommonGfpAdRequestFingerprint.classDef.methods
            .filter { method ->
                method.definingClass == COMMON_GFP_AD_REQUEST_CLASS &&
                    method.returnType == "Z" &&
                    method.parameterTypes.isEmpty() &&
                    method.name in setOf("a", "c", "f", "isValid")
            }
            .forEach { method ->
                method.returnEarly(false)
                patchedGfpMethods++
            }

        StreamingVodEndCommonEffectFingerprint.classDef.methods
            .filter { method ->
                method.definingClass == STREAMING_VOD_END_COMMON_EFFECT_CLASS &&
                    method.returnType == COMMON_GFP_AD_REQUEST_CLASS &&
                    method.parameterTypes.size == 6
            }
            .forEach { method ->
                method.returnNullObjectEarly()
                patchedGfpMethods++
            }

        if (patchedGfpMethods == 0) {
            throw PatchException("Could not find VOD GFP ad methods.")
        }

        var patchedVideoSdkMethods = 0

        ImaInStreamVideoPlayerControllerFingerprint.classDef.methods
            .filter { method ->
                method.definingClass == IMA_IN_STREAM_VIDEO_PLAYER_CONTROLLER_CLASS &&
                    method.returnType == "V" &&
                    (
                        method.name == "requestAndPlayAds" ||
                            method.name == "start"
                        )
            }
            .forEach { method ->
                method.completeAdsAndReturnEarly(IMA_IN_STREAM_VIDEO_PLAYER_CONTROLLER_CLASS)
                patchedVideoSdkMethods++
            }

        NdaInStreamVideoPlayerControllerFingerprint.classDef.methods
            .filter { method ->
                method.definingClass == NDA_IN_STREAM_VIDEO_PLAYER_CONTROLLER_CLASS &&
                    method.returnType == "V" &&
                    (
                        method.name == "requestAds" ||
                            method.name == "start"
                        )
            }
            .forEach { method ->
                method.completeAdsAndReturnEarly(NDA_IN_STREAM_VIDEO_PLAYER_CONTROLLER_CLASS)
                patchedVideoSdkMethods++
            }

        OutStreamVideoAdPlaybackFingerprint.classDef.methods
            .filter { method ->
                method.definingClass == OUT_STREAM_VIDEO_AD_PLAYBACK_CLASS &&
                    method.returnType == "V" &&
                    method.parameterTypes.any { it.toString() == VIDEO_ADS_REQUEST_CLASS }
            }
            .forEach { method ->
                method.returnEarly()
                patchedVideoSdkMethods++
            }

        InStreamVideoAdPlaybackFingerprint.classDef.methods
            .filter { method ->
                method.definingClass == IN_STREAM_VIDEO_AD_PLAYBACK_CLASS &&
                    method.returnType == "V" &&
                    method.parameterTypes.any { it.toString() == VIDEO_ADS_REQUEST_CLASS }
            }
            .forEach { method ->
                method.returnEarly()
                patchedVideoSdkMethods++
            }

        if (patchedVideoSdkMethods == 0) {
            throw PatchException("Could not find video SDK ad request methods.")
        }
    }
}

private fun Method.isFieldGetter(
    opcode: Opcode,
    fieldType: String,
    definingClass: String,
    fieldNames: Set<String>,
): Boolean {
    if (parameterTypes.isNotEmpty()) return false

    val instructions = implementation?.instructions?.iterator()
        ?: return false

    if (!instructions.hasNext()) return false
    val fieldGet = instructions.next()
    if (!instructions.hasNext()) return false
    val returnValue = instructions.next()
    if (instructions.hasNext()) return false

    val fieldReference = fieldGet.getReference<FieldReference>() ?: return false
    val fieldGetRegister = (fieldGet as? TwoRegisterInstruction)?.registerA
        ?: return false
    val returnRegister = (returnValue as? OneRegisterInstruction)?.registerA
        ?: return false

    val returnOpcode = when (opcode) {
        Opcode.IGET_OBJECT -> Opcode.RETURN_OBJECT
        Opcode.IGET_WIDE -> Opcode.RETURN_WIDE
        else -> Opcode.RETURN
    }

    return fieldGet.opcode == opcode &&
        returnValue.opcode == returnOpcode &&
        fieldReference.definingClass == definingClass &&
        fieldReference.type == fieldType &&
        fieldReference.name in fieldNames &&
        fieldGetRegister == returnRegister
}

private fun MutableMethod.returnStaticObjectEarly(
    className: String,
    fieldName: String,
    fieldType: String,
) {
    addInstructions(
        0,
        """
            sget-object v0, $className->$fieldName:$fieldType
            return-object v0
        """.trimIndent()
    )
}

private fun MutableMethod.returnNullObjectEarly() {
    addInstructions(
        0,
        """
            const/4 v0, 0x0
            return-object v0
        """.trimIndent()
    )
}

private fun MutableMethod.completeAdsAndReturnEarly(className: String) {
    addInstructions(
        0,
        """
            invoke-direct {p0}, $className->onAllAdsCompleted()V
            return-void
        """.trimIndent()
    )
}
