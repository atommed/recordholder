package util

import java.security.SecureRandom

class ThreadLocalSecureRandom {
  private val self = new ThreadLocal[SecureRandom] {
    override def initialValue(): SecureRandom = new SecureRandom()
  }

  def nextBytes(count: Int): Array[Byte] = {
    val arr = new Array[Byte](count)
    nextBytes(arr)
    arr
  }

  def nextBytes(bytes: Array[Byte]) = self.get().nextBytes(bytes)
}
