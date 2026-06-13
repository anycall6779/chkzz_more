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
val unlockCheatKeyPatch = bytecodePatch(
    name = "Unlock Cheat Key",
    description = "Forces CHZZK Cheat Key subscription flags to be enabled.",
) {
    compatibleWith(COMPATIBILITY_CHZZK)

    execute {
        val statusMethods = CheatKeyStatusFingerprint.classDef.methods
            .filter { method ->
                method.isBooleanFieldGetter(
                    definingClass = CHEAT_KEY_STATUS_CLASS,
                    fieldNames = setOf("cheatKeyHistoryExist", "subscribing", "canceled", "deferred")
                )
            }

        if (statusMethods.isEmpty()) {
            throw PatchException("Could not find CheatKeyStatus boolean getters.")
        }

        statusMethods.forEach { method ->
            method.returnEarly(method.booleanGetterFieldName() !in setOf("canceled", "deferred"))
        }

        val cheatKeyInfoMethods = CheatKeyInfoFingerprint.classDef.methods
            .filter { method ->
                method.isBooleanFieldGetter(
                    definingClass = CHEAT_KEY_INFO_CLASS,
                    fieldNames = setOf("cheatKeyHistoryExist", "subscribing", "channelSupportTimeMachine")
                )
            }

        if (cheatKeyInfoMethods.isEmpty()) {
            throw PatchException("Could not find CheatKeyInfo boolean getters.")
        }

        cheatKeyInfoMethods.forEach { method ->
            method.returnEarly(true)
        }

        val navDataMethods = NavProfileCheatKeyDataFingerprint.classDef.methods
            .filter { method ->
                method.isBooleanFieldGetter(
                    definingClass = NAV_PROFILE_CHEAT_KEY_DATA_CLASS,
                    fieldNames = setOf("Z", "a0", "b0")
                )
            }

        if (navDataMethods.isEmpty()) {
            throw PatchException("Could not find NavProfileCheatKeyComposable.Data boolean getters.")
        }

        navDataMethods.forEach { method ->
            method.returnEarly(true)
        }

        val streamingLiveStatusMethods = StreamingLiveStatusFingerprint.classDef.methods
            .filter { method ->
                method.isBooleanFieldGetter(
                    definingClass = STREAMING_LIVE_STATUS_CLASS,
                    fieldNames = setOf("timeMachineActive", "skipPreRollAd")
                )
            }

        if (streamingLiveStatusMethods.isEmpty()) {
            throw PatchException("Could not find StreamingLiveStatus benefit getters.")
        }

        streamingLiveStatusMethods.forEach { method ->
            method.returnEarly(true)
        }

        val streamingLiveItemMethods = StreamingLiveItemFingerprint.classDef.methods
            .filter { method ->
                method.isBooleanFieldGetter(
                    definingClass = STREAMING_LIVE_ITEM_CLASS,
                    fieldNames = setOf("timeMachineActive", "timeMachinePlayback", "skipPreRollAd")
                )
            }

        if (streamingLiveItemMethods.isEmpty()) {
            throw PatchException("Could not find StreamingLiveItem benefit getters.")
        }

        streamingLiveItemMethods.forEach { method ->
            method.returnEarly(true)
        }

        StreamingLiveItemFingerprint.classDef.methods
            .filter { method ->
                method.name == "k" &&
                    method.definingClass == STREAMING_LIVE_ITEM_CLASS &&
                    method.returnType == "Lcom/navercorp/game/android/community/data/core/entity/player/LivePlayableSource;" &&
                    method.parameterTypes.isEmpty()
            }
            .forEach { method ->
                method.forceBooleanFieldReads(
                    definingClass = STREAMING_LIVE_ITEM_CLASS,
                    fieldNames = setOf("timeMachinePlayback"),
                    value = true
                )
            }

    }
}

private fun Method.isBooleanFieldGetter(
    definingClass: String,
    fieldNames: Set<String>,
): Boolean {
    if (parameterTypes.isNotEmpty() || returnType != "Z") return false

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

    return fieldGet.opcode == Opcode.IGET_BOOLEAN &&
        returnValue.opcode == Opcode.RETURN &&
        fieldReference.definingClass == definingClass &&
        fieldReference.type == "Z" &&
        fieldReference.name in fieldNames &&
    fieldGetRegister == returnRegister
}

private fun Method.booleanGetterFieldName(): String? =
    getterFieldReference(Opcode.IGET_BOOLEAN, "Z")?.name

private fun Method.isIntFieldGetter(
    definingClass: String,
    fieldNames: Set<String>,
): Boolean =
    isFieldGetter(Opcode.IGET, "I", definingClass, fieldNames)

