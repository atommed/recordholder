package util

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object AuthUtils {
  private val rnd = new ThreadLocalSecureRandom
  private val pepper: String = "testPepper"
  //TODO: Replace this!
  private val KDAlgorithm = "PBKDF2WithHmacSHA1"
  private val iterationCount = 2 << 16
  private val saltLength = 64
  private val hashLength = 128
  private val keyFactory = SecretKeyFactory.getInstance(KDAlgorithm)


  def genSalt(): Array[Byte] = rnd.nextBytes(saltLength)

  def genHash(salt: Array[Byte], password: String): Array[Byte] = {
    val peppered_password = password + pepper
    val keySpec = new PBEKeySpec(peppered_password.toCharArray, salt, iterationCount, hashLength * 8)
    keyFactory.generateSecret(keySpec).getEncoded
  }
}
