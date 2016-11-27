package controllers

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject

import anorm._
import play.api.data._
import play.api.data.Forms._
import play.api.db.Database
import play.api.mvc._

/**
  * Created by gregory on 26.11.16.
  */


class AuthController @Inject()(db: Database) extends Controller{
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

  private def genHash(salt:Array[Byte], password: String) : Array[Byte] = {
    val peppered_password = password + pepper
    val keySpec = new PBEKeySpec(peppered_password.toCharArray, salt, iterationCount, hashLength)
    keyFactory.generateSecret(keySpec).getEncoded
  }

  private def register(login: String, password: String): Unit={
    val salt = rnd.genByes(saltLength)
    val hash = genHash(salt, password)
    db.withConnection(implicit conn=>
      SQL"""
            INSERT INTO db_auth(login,passwordHash,salt) VALUES($login, $hash, $salt)
        """.executeInsert(SqlParser.scalar[String].single) //TODO: Causes exception on h2
    )
  }

  case class AuthData(passwordHash: Array[Byte], salt: Array[Byte])

  private def passCorrect(login:String, password: String): Option[Boolean] = {
    val resOpt = db.withConnection(implicit conn => {
      val parser = Macro.namedParser[AuthData]
      SQL"""
            SELECT passwordHash, salt FROM db_auth WHERE login=$login
        """.as(parser.singleOpt)
    })
    resOpt.map(res=>genHash(res.salt, password) sameElements res.passwordHash)
  }

  val authForm = Form(tuple(
    "login" -> text,
    "password" -> text
  ))

  def finishSuccessfully(status: String,login:String) : Result = {
    val userPair = "user" -> login
    val token = Base64.getEncoder.encodeToString(rnd.genByes(128))
    val tokenPair = "token" -> token
    Ok(status).withSession(userPair, tokenPair).withCookies(Cookie("token",token,httpOnly = false,
      maxAge = Some(60*60*24*31)))
  }

  def auth = Action { implicit request =>
    val (login, password) = authForm.bindFromRequest.get
    passCorrect(login, password) match {
      case Some(true) => finishSuccessfully("Logged in!",login)
      case Some(false) => BadRequest("Invalid login-pass!")
      case None =>
        register(login, password)
        finishSuccessfully("Registered!",login)
    }
  }
}
