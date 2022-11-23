package com.hairysnow.play.integrity.api.core.result


/**
 * @author Jam 2022/11/23
 * Description : Google Play Integrity结果
 */
class IntegrityResult {
    /*
    * 应用和证书与 Google Play 分发的版本相符(是否为正品应用，是否被篡改过)
    * */
    var isPlayRecognized = false

    /*
    * 应用正在由 Google Play 服务提供支持的 Android 设备上运行
    * */
    var isMeetsDeviceIntegrity = false

    /*
    * 应用正在通过了基本系统完整性检查的设备上运行
    * */
    var isMeetsBasicIntegrity = false

    constructor() {}
    constructor(
        playRecognized: Boolean,
        meetsDeviceIntegrity: Boolean,
        meetsBasicIntegrity: Boolean
    ) {
        isPlayRecognized = playRecognized
        isMeetsDeviceIntegrity = meetsDeviceIntegrity
        isMeetsBasicIntegrity = meetsBasicIntegrity
    }

    override fun toString(): String {
        return "IntegrityResult{" +
                "isPlayRecognized=" + isPlayRecognized +
                ", isMeetsDeviceIntegrity=" + isMeetsDeviceIntegrity +
                ", isMeetsBasicIntegrity=" + isMeetsBasicIntegrity +
                '}'
    }

    companion object {
        fun failed(): IntegrityResult {
            return IntegrityResult()
        }
    }
}