private fun Method.isObjectFieldGetter(
    definingClass: String,
    fieldNames: Set<String>,
): Boolean =
    isFieldGetter(Opcode.IGET_OBJECT, null, definingClass, fieldNames)

private fun Method.isFieldGetter(
    opcode: Opcode,
    fieldType: String?,
    definingClass: String,
    fieldNames: Set<String>,
): Boolean {
    val fieldReference = getterFieldReference(opcode, fieldType) ?: return false
    return fieldReference.definingClass == definingClass &&
        fieldReference.name in fieldNames
}

private fun Method.getterFieldReference(
    opcode: Opcode,
    fieldType: String?,
): FieldReference? {
    if (parameterTypes.isNotEmpty()) return null

    val instructions = implementation?.instructions?.iterator()
        ?: return null

    if (!instructions.hasNext()) return null
    val fieldGet = instructions.next()
    if (!instructions.hasNext()) return null
    val returnValue = instructions.next()
    if (instructions.hasNext()) return null

    val fieldReference = fieldGet.getReference<FieldReference>() ?: return null
    val fieldGetRegister = (fieldGet as? TwoRegisterInstruction)?.registerA
        ?: return null
    val returnRegister = (returnValue as? OneRegisterInstruction)?.registerA
        ?: return null

    val returnOpcode = when (opcode) {
        Opcode.IGET_OBJECT -> Opcode.RETURN_OBJECT
        Opcode.IGET_WIDE -> Opcode.RETURN_WIDE
        else -> Opcode.RETURN
    }

    return if (
        fieldGet.opcode == opcode &&
        returnValue.opcode == returnOpcode &&
        (fieldType == null || fieldReference.type == fieldType) &&
        fieldGetRegister == returnRegister
    ) {
        fieldReference
    } else {
        null
    }
}

private fun MutableMethod.forceBooleanFields(
    className: String,
    trueFields: Set<String> = emptySet(),
    falseFields: Set<String> = emptySet(),
) {
    val instructions = buildString {
        appendLine("move-object/from16 v0, p0")
        if (trueFields.isNotEmpty()) {
            appendLine("const/4 v1, 0x1")
            trueFields.forEach { fieldName ->
                appendLine("iput-boolean v1, v0, $className->$fieldName:Z")
            }
        }
        if (falseFields.isNotEmpty()) {
            appendLine("const/4 v1, 0x0")
            falseFields.forEach { fieldName ->
                appendLine("iput-boolean v1, v0, $className->$fieldName:Z")
            }
        }
    }.trim()

    if (instructions.isEmpty()) return

    addInstructions(this.instructions.size - 1, instructions)
}

private fun MutableMethod.forceIntField(
    className: String,
    fieldName: String,
    value: Int,
) {
    addInstructions(
        this.instructions.size - 1,
        """
            move-object/from16 v0, p0
            const/4 v1, 0x${value.toString(16)}
            iput v1, v0, $className->$fieldName:I
        """.trimIndent()
    )
}

private fun MutableMethod.forceLongField(
    className: String,
    fieldName: String,
    value: Long,
) {
    addInstructions(
        this.instructions.size - 1,
        """
            move-object/from16 v0, p0
            const-wide/16 v1, 0x${value.toString(16)}
            iput-wide v1, v0, $className->$fieldName:J
        """.trimIndent()
    )
}

private fun MutableMethod.forceBooleanFieldReads(
    definingClass: String,
    fieldNames: Set<String>,
    value: Boolean,
) {
    val valueLiteral = if (value) "0x1" else "0x0"
    implementation?.instructions
        ?.mapIndexedNotNull { index, instruction ->
            val fieldReference = instruction.getReference<FieldReference>() ?: return@mapIndexedNotNull null
            val destinationRegister = (instruction as? TwoRegisterInstruction)?.registerA
                ?: return@mapIndexedNotNull null

            if (
                instruction.opcode == Opcode.IGET_BOOLEAN &&
                fieldReference.definingClass == definingClass &&
                fieldReference.type == "Z" &&
                fieldReference.name in fieldNames
            ) {
                index to destinationRegister
            } else {
                null
            }
        }
        ?.asReversed()
        ?.forEach { (index, register) ->
            addInstructions(
                index + 1,
                "const/16 v$register, $valueLiteral"
            )
        }
}

private fun MutableMethod.forceNullObjectField(
    className: String,
    fieldName: String,
    fieldType: String,
) {
    addInstructions(
        this.instructions.size - 1,
        """
            move-object/from16 v0, p0
            const/4 v1, 0x0
            iput-object v1, v0, $className->$fieldName:$fieldType
        """.trimIndent()
    )
}
