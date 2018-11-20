package scalachain.blockchain

import java.security.InvalidParameterException

import scalachain.crypto.Crypto

sealed trait Chain {
  val index: Int
  val hash: String
  val values: List[Transaction]
  val proof: Long
  val timestamp: Long

  def ::(link: Chain): Chain = link match {
    case l:ChainLink => ChainLink(l.index, l.proof, l.values, this.hash, this)
    case _ => throw new InvalidParameterException("Cannot add invalid link to chain")

  }
}

case class ChainLink(index: Int, proof: Long, values: List[Transaction], previousHash: String = "", tail: Chain = EmptyChain, timestamp: Long = System.currentTimeMillis()) extends Chain {
  val hash = Crypto.sha256Hash(this.toJson.toString)
}

case object EmptyChain extends Chain {
  val index = 0
  val hash = "1"
  val values = Nil
  val proof = 100L
  val timestamp = System.currentTimeMillis()
}

object Chain {
  def apply[T](b: Chain*): Chain = {
    if (b.isEmpty) EmptyChain
    else {
      val link = b.head.asInstanceOf[ChainLink]
      ChainLink(link.index, link.proof, link.values, link.previousHash, apply(b.tail: _*))
    }
  }
}