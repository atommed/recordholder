package io.github.atommed.recordholder.beans

import java.security.SecureRandom
import java.sql.SQLIntegrityConstraintViolationException
import javax.annotation.Resource
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.ejb.Stateless
import javax.persistence.{EntityManager, PersistenceContext}
import javax.transaction.Transactional

import io.github.atommed.recordholder.entities.{DBAuth, User}

/**
  * Created by gregory on 13.11.16.
  */

case class UserExistsException(login: String) extends Exception(s"$login already exists")

@Stateless
class DBAuthBean {
  private val rnd = new SecureRandom()

  @PersistenceContext private var em: EntityManager = _

  @Resource(lookup = "java:/recordholder/config/security/pepper") private var pepper : String = _
  private val KDAlgorithm = "PBKDF2WithHmacSHA1"
  private val iterationCount = 2 << 16
  private val saltLength = 64
  private val hashLength = 128
  private val keyFactory = SecretKeyFactory.getInstance(KDAlgorithm)

  private def genHash(salt:Array[Byte], password: String) : Array[Byte] = {
    val peppered_password = password + pepper
    val keySpec = new PBEKeySpec(peppered_password.toCharArray, salt, iterationCount, hashLength)
    keyFactory.generateSecret(keySpec).getEncoded
  }

  private def nextSalt() : Array[Byte] = {
    val arr = new Array[Byte](saltLength)
    rnd.nextBytes(arr)
    arr
  }

  def authenticate(login: String, password: String) : Option[User] =
    Option(em.find(classOf[DBAuth], login)) flatMap(auth =>
      if(genHash(auth.salt, password).deep == auth.passwordHash.deep) Some(auth.relatedUser)
      else None
      )


  @Transactional(rollbackOn = Array(classOf[UserExistsException]))
  def register(login: String, password: String): User ={
    val salt = nextSalt()
    val hash = genHash(salt, password)
    try{
      val newUser = new User(login)
      em.persist(newUser)
      val newAuthentification = DBAuth(login, hash, salt, newUser)
      em.persist(newAuthentification)
      newUser
    } catch {
      case e: SQLIntegrityConstraintViolationException =>
        throw UserExistsException(login)
    }
  }

}
