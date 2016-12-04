package util

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object AuthUtils{
  private val rnd = new SecureRandom()
  private val pepper : String = "testPepper" //TODO: Replace this!
  private val KDAlgorithm = "PBKDF2WithHmacSHA1"
  private val iterationCount = 2 << 16
  private val saltLength = 64
  private val hashLength = 128
  private val keyFactory = SecretKeyFactory.getInstance(KDAlgorithm)

  implicit class RichRandom(seed: SecureRandom){
    def genByes(count: Int) : Array[Byte] ={
      val arr = new Array[Byte](count)
      seed.nextBytes(arr)
      arr
    }
  }

  def genSalt() : Array[Byte] = rnd.genByes(saltLength)

  def genHash(salt:Array[Byte], password: String) : Array[Byte] = {
    val peppered_password = password + pepper
    val keySpec = new PBEKeySpec(peppered_password.toCharArray, salt, iterationCount, hashLength * 8)
    keyFactory.generateSecret(keySpec).getEncoded
  }
}
