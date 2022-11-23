package com.hairysnow.play.integrity.api.core.exception

/**
 * @author Jam 2022/11/23
 * Description : Google Play Integrity 异常
 */
class IntegrityException(val errorCode: Int, message: String) : Exception(message